package xifu.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xifu.com.pojo.User;
import xifu.com.service.AuthService;

/**
 * 用户权限认证的接口
 * @auth wq on 2019/2/12 13:41
 **/
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<String> login(User user) {
        return ResponseEntity.ok(authService.login(user));
    }
}
