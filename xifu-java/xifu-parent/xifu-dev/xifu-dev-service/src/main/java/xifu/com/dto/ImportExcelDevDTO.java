package xifu.com.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import xifu.com.pojo.devService.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于保存点表导入的excel中的设备、版本、信号点、告警模型等信息
 * @auth wq on 2019/3/20 9:58
 **/
@Data
public class ImportExcelDevDTO {
    // 用于保存导入点表的父版本的版本类型
    private DevModelVersion parentModelVersion;
    // 用于保存子版本的设备版本信息
    private List<DevModelVersion> childrenModelVersions = new ArrayList<>();
    // 用于保存通管机设备的信息
    private DevInfo parentDev;
    // 用于保存通管机下的子设备信息
    private List<DevInfo> childrenDevs = new ArrayList<>();
    // 用于保存版本编号和对应模型信号点的对应关系 <版本的modelVersionCode, 版本的信号点的集合>
    private Map<String, List<DevVersionSignal>> versionCodeToSiganlMap = new HashMap<>();
    // 版本信息和版本实体之间的对应关系<modelVersionCode, 版本的一个实体对象>
    private Map<String, DevModelVersion> modelVersionCodeToModelMap = new HashMap<>();
    // 用于保存设备名称到设备信号点的对应关系 <设备名称， 设备对应的信号点>
    private Map<String, List<DevSignal>> devNameToSiganlMap = new HashMap<>();
    // 设备与设备信号点的Map<设备名称，<信号点名称， 信号点信息>>
    private Map<String, Map<String, DevSignal>> devNameToDevSignalMap = new HashMap<>();
    // 版本信息->信号点模型名称->信号点模型对象 <版本编号, <信号点模型名称，信号点模型实体对象>>
    private Map<String, Map<String, DevVersionSignal>> modelCodeToModelSignalMap = new HashMap<>();
    // 用于保存设备名称和设备之间的关系 <设备名称，设备信息>
    private Map<String, DevInfo> devNameToDevInfoMap = new HashMap<>();
    // 用于保存导入点表的告警模型的信息
    private List<DevAlarmModel> alarmList = new ArrayList<>();

    /**
     * 判断子版本集合中是否包含给定的字符串
     * @param modelVersionCode
     * @return
     */
    public boolean isChildrenVersionIncloudVersion(String modelVersionCode) {
        if(CollectionUtils.isEmpty(childrenModelVersions)) {
            return false;
        }
        for(DevModelVersion d : childrenModelVersions) {
            if (StringUtils.equals(d.getModelVersionCode(), modelVersionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前的版本模型中是否存在当前的信号点名称
     * @param modelVersionCode
     * @param signalName
     * @return
     */
    public boolean isExsitSignalNameOfVersionCode (String modelVersionCode,String signalName) {
        List<DevVersionSignal> devVersionSignals = versionCodeToSiganlMap.get(modelVersionCode);
        if (CollectionUtils.isEmpty(devVersionSignals)) {
            return false;
        }
        for (DevVersionSignal s : devVersionSignals) {
            if (StringUtils.equals(s.getSignalName(), signalName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置通管机下挂设备版本信息的父节点的id
     * @param modelVersionId 通管机版本的id
     */
    public void setParentVersionId(Long modelVersionId){
        for(DevModelVersion v : childrenModelVersions) {
            v.setParentId(modelVersionId);
        }
    }

    /**
     * 对设备的设置对于的版本号的id
     */
    public void setChildrenDevsModelVersionId() {
        Long parentId = parentDev.getId();
        for (DevInfo dev : childrenDevs) {
            dev.setParentId(parentId); // 设置父节点的id
            DevModelVersion devModelVersion = modelVersionCodeToModelMap.get(dev.getModelVersionCode());
            if (devModelVersion != null) {
                dev.setModelVersionId(devModelVersion.getId());
            }
        }
    }

    /**
     * 获取所有的模型信号点
     * @return
     */
    public List<DevVersionSignal> getAllDevVersionSignals() {
        List<DevVersionSignal> list = new ArrayList<>();
        for(Map.Entry<String, List<DevVersionSignal>> entry : versionCodeToSiganlMap.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    /**
     * 设置设备信号点的设备id和信号点模型id,并且返回所有的信号点
     * @return
     */
    public void setDevSignalInfos() {
        for(Map.Entry<String, List<DevSignal>> entry: devNameToSiganlMap.entrySet()) {
            List<DevSignal> list = entry.getValue();
            String devName = entry.getKey();
            for (DevSignal s : list) {
                DevInfo devInfo = devNameToDevInfoMap.get(devName);
                if(devInfo != null) { // 设置设备id
                    s.setDevId(devInfo.getId());
                }
                // 设置信号点模型的id
                Map<String, DevVersionSignal> stringDevVersionSignalMap = modelCodeToModelSignalMap.get(s.getModelVersionCode());
                if (CollectionUtils.isEmpty(stringDevVersionSignalMap)) {
                    continue;
                }
                DevVersionSignal devVersionSignal = stringDevVersionSignalMap.get(s.getSignalName());
                if (devVersionSignal == null) {
                    continue;
                }
                s.setModelId(devVersionSignal.getId()); // 设置信号点模型的id
            }
        }
    }
    // 初始化设备与设备信号点与
    public void initDevNameToDevSignalMap() {
        for (Map.Entry<String, List<DevSignal>> entry : devNameToSiganlMap.entrySet()) {
            String devName = entry.getKey();
            if (!devNameToDevSignalMap.containsKey(devName)) {
                devNameToDevSignalMap.put(devName, new HashMap<>());
            }
            List<DevSignal> signalList = entry.getValue();
            Map<String, DevSignal> tmpSignalNameToDevSignalMap = devNameToDevSignalMap.get(devName);
            for(DevSignal s : signalList) {
                tmpSignalNameToDevSignalMap.put(s.getSignalName(), s);
            }
        }
    }
}
