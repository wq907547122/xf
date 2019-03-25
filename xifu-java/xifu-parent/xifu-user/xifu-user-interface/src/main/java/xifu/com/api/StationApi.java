package xifu.com.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import xifu.com.pojo.StationInfo;

/**
 * 电站相关的api：给其他微服务调用查询电站相关的信息的接口
 * @auth wq on 2019/3/19 15:18
 **/
public interface StationApi {
    /**
     * 判断电站名称是否可以使用
     * @param stationName 电站名称
     * @param id 可以为空，如果不为空的，就是查询当前的不为当前id的电站是否存在
     * @return
     */
    @GetMapping("check/ph/{stationName}")
    Boolean checkStationNameCanUse(@PathVariable("stationName") String stationName,
                                                          @RequestParam(name = "id", required = false) Long id);
    /**
     * 根据电站名称获取电站信息
     * @param stationName 电站名称
     * @return
     */
    @GetMapping("station/find/ph/{stationName}")
    StationInfo getStationByName(@PathVariable("stationName") String stationName);
}
