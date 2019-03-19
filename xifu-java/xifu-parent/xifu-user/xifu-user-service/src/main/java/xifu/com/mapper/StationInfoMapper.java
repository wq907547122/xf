package xifu.com.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import xifu.com.dto.StationRequest;
import xifu.com.pojo.StationInfo;

import java.util.List;

/**
 * 电站表的实体类的Mapper
 * @auth wq on 2019/1/30 10:16
 **/
public interface StationInfoMapper extends Mapper<StationInfo> {
    /**
     * 获取企业下的区域信息
     * @param stationRequest
     * @return
     */
    List<StationInfo> findStationOfDomain(StationRequest stationRequest);

    /**
     * 查询普通用户管理所在区域下的电站信息
     * @param stationRequest
     * @return
     */
    List<StationInfo> findStationOfDomainByUser(StationRequest stationRequest);

    /**
     * 保存用户和电站的关系
     * @param userId
     * @param stationCodes
     * @return
     */
    int insertUserAndStations(@Param("userId") Long userId, @Param("stationCodes") List<String> stationCodes);
}
