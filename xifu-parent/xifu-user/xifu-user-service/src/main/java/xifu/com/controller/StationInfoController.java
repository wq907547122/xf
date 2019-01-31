package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xifu.com.dto.StationRequest;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.StationInfo;
import xifu.com.service.StationInfoService;
import xifu.com.vo.AuthUserInfo;
import xifu.com.vo.PageInfoResult;

import java.util.List;

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
        AuthUserInfo loginUser = UserAuthInterceptor.getLoginUser();
        stationRequest.setUserId(loginUser.getId());
        stationRequest.setUserType(loginUser.getUserType());
        List<StationInfo> list = stationInfoService.findStationPage(stationRequest);
        PageInfo<StationInfo> pageInfo = new PageInfo<>(list);
        return ResponseEntity.ok(new PageInfoResult<StationInfo>().setList(pageInfo.getList()).setTotal(pageInfo.getTotal()));
    }
}
