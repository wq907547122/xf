package xifu.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.pojo.Auth;
import xifu.com.service.AuthService;
import xifu.com.utils.UserLoginUtils;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 角色权限的controller层
 * @auth wq on 2019/1/24 10:56
 **/
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserLoginUtils userLoginUtils;

    /**
     * 根据登录用户获取当前用户具有的所有权限
     * @param request
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Auth>> queryLoginUserAuths (HttpServletRequest request) {
        AuthUserInfo loginInfo = userLoginUtils.getLoginInfo(request);
        return ResponseEntity.ok(this.authService.queryAuthByUserType(loginInfo.getId(), loginInfo.getUserType()));
    }
}
