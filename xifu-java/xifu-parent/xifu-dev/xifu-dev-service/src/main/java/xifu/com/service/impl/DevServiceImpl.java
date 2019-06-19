package xifu.com.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import xifu.com.cache.CacheConstant;
import xifu.com.cache.DevModelsCache;
import xifu.com.client.StationClient;
import xifu.com.dto.ImportExcelDevDTO;
import xifu.com.dto.Kr104Dto;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.*;
import xifu.com.menus.DevTypeEnum;
import xifu.com.menus.ProtocolEnum;
import xifu.com.pojo.StationInfo;
import xifu.com.pojo.devService.*;
import xifu.com.service.DevService;
import xifu.com.utils.ExcelUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备的service层
 * @auth wq on 2019/3/19 11:03
 **/
@Slf4j
@Service
public class DevServiceImpl implements DevService{

    @Autowired
    private DevInfoMapper devInfoMapper;
    @Autowired
    private DevModelVersionMapper devModelVersionMapper;
    @Autowired
    private StationClient stationClient; // 第三方接口的api
    @Autowired
    private DevModelsCache devModelsCache; // 设备相关版本、信号点、设备等等的缓存的模块的类
    @Autowired
    private DevVersionSignalMapper devVersionSignalMapper;
    @Autowired
    private DevSignalMapper devSignalMapper;
    @Autowired
    private DevAlarmModelMapper devAlarmModelMapper;

    /**
     * 点表导入的service层处理
     * @param file
     * @throws Exception
     */
    @Transactional // 统一事务的提交和处理
    @Override
    public void importPoint(MultipartFile file) throws Exception{
        // 1.解析数据
        if (file.getSize() == 0) {
            throw new Exception("点表无数据");
        }
        // 获取文件名
        ExcelUtil excelUtil = new ExcelUtil(file);
        int sheetCount = excelUtil.getSheetCount();
        if (sheetCount < 2) { // 验证sheet页是否足够
            throw new Exception("sheet页不满足点表的需求");
        }
        // 解析的第一页的数据,第一行不解析，从第二行开始解析
        List<List<String>> versionStringList = excelUtil.read(0, 1);
        if (CollectionUtils.isEmpty(versionStringList)) { // 一行关于版本和设备的信息都没有
            throw new Exception("缺少版本信息");
        }
        // 第一行数据
        List<String> firstRow = versionStringList.get(0);
        String rowValueFlag = checkIsNull(1, firstRow); // 验证数据是否正确
        if (!StringUtils.isBlank(rowValueFlag)) {
            throw new Exception(rowValueFlag);
        }
        String protocolStr = firstRow.get(2).trim(); // 协议类型
        ProtocolEnum protocolType = ProtocolEnum.getEnumByCode(protocolStr); // 当前的协议类型
        if(protocolType == null) { // 如果导入的协议类型不支持，不能导入
            throw new Exception("不支持此类(" + protocolStr + ")协议");
        }
        // 协议类型支持之后，就去做对应的协议类型的解析
        switch (protocolType) {
            case P_104: // 104/铁牛数采 协议 有两个sheet页 第一个sheet是版本和设备信息 第二个sheet是设备的信号点信息
                // 验证是和判断是否是扩容
                String stationName = firstRow.get(6);
                StationInfo stationInfo = checkStationInfo(stationName); // 所有的电站名称都应该和第一行的电站名称一致，这里目前没有做验证
                // 解析和验证当前点表的信息，转换为后面需要存储到数据库中的数据的对象
                ImportExcelDevDTO importExcelDevDTO = excelTo104Dto(versionStringList,
                        excelUtil.read(1, 1), stationInfo, excelUtil.getSheetName(0));
                // 判断通管机是否存在
                String tgjModelVersionCode = importExcelDevDTO.getParentModelVersion().getModelVersionCode();
                DevModelVersion cacheTgjDevModel = devModelsCache.getDevVersionByCode(tgjModelVersionCode); // 获取缓存中的数据
                if (cacheTgjDevModel != null) { // 存在版本，就走扩容的线
                    // 根据版本号和设备信息通管机设备信息
                    DevInfo search = new DevInfo();
                    search.setDevName(firstRow.get(5));
                    search.setModelVersionCode(tgjModelVersionCode);
                    DevInfo select = devInfoMapper.selectOne(search);
                    if (select == null) { // 通管机设备不存在
                        throw new Exception("通管机的版本存在，但是通管机的设备不存在，不能扩容，停止解析");
                    }
                    if (!StringUtils.equals(select.getStationCode(), stationInfo.getStationCode())) { // 扩容的电站必须与对应的通管机电站相同
                        throw new Exception("导入的电站不正确，扩容的设备的电站编号="+select.getStationCode()+",当前导入点表的电站编号=" + stationInfo.getStationCode());
                    }
                    importExcelDevDTO.getParentModelVersion().setId(cacheTgjDevModel.getId()); // 设置通管机的版本id
                    importExcelDevDTO.getParentDev().setId(select.getId()); // 设置通管机的设备的id
                    // TODO 扩容
                    // 扩容涉及到的 1.新增了设备  2.老设备新增了信号点 3.新增了版本和设备(有信号点)
                    Kr104Dto kr104Dto = getKr104Dto(importExcelDevDTO);
                    // 对通管机的数据做扩容的处理
                    save104KrDatas(kr104Dto, select.getId(), importExcelDevDTO);
                    // 更新redis的缓存
                    flushRedis(importExcelDevDTO);
                    return;
                }
                // 验证当前的版本是否有存在的
                Set<String> newModelVersionList = importExcelDevDTO.getVersionCodeToSiganlMap().keySet();// 新增的版本信息
                if (CollectionUtils.isEmpty(newModelVersionList)) {
                    throw new Exception("没有任何新的版本需要加入");
                }
                List<String> existVersions = checkExsitsModelVersion(newModelVersionList);
                if(!CollectionUtils.isEmpty(existVersions)) {
                    throw new Exception("有版本已经存在了，不能导入，存在的版本编号为：" + existVersions.stream().collect(Collectors.joining(",")));
                }
                // 判断在当前电站下是否存在相同名称的设备，如果存在，不能新增
                Example devExample = new Example(DevInfo.class);
                // 查询设备的名称，包括通管机设备
                devExample.createCriteria().andIn("devName", importExcelDevDTO.getDevNameToSiganlMap().keySet())
                        .andEqualTo("stationCode", stationInfo.getStationCode());
                List<DevInfo> dbExistDevList = devInfoMapper.selectByExample(devExample);
                if (!CollectionUtils.isEmpty(dbExistDevList)) { // 同一个电站下不能出现相同的设备名称
                    throw new Exception("在相同电站下，点表中的设备名称已经存在了，设备名：" +
                            dbExistDevList.stream().map(d -> d.getDevName()).collect(Collectors.joining(",")));
                }
                // 保存新增通管机的信息
                save104Datas(importExcelDevDTO);
                // 更新redis的缓存
                flushRedis(importExcelDevDTO);
                break;
            case SN_MODBUS: // 上能modbus协议

                break;
            case MQTT: // mqtt协议

                break;
        }
        try {
            if (excelUtil != null) { // 关闭流
                excelUtil.close();
            }
        }catch (Exception e) {
            log.error("关闭流出现异常：", e);
        }
    }

    /**
     * 扩容通管机的信息
     * @param kr104Dto 扩容的数据
     * @param parentId 通管机设备的id
     * @param importExcelDevDTO 扩容的数据信息，主要是为了取设备名称与设备id的关系
     */
    private void save104KrDatas(Kr104Dto kr104Dto, Long parentId, ImportExcelDevDTO importExcelDevDTO) {
        // 1.新增版本
        if (!CollectionUtils.isEmpty(kr104Dto.getNewDevModelList())) { // 版本信息
            devModelVersionMapper.insertList(kr104Dto.getNewDevModelList());
        }
        // 2.新增设备信息
        if (!CollectionUtils.isEmpty(kr104Dto.getNewDevMap())) { // 对设备的新增
            // 有版本存在的就不需要设置版本，如果版本不存在，就需要设置版本
//            Map<String, Long> vNameToIdMap = kr104Dto.getModelVersionCodeToVersionIdMap();
            List<DevInfo> addDevList = new ArrayList<>();
            for (DevInfo d : kr104Dto.getNewDevMap().values()) {
                d.setParentId(parentId);
                if (d.getModelVersionId() == null) {
                    Long vId = importExcelDevDTO.getModelVersionCodeToModelMap().get(d.getModelVersionCode()).getId();
                    if (vId == null) { // 如果找不到就不直接退出，还是继续执行
                        log.error("设备名称没有对应的版本,设备名称=" + d.getDevName());
                        continue;
                    }
                    d.setModelVersionId(vId);
                }
                addDevList.add(d); // 需要新增的设备添加
            }
            if (!CollectionUtils.isEmpty(addDevList)) {
                devInfoMapper.insertList(addDevList);
            }
        }
        // 3.新增版本信号点
        List<DevVersionSignal> newVersionSiganlList = kr104Dto.getNewVersionSiganlList();
        if (!CollectionUtils.isEmpty(newVersionSiganlList)) { // 如果有需要新增的版本信号点信息
            // 设置信号点的版本号
            devVersionSignalMapper.insertList(newVersionSiganlList);
        }
        // 4.新增设备信号点
        Map<String, List<DevSignal>> newSignalMap = kr104Dto.getNewSignalMap();
        if (!CollectionUtils.isEmpty(newSignalMap)) {
            List<DevSignal> addSignalList = new ArrayList<>();
            int count = 0;
            for (Map.Entry<String, List<DevSignal>> entry : newSignalMap.entrySet()){ // 第一层循环的设备
                count ++;
                List<DevSignal> sList = entry.getValue();
                for (DevSignal s : sList) { // 设置信号点的所属设备的名称
                    DevVersionSignal dvs = importExcelDevDTO.getModelCodeToModelSignalMap().get(s.getModelVersionCode()).get(s.getSignalName());
                    if (dvs != null) { // 设置模型id
                        s.setModelId(dvs.getId());
                    }
                    s.setDevId(importExcelDevDTO.getDevNameToDevInfoMap().get(s.getDevName()).getId());
                    addSignalList.add(s);
                }
                if (count % 5 == 0 && !CollectionUtils.isEmpty(addSignalList)) { // 每5个设备批量插入一批设备信号点
                    devSignalMapper.insertList(addSignalList);
                    addSignalList = new ArrayList<>();
                }
            }
            if (!CollectionUtils.isEmpty(addSignalList)) { // 最后一次如果集合中有数据，则需要做保存
                devSignalMapper.insertList(addSignalList);
            }
        }

    }

    /**
     * 保存新增104的版本信息
     * @param importExcelDevDTO
     */
    private void save104Datas(ImportExcelDevDTO importExcelDevDTO) {
        // 下面的几个步骤的顺序不能调，否则会没有保存到数据库，导致设置的数据没有对应的id
        // 1.存储通管机版本信息
        devModelVersionMapper.insertSelective(importExcelDevDTO.getParentModelVersion());
        // 2.存储通管机设备
        DevInfo parentDev = importExcelDevDTO.getParentDev();
        Long parentVersionId = importExcelDevDTO.getParentModelVersion().getId();
        parentDev.setModelVersionId(parentVersionId); // 设置当前设备所属的通管机id
        devInfoMapper.insertSelective(parentDev);
        // 3.存储通管机下挂的设备版本信息
        importExcelDevDTO.setParentVersionId(parentVersionId); // 设置通管机版本下挂的版本信息的父版本id为当前通管机的版本信息
        devModelVersionMapper.insertList(importExcelDevDTO.getChildrenModelVersions()); // 批量保存通管机下挂子版本信息
        // 4.存储通管机下的设备信息
        // 4.1设置设备对应的版本id
        importExcelDevDTO.setChildrenDevsModelVersionId(); // 设置版本和当前设备的父节点的id
        // 4.2保存设备信息
        devInfoMapper.insertList(importExcelDevDTO.getChildrenDevs());
        // TODO 5.信号点模型,这里对所有模型统一入口，不确定是有数据量过大的请求
        List<DevVersionSignal> allDevVersionSignals = importExcelDevDTO.getAllDevVersionSignals();
        if (!CollectionUtils.isEmpty(allDevVersionSignals)) { // 如果有版本告警模型的信号点才新增信息
            devVersionSignalMapper.insertList(allDevVersionSignals);
        }
        // 6.存储设备信号点
        // 6.1设置设备信号的所属设备id和所属的信号点模型的id
        importExcelDevDTO.setDevSignalInfos();
        // 防止信号点过多，这里对每个设备新增5个设备新增一次
        Map<String, List<DevSignal>> devNameToSiganlMap = importExcelDevDTO.getDevNameToSiganlMap();
        List<DevSignal> signalList = new ArrayList<>();
        int count = 0;
        for(Map.Entry<String, List<DevSignal>> entry : devNameToSiganlMap.entrySet()) {
            count ++;
            signalList.addAll(entry.getValue());
            if (count % 5 == 0) { // 每5次新增一次
                devSignalMapper.insertList(signalList);
                signalList = new ArrayList<>();
            }
        }
        if (!CollectionUtils.isEmpty(signalList)) { // 循环的最后一次没有达到5个设备，就需要单独处理新增
            devSignalMapper.insertList(signalList);
        }
        // 7.新增告警模型
        devAlarmModelMapper.insertSelective(importExcelDevDTO.getAlarmList().get(0)); // 只有一个断连的告警
        // 到此全部导入完成
    }

    /**
     * 解析并且验证104版本的信息
     * @param firstSheetList 第一个sheet页的有效数据
     * @param secendSheetList 第二个sheet页的有效数据
     * @param stationInfo 当前的电站信息
     * @param sheetName 第一个sheet页的名称：这个名称作为通管机设备的名称
     * @return
     */
    private ImportExcelDevDTO excelTo104Dto(List<List<String>> firstSheetList, List<List<String>> secendSheetList,
                                            StationInfo stationInfo, String sheetName) {
        ImportExcelDevDTO dto = new ImportExcelDevDTO();
        // 1.解析版本和设备
        // 1.1解析第一行的版本和设备
        pase104VersionModelAndDev(1, firstSheetList.get(0), stationInfo, dto, sheetName, true);
        int versionSize = firstSheetList.size();
        // 1.2从第二行有效数据开始解析
        for (int i = 1; i < versionSize; i++) {
            pase104VersionModelAndDev(i+ 1, firstSheetList.get(i), stationInfo, dto, sheetName, false);
        }
        // 2.解析信号点
        int signalSize = secendSheetList.size();
        for (int i = 0; i < signalSize; i++) {
            parse104Signals(i + 1, secendSheetList.get(i), dto);
        }
        // 3.添加一条设备通讯中断的告警信息
        pasre104Alarm(dto);
        return dto;
    }

    /**
     * 解析设备信息
     * @param index 解析数据当前的行
     * @param list 解析数据的行数据
     * @param stationInfo 电站信息
     * @param dto 需要组装的dto
     * @param sheetName 当前sheet页的名称
     * @param isParent 是否是第一行有效数据，即是通管机版本和通管机设备
     */
    private void pase104VersionModelAndDev(int index,List<String> list,
                                           StationInfo stationInfo,
                                           ImportExcelDevDTO dto,
                                           String sheetName,
                                           boolean isParent) {
        // 1.验证信息
        String errorMsg = checkIsNull(index, list);
        if (StringUtils.isNotBlank(errorMsg)) { // 如果有空的
            throw new RuntimeException(errorMsg);
        }
        Integer devTypeId = DevTypeEnum.getDevTypeIdByName(list.get(0));
        if (devTypeId == null) { // 验证设备类型
            throw new RuntimeException("Sheet 1的第" + index + "行的第1列的设备类型不正确");
        }
        String rowStationName = list.get(6).trim();
        if (!StringUtils.equals(rowStationName, stationInfo.getStationName())) { // 如果电站名称不一致，不要支持新增
            throw new RuntimeException("Sheet 1的第" + index + "行的电站名称和第一行的电站名称不一致");
        }
        String interfaceVersion = list.get(3).trim();
        String modelVersionCode = list.get(1).trim() + "_" + interfaceVersion;
        String protocol = list.get(2).trim(); // 协议类型
        // 1.解析版本信息 devNameToDevInfoMap
        DevModelVersion version = new DevModelVersion();
        Date date = new Date();
        version.setCreateDate(date);
        version.setDevTypeId(DevTypeEnum.getDevTypeIdByName(list.get(0)));
        version.setEnterpriseId(stationInfo.getEnterpriseId());
        version.setInterfaceVersion(interfaceVersion);
        version.setModelVersionCode(modelVersionCode);
        if (isParent) { // 如果是通管机版本
            version.setName(sheetName);
        } else { // 如果通管机下挂的版本
            version.setName(modelVersionCode + "版本");
        }
        version.setProtocolCode(protocol);
        version.setStationCode(stationInfo.getStationCode());
        version.setType(2); // 1.系统导入 2.用户导入，目前都设置为用户导入
        version.setVenderName(list.get(4).trim()); // 厂商名称
        if (isParent) {
            dto.setParentModelVersion(version);
        } else if(!dto.isChildrenVersionIncloudVersion(modelVersionCode)){ // 如果当前的版本没有被包含
            dto.getChildrenModelVersions().add(version);
        }
        if (!dto.getModelVersionCodeToModelMap().containsKey(modelVersionCode)) { // 添加版本编号-->版本实体对象的对应关系
            dto.getModelVersionCodeToModelMap().put(modelVersionCode, version);
        }
        // 2.解析设备信息
        String devName = list.get(5).trim();
        if (dto.getDevNameToDevInfoMap().containsKey(devName)) { // 这个设备名称已经存在了
            throw new RuntimeException("存在相同名称的设备,设备名称=" + devName);
        }
        DevInfo dev = new DevInfo();
        dev.setModelVersionCode(modelVersionCode);
        dev.setDevName(devName);
        dev.setDevAlias(devName);
        dev.setCreateDate(date);
        dev.setDevTypeId(devTypeId);
        dev.setEnterpriseId(stationInfo.getEnterpriseId());
        dev.setIsLogicDelete(false);
        dev.setIsMonitorDev(false);
        dev.setStationCode(stationInfo.getStationCode());
        dev.setProtocolCode(protocol);
        if (isParent) { // 如果是通管机设备
            dto.setParentDev(dev);
        } else { // 如果是通管机下的子设备
            dto.getChildrenDevs().add(dev);
        }
        dto.getDevNameToDevInfoMap().put(devName, dev); // 设备名称和设备的关系
    }

    /**
     * 解析104点表的信号点信息
     * @param index 当前的行的位置
     * @param list 当前行的数据
     * @param dto
     */
    private void parse104Signals(int index,List<String> list,
                                 ImportExcelDevDTO dto) {
        String errorMsg = checkIsNull(index, list);
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new RuntimeException(errorMsg);
        }
        // 1.解析设备信号点
        String devName = list.get(6).trim();
        if (!dto.getDevNameToSiganlMap().containsKey(devName)) {
            dto.getDevNameToSiganlMap().put(devName, new ArrayList<>());
        }
        DevInfo devInfo = dto.getDevNameToDevInfoMap().get(devName);
        if (devInfo == null) { // 设备名称在信号点（sheet2）中存在，在sheet1中不存在
            throw new RuntimeException("存在设备名称不存在的信号点,row=" + index + "col=6");
        }
        String modelVersionCode = devInfo.getModelVersionCode();
        Integer signalType = Integer.valueOf(list.get(1).trim()); // 信号点类型
        Double gain = Double.valueOf(list.get(3).trim()); // 增益
        String unit = StringUtils.equals("N/A", list.get(2).trim()) ? null : list.get(2).trim();
        Date date = new Date();
        DevSignal devSignal = new DevSignal();
        devSignal.setCreateDate(date);
        devSignal.setSignalType(signalType);
        devSignal.setGain(gain);
        devSignal.setSignalUnit(unit);
        devSignal.setSignalAddress(Integer.valueOf(list.get(4).trim()));
        devSignal.setOffset(Double.valueOf(list.get(5).trim()));
        devSignal.setSignalAlias(list.get(0).trim());
        devSignal.setSignalName(list.get(7).trim());
        devSignal.setDevName(devName); // 设置设备名称,主要导入是做转换的时候使用
        devSignal.setModelVersionCode(modelVersionCode); // 版本号信息
        if (!dto.getDevNameToSiganlMap().containsKey(devName)) {
            dto.getDevNameToSiganlMap().put(devName, new ArrayList<>());
        }
        dto.getDevNameToSiganlMap().get(devName).add(devSignal);
        // 2.解析版本信号点
        if (dto.isExsitSignalNameOfVersionCode(modelVersionCode, devSignal.getSignalName())) {
            return;
        } else {
            if (!dto.getVersionCodeToSiganlMap().containsKey(modelVersionCode)) { // 如果当前版本不存在的信号点模型的列表，就添加一个
                dto.getVersionCodeToSiganlMap().put(modelVersionCode, new ArrayList<>());
            }
            if (!dto.getModelCodeToModelSignalMap().containsKey(modelVersionCode)) {
                dto.getModelCodeToModelSignalMap().put(modelVersionCode, new HashMap<>());
            }
        }
        // 不存在，就添加
        DevVersionSignal devVersionSignal = new DevVersionSignal();
        devVersionSignal.setSignalName(devSignal.getSignalName());
        devVersionSignal.setSignalAlias(devSignal.getSignalName());
        devVersionSignal.setCreateDate(date);
        devVersionSignal.setBit(devSignal.getBit());
        devVersionSignal.setDataType(devSignal.getDataType());
        devVersionSignal.setGain(devSignal.getGain());
        devVersionSignal.setModelVersionCode(devSignal.getModelVersionCode());
        devVersionSignal.setOffset(devSignal.getOffset());
        devVersionSignal.setProtocolCode(devInfo.getProtocolCode());
        devVersionSignal.setRegisterNum(devSignal.getRegisterNum());
//        devVersionSignal.setSignalAddress(devSignal.getSignalAddress());
        devVersionSignal.setSignalGroup(devSignal.getSignalGroup());
        devVersionSignal.setSignalType(devSignal.getSignalType());
        devVersionSignal.setSignalUnit(devSignal.getSignalUnit());
        dto.getVersionCodeToSiganlMap().get(devSignal.getModelVersionCode()).add(devVersionSignal);
        dto.getModelCodeToModelSignalMap().get(modelVersionCode).put(devSignal.getSignalName(), devVersionSignal); // 信号点与版本的信息的集合

    }

    /**
     * 解析104的告警模型，104只有给一个固定的设备通讯中断告警
     * @param dto
     */
    private void pasre104Alarm(ImportExcelDevDTO dto) {
        DevInfo parentDev = dto.getParentDev();
        DevAlarmModel model = new DevAlarmModel();
        model.setAlarmId(65535);
        model.setCauseId(1);
        model.setAlarmCause("设备通讯中断"); //设备通讯中断
        model.setAlarmName("设备通讯中断"); //设备通讯中断
        model.setAlarmType(1);
        model.setDevTypeId(parentDev.getDevTypeId());
        model.setStationCode(parentDev.getStationCode());
        model.setModelVersionCode(parentDev.getModelVersionCode());
        model.setSeverityId((byte)1);
        model.setRepairSuggestion("1.请检查设备是否连接\r\n2.请检查通讯配置是否正确");//1.请检查设备是否连接\r\n2.请检查通讯配置是否正确
        dto.getAlarmList().add(model);
    }


    private List<String> checkExsitsModelVersion(Set<String> versions) {
        // 查询当前版本是否存在
        Example example = new Example(DevModelVersion.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("modelVersionCode", versions);
        List<DevModelVersion> devModelVersions = devModelVersionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(devModelVersions)) {
            return null;
        }
        // 返回存在的版本信息
        return devModelVersions.stream().map(m -> m.getModelVersionCode()).collect(Collectors.toList());

    }

    /**
     * 检查电站信息
     * @param stationName
     * @return
     */
    private StationInfo checkStationInfo(String stationName) {
        if (StringUtils.isBlank(stationName)) {
            throw new RuntimeException("电站名称为空");
        }
        StationInfo station = stationClient.getStationByName(stationName);
        if (station == null) {
            throw new RuntimeException("电站名称{" + stationName + "}不存在");
        }
        return station;
    }

    /**
     * 判断list中的数据是否为空
     * @param rowIndex 当前的行数 从0开始
     * @param list 这一行的具体数据
     * @return String 如果有错误，就返回当前的错误信息，如果没有空的就直接返回的null
     */
    private String checkIsNull(int rowIndex, List<String> list) {
        rowIndex = rowIndex + 1; // rowIndex从0开始，所以数数的时候是加一的行
        if (CollectionUtils.isEmpty(list)) {
            return "第" + rowIndex + "行为空";
        }
        int colNum = list.size();
        for (int i = 0; i < colNum; i++) {
            if (StringUtils.isBlank(list.get(i))) { // 判断是否是空
                return "第" + rowIndex + "行的第" + i + "列为空";
            }
        }
        return null;
    }

    /**
     * 组装扩容的104的信息
     * @param importExcelDevDTO
     * @return
     */
    private Kr104Dto getKr104Dto(ImportExcelDevDTO importExcelDevDTO) {
        Kr104Dto dto = new Kr104Dto();
        String stationCode = importExcelDevDTO.getParentDev().getStationCode();
        Long tgjId = importExcelDevDTO.getParentModelVersion().getId(); // 通管机版本的id
        Long tgjDevId = importExcelDevDTO.getParentDev().getId(); // 通管机设备的id
        // 1.查询版本信息,根据版本编号查询
        Example modelExample = new Example(DevModelVersion.class);
        modelExample.createCriteria().andIn("modelVersionCode",
                importExcelDevDTO.getChildrenModelVersions().stream().map(v -> v.getModelVersionCode()).collect(Collectors.toList()));
        List<DevModelVersion> devModelVersions = devModelVersionMapper.selectByExample(modelExample);
        Map<String, DevModelVersion> allCodeToModelMap = importExcelDevDTO.getModelVersionCodeToModelMap();
        for(DevModelVersion v : devModelVersions) {
            DevModelVersion devModelVersion = allCodeToModelMap.get(v.getModelVersionCode());
            devModelVersion.setId(v.getId()); // 修改已经存在的设置id，如果有id就是已经存在的版本，没有id就是没有存在的id
//            dto.getExistDevModelList().add(devModelVersion); // 添加已经存在的版本
        }
        List<DevModelVersion> newDevModelList = dto.getNewDevModelList(); // 需要新增的版本
        List<DevModelVersion> allDevModelVersionList = importExcelDevDTO.getChildrenModelVersions();
        // 选择出需要新增的版本
        for(DevModelVersion v : allDevModelVersionList) {
            if (v.getId() == null) { // 如果没有存在id就是新增的版本, 因为ModelVersionCodeToModelMap 与ChildrenModelVersions中的设备版本对应的对象都是同一个
                v.setParentId(tgjId); // 设置通管机的id
                newDevModelList.add(v);
            }
        }
        // 2.查询设备信息, 根据设备名称和电站编号查询
        Example devExample = new Example(DevInfo.class);
        devExample.createCriteria().andIn("devName", importExcelDevDTO.getDevNameToDevInfoMap().keySet())
                .andEqualTo("stationCode", stationCode);
        List<DevInfo> dbDevList = devInfoMapper.selectByExample(devExample);
        Map<String, DevInfo> devNameToDevInfoMap = importExcelDevDTO.getDevNameToDevInfoMap();
        List<Long> exsitDevIdList = new ArrayList<>();
        Map<Long, String> devIdToDevNameMap = new HashMap<>(); // 设备id->设备名称的转换 Map<devId, devName>
        for(DevInfo d : dbDevList) { // 已经存在的设备
            Long devId = d.getId();
            exsitDevIdList.add(devId); // 存在设备的id
            devIdToDevNameMap.put(devId, d.getDevName());
            devNameToDevInfoMap.get(d.getDevName()).setId(devId); // 设置当前设备已经有设备id，即在数据库中已经存在了
        }
        List<DevInfo> allDevInfoList = importExcelDevDTO.getChildrenDevs();
        Map<String, DevInfo> newDevMap = dto.getNewDevMap();
        // 选择出需要新增的设备
        for(DevInfo d : allDevInfoList) {
            if (d.getId() == null) {
                d.setParentId(tgjDevId); // 父设备的id
                d.setModelVersionId(allCodeToModelMap.get(d.getModelVersionCode()).getId()); // 如果有就设置，没有就设置为空
                newDevMap.put(d.getDevName(), d); // 新增的设备
            }
        }
        // 3.查询版本信号点信息 根据版本编号获取
        List<DevVersionSignal> dbdevVersionSignalList = new ArrayList<>();
        for (String key : importExcelDevDTO.getModelCodeToModelSignalMap().keySet()) {
            // 从缓存中获取,使用缓存虽然减少与数据库的交互，但是可能会有数据不准确的情况
            List<DevVersionSignal> dsList = devModelsCache.getDevVersionSignalsByModelVersionCode(key);
            if(!CollectionUtils.isEmpty(dsList)) {
                dbdevVersionSignalList.addAll(dsList);
            }
        }

//        Example versionSignalExample = new Example(DevVersionSignal.class);
//        versionSignalExample.createCriteria().andIn("modelVersionCode", importExcelDevDTO.getModelCodeToModelSignalMap().keySet());
//        List<DevVersionSignal> dbdevVersionSignalList = devVersionSignalMapper.selectByExample(versionSignalExample); // 查询数据库中存在的版
        for (DevVersionSignal s : dbdevVersionSignalList) { // 给已经存在的信号点添加id
            String modelVersionCode = s.getModelVersionCode();
            importExcelDevDTO.getModelCodeToModelSignalMap().get(modelVersionCode).get(s.getSignalName()).setId(s.getId()); // 给查询到的数据添加id
        }
        // 选出没有id的信号点模型，这些就是需要新增的信号点模型
        List<DevVersionSignal> newVersionSiganlList = dto.getNewVersionSiganlList();
        List<DevVersionSignal> allVersionSignalList = importExcelDevDTO.getAllDevVersionSignals(); // 所有的版本模型信号点
        for(DevVersionSignal s : allVersionSignalList) {
            if (s.getId() == null) { // 如果没有id，就认为是新增的
                newVersionSiganlList.add(s);
            }
        }
        // 4.查询设备的信号点 设备id
        List<DevSignal> dbDevSignalList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(exsitDevIdList)) {
            for (Long devId : exsitDevIdList) {
                // 从缓存中获取,虽然使用缓存减少与数据库的交互，但是不是那么的准确
                List<DevSignal> devSignalListByDevId = devModelsCache.getDevSignalListByDevId(devId);
                if (!CollectionUtils.isEmpty(devSignalListByDevId)) {
                    dbDevSignalList.addAll(devSignalListByDevId);
                }
            }
            // Example devSignalExample = new Example(DevSignal.class);
            // devSignalExample.createCriteria().andIn("devId", exsitDevIdList);
            // dbDevSignalList = devSignalMapper.selectByExample(devSignalExample);
        }
        importExcelDevDTO.initDevNameToDevSignalMap(); // 初始化设备与信号点之间的关系
        for(DevSignal s : dbDevSignalList) {
            // 设置以及存在的信号点的id
            importExcelDevDTO.getDevNameToDevSignalMap()
                    .get(devIdToDevNameMap.get(s.getDevId()))
                    .get(s.getSignalName())
                    .setId(s.getId());
        }
        // 获取所有的没有信号点的设备信息
        Map<String, List<DevSignal>> devNameToSiganlMap = importExcelDevDTO.getDevNameToSiganlMap();
        for(Map.Entry<String, List<DevSignal>> entry : devNameToSiganlMap.entrySet()) {
            List<DevSignal> tmpList = new ArrayList<>();
            List<DevSignal> sigList = entry.getValue();
            for(DevSignal s : sigList) {
                if (s.getId() == null) { // 数据库中不存在的信息
                    tmpList.add(s);
                }
            }
            if (!tmpList.isEmpty()) { // 当前需要新增的信息
                dto.getNewSignalMap().put(entry.getKey(), tmpList);
            }
        }
        return dto;
    }
    // 更新redis的缓存
    private void flushRedis(ImportExcelDevDTO importExcelDevDTO) {
        // 批量新增缓存
        try {
            // 保存版本信息
            devModelsCache.setDevModelVersion(importExcelDevDTO.getParentModelVersion()); // 保存父版本缓存
            devModelsCache.batchInsertDevModelVersion(importExcelDevDTO.getChildrenModelVersions()); // 保存通管机下挂版本
            // 保存设备信息
            devModelsCache.setDevInifo(importExcelDevDTO.getParentDev()); // 保存通管机设备
            devModelsCache.batchDevs(importExcelDevDTO.getChildrenDevs()); // 保存通管机下挂设备
            // 保存版本信号点模型信息
            devModelsCache.batchAddVersionSignals(importExcelDevDTO.getAllDevVersionSignals());
            // 保存设备信号点信息入缓存
            List<DevSignal> signalList = new ArrayList<>();
            importExcelDevDTO.getDevNameToSiganlMap().values().forEach(ss -> signalList.addAll(ss));
            devModelsCache.batchAddDevSignal(signalList);
        }catch (Exception e) {
            log.error("save devInfo failed:", e);
        }
    }

    /**
     * 删除通管机版本信息
     * @param id 版本的id
     */
    @Transactional // 统一提交事务
    @Override
    public void deleteById(Long id) { // 根据点表信息删除点表信息
        DevModelVersion devModelVersion = this.devModelVersionMapper.selectByPrimaryKey(id);
        if (devModelVersion == null) {
            log.error("[点表删除] 无需要删除的版本信息");
            throw new XiFuException(ExceptionEnum.DEV_MODEL_VERSION_NOT_FOUND);
        }
        if (devModelVersion.getParentId() != null) { // 查询的数据必须是第一级的才支持
            log.error("[点表删除] 不支持删除子版本信息");
            throw new XiFuException(ExceptionEnum.INVALID_PARAM);
        }
        // 查询通管机设备
        Example devChilrenExample = new Example(DevModelVersion.class);
        devChilrenExample.createCriteria().andEqualTo("parentId", id);
        List<DevModelVersion> devModelVersions = this.devModelVersionMapper.selectByExample(devChilrenExample);
        // 组装数据
        List<Long> allModelIdList = new ArrayList<>();
        allModelIdList.add(devModelVersion.getId()); // 添加需要删除的点表
        List<String> allModelCodeList = new ArrayList<>();
        allModelCodeList.add(devModelVersion.getModelVersionCode());
        devModelVersions.forEach(d -> {
            allModelIdList.add(d.getId());
            allModelCodeList.add(d.getModelVersionCode());
        });

        // 1.删除版本信号点模型的数据
        Example delVersionSignalExample = new Example(DevVersionSignal.class);
        delVersionSignalExample.createCriteria().andEqualTo("protocolCode", devModelVersion.getProtocolCode())
                .andIn("modelVersionCode", allModelCodeList);
        this.devVersionSignalMapper.deleteByExample(delVersionSignalExample); // 执行删除设备版本的信号点模型
        // 2.删除设备信号点模型
        // 2.1 查询设备信息
        Example devSearchExample = new Example(DevInfo.class);
        devSearchExample.createCriteria().andIn("modelVersionId", allModelIdList);
        List<DevInfo> devInfos = this.devInfoMapper.selectByExample(devSearchExample);
        List<Long> devIds = null;
        if (!CollectionUtils.isEmpty(devInfos)) { // 如果有设备
            devIds = devInfos.stream().map(d -> d.getId()).collect(Collectors.toList());
            Example delDevSignalExample = new Example(DevSignal.class);
            delDevSignalExample.createCriteria().andIn("devId", devIds);
            devSignalMapper.deleteByExample(delDevSignalExample); // 执行删除设备信号点的信息
            // 3.删除设备
            Example delDevExample = new Example(DevInfo.class);
            delDevExample.createCriteria().andIn("id", devIds);
            this.devInfoMapper.deleteByExample(delDevExample); // 执行删除设备
            // TODO 4.删除对应的归一化
            // 删除设备的缓存
        }

        // 5.删除版本 和子版本
        Example delDevVersionExample = new Example(DevModelVersion.class);
        delDevVersionExample.createCriteria().andIn("id", allModelIdList);
        devModelVersionMapper.deleteByExample(delDevVersionExample);
        // 6.删除告警模型
        Example delAlarmModelExample = new Example(DevAlarmModel.class);
        delAlarmModelExample.createCriteria().andEqualTo("modelVersionCode", devModelVersion.getModelVersionCode());
        this.devAlarmModelMapper.deleteByExample(delAlarmModelExample);
        try {
            // 7.清除redis缓存
            // 7.1删除版本
            this.devModelsCache.removeDevVersion(devModelVersion); // 删除父版本
            this.devModelsCache.batchDelDevVersionCaches(devModelVersions); // 删除子版本
            // 7.2删除设备的缓存
            this.devModelsCache.batchDelDevInfos(devInfos);
            // 7.3 删除版本信号点模型
            this.devModelsCache.clearDevVersionSignalByModelVersionCodeList(allModelCodeList);
            // 7.4 删除设备信号点的模型
            this.devModelsCache.deleteDevSignalByDevIdList(devIds);
        }catch (Exception e) {
            log.error("has exception:", e);
        }
        // TODO 8.通知设备连接的地方去掉对应的连接信息
    }
}
