package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xifu.com.dto.StationRequest;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.StationInfo;
import xifu.com.service.StationInfoService;
import xifu.com.utils.XifuRandomUtils;
import xifu.com.vo.PageInfoResult;
import xifu.com.vo.UserInfo;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 电站表对应实体类的controller层
 * @auth wq on 2019/1/30 10:19
 **/
@RestController
@RequestMapping("station")
public class StationInfoController {
    @Autowired
    private StationInfoService stationInfoService;

    /**
     * 获取电站的分页信息
     * @param stationRequest
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageInfoResult<StationInfo>> findStationPage(StationRequest stationRequest) {
        UserInfo loginUser = UserAuthInterceptor.getLoginUser();
        stationRequest.setUserId(loginUser.getId());
        stationRequest.setUserType(loginUser.getUserType());
        List<StationInfo> list = stationInfoService.findStationPage(stationRequest);
        PageInfo<StationInfo> pageInfo = new PageInfo<>(list);
        return ResponseEntity.ok(new PageInfoResult<StationInfo>().setList(pageInfo.getList()).setTotal(pageInfo.getTotal()));
    }

    /**
     * 判断电站名称是否可以使用
     * @param stationName
     * @return
     */
    @GetMapping("check/ph/{stationName}")
    public ResponseEntity<Boolean> checkStationNameCanUse(@PathVariable("stationName") String stationName,
                                                          @RequestParam(name = "id", required = false) Long id) {
        return ResponseEntity.ok(this.stationInfoService.isCanUseStationName(stationName, id));
    }

    /**
     * 保存电站信息
     * @param stationInfo
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> saveStation(StationInfo stationInfo) {
        UserInfo loginUser = UserAuthInterceptor.getLoginUser();
        stationInfo.setCreateUserId(loginUser.getId());
        stationInfo.setCreateDate(new Date());
        stationInfo.setUserType(loginUser.getUserType());
        stationInfo.setTimeZone(Integer.parseInt(TimeZone.getDefault().getRawOffset() / 3600000 + "")); // 设置时区
        stationInfo.setStationCode(StringUtils.replace(XifuRandomUtils.getUUIDString(), "-", ""));
        this.stationInfoService.saveStation(stationInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新电站信息
     * @param stationInfo
     * @return
     */
    @PostMapping("update")
    public ResponseEntity<Void> updateStation(StationInfo stationInfo) {
        UserInfo loginUser = UserAuthInterceptor.getLoginUser();
        stationInfo.setUpdateUserId(loginUser.getId());
        stationInfo.setUpdateDate(new Date());
        this.stationInfoService.updateStation(stationInfo);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据id获取电站信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<StationInfo> getStationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.stationInfoService.getStationById(id));
    }

    /**
     * 根据电站名称获取电站信息
     * @param stationName 电站名称
     * @return
     */
    @GetMapping("find/ph/{stationName}")
    public ResponseEntity<StationInfo> getStationByName(@PathVariable("stationName") String stationName){
        return ResponseEntity.ok(this.stationInfoService.getStationByStationName(stationName));
    }
}
