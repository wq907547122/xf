package xifu.com.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xifu.com.pojo.User;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 提供给外面的接口定义
 * @auth wq on 2019/2/12 13:36
 **/
public interface UserApi {
    /**
     * 用户登录 传递根据用户名和密码登录
     * @return 返回token
     */
    @PostMapping("login")
    String login(@RequestBody User user);

    /**
     * 根据用户名和密码查询用户的相关信息
     * @param username
     * @param password
     * @return
     */
    @GetMapping("queryUser")
    AuthUserInfo queryUser(@RequestParam("username") String username, @RequestParam("password") String password);

    /**
     * 根据用户名和密码查询用户的信息2
     * 这里测试使用对象的方式调用@RequestBody注解只能使用一次
     * @param user
     * @return
     */
    @PostMapping("queryUser2")
    AuthUserInfo queryUser2(@RequestBody User user);

    /**
     * 用户注销
     * @param request
     */
    @GetMapping("logout")
    void logout(HttpServletRequest request);
}
