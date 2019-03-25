package xifu.com.service;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import xifu.com.dto.StationRequest;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.StationInfoMapper;
import xifu.com.pojo.StationInfo;

import java.util.Arrays;
import java.util.List;

/**
 * 电站表的service层
 * @auth wq on 2019/1/30 10:17
 **/
@Slf4j
@Service
public class StationInfoService {
    @Autowired
    private StationInfoMapper stationInfoMapper;

    /**
     * 查询电站的分页信息,根据用户类型来区分
     * 如果是企业用户和超级管理员查询的数据相同
     * 如果是普通用户，只能查询他当前管理的用户
     * @param stationRequest
     * @return
     */
    public List<StationInfo> findStationPage(StationRequest stationRequest) {
        List<StationInfo> list = null;
        // 启用分页助手
        PageHelper.startPage(stationRequest.getPage(), stationRequest.getPageSize());
        // 当前登录的用户类型 // 用户类型：0:企业用户 1:注册用户 2：系统管理员
        switch (stationRequest.getUserType()) {
            case 1: // 普通用户,只查询当前用户管理的电站
                list = stationInfoMapper.findStationOfDomainByUser(stationRequest);
                break;
            case 0: // 企业用户 和超级管理员查询的相同
            case 2: // 超级管理员
                // 普通用户，和超级管理员是查询当前企业或者区域下的所有电站
                list = stationInfoMapper.findStationOfDomain(stationRequest);
                break;
            default:
                throw new XiFuException(ExceptionEnum.INVALID_USER_PARAM);
        }
        if (CollectionUtils.isEmpty(list)) { // 没有查询到
            throw new XiFuException(ExceptionEnum.STATION_NOT_FOUND);
        }
        return list;
    }

    /**
     * 判断电站名称是否可用, true 可以用 false：不可用，已经存在电站名称
     * @param stationName
     * @param id 如果id不为空，则查询id不为他的数据
     * @return
     */
    public boolean isCanUseStationName (String stationName, Long id) {
        if (StringUtils.isBlank(stationName)) { // 如果电站名称是空的字符串
            return false;
        }
        Example example = new Example(StationInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stationName", stationName);
        if (id != null) {
            criteria.andNotEqualTo("id", id);
        }
        int count = this.stationInfoMapper.selectCountByExample(example);
        return count == 0;
    }

    /**
     * 保存电站信息
     * @param stationInfo
     */
    @Transactional
    public void saveStation(StationInfo stationInfo) {
        // 验证电站名称是否可用
        if (!this.isCanUseStationName(stationInfo.getStationName(), null)) {
            log.error("[创建电站] 新增电站失败,此电站名称已经存在");
            throw new XiFuException(ExceptionEnum.CREATE_STATION_ERROR);
        }
        // 1.保存电站
        int count = this.stationInfoMapper.insertSelective(stationInfo);
        if (count != 1) {
            log.error("[创建电站] 新增电站失败");
            throw new XiFuException(ExceptionEnum.CREATE_STATION_ERROR);
        }
        // 2.如果是普通用户创建的电站，需要创建用户和电站的关系
        if (stationInfo.getUserType() == 1) { // 保存用户和电站的关系
            count = this.stationInfoMapper.insertUserAndStations(stationInfo.getCreateUserId(), Arrays.asList(stationInfo.getStationCode()));
            if (count != 1) {
                log.error("[创建电站] 新增用户与电站的关系表失败，用户id={},电站编号={}", stationInfo.getCreateUserId(), stationInfo.getStationCode());
                throw new XiFuException(ExceptionEnum.CREATE_STATION_ERROR);
            }
        }
    }

    /**
     * 更新电站信息
     * @param stationInfo
     */
    public void updateStation(StationInfo stationInfo) {
        // 1.验证电站名称
        if (!this.isCanUseStationName(stationInfo.getStationName(), stationInfo.getId())) {
            log.error("[修改电站] 修改电站失败,此电站名称已经存在");
            throw new XiFuException(ExceptionEnum.EDIT_STATION_ERROR);
        }
        int count = this.stationInfoMapper.updateByPrimaryKeySelective(stationInfo);
        if (count != 1) {
            log.error("[修改电站] 修改电站失败");
            throw new XiFuException(ExceptionEnum.EDIT_STATION_ERROR);
        }
    }

    /**
     * 根据电站id获取电站信息
     * @param id
     * @return
     */
    public StationInfo getStationById(Long id) {
        StationInfo stationInfo = this.stationInfoMapper.selectByPrimaryKey(id);
        if (stationInfo == null) {
            log.error("[电站查询] 查询不到电站信息，电站id:{}", id);
            throw new XiFuException(ExceptionEnum.STATION_NOT_FOUND);
        }
        return stationInfo;
    }

    /**
     * 根据电站名称查询电站
     * @param stationName 查询的电站名称，不是模糊是等于
     * @return
     */
    public StationInfo getStationByStationName(String stationName) {
        if (StringUtils.isBlank(stationName)) {
            log.error("[电站查询] 电站名称为空");
            throw new XiFuException(ExceptionEnum.STATION_NOT_FOUND);
        }
        StationInfo search = new StationInfo();
        search.setStationName(stationName);
        search.setIsMonitor(false); // 不是监控传递上来的电站
        search.setIsDelete(false); // 电站没有被删除
        List<StationInfo> select = this.stationInfoMapper.select(search);
        if (CollectionUtils.isEmpty(select)) {
            log.error("[电站查询] 查询不到电站信息，电站名称:{}", stationName);
            throw new XiFuException(ExceptionEnum.STATION_NOT_FOUND);
        }
        if (select.size() > 1) {
            log.error("[电站查询] 查询到多个电站，电站名称：{}", stationName);
            throw new XiFuException(ExceptionEnum.STATION_FOUND_MULT);
        }
        return select.get(0);
    }
}
