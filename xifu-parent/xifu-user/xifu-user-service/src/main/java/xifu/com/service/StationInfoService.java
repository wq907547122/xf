package xifu.com.service;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xifu.com.dto.StationRequest;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.StationInfoMapper;
import xifu.com.pojo.StationInfo;

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

}
