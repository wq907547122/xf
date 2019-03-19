package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xifu.com.dto.UserRequest;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.User;
import xifu.com.service.UserService;
import xifu.com.vo.AuthUserInfo;
import xifu.com.vo.PageInfoResult;
import xifu.com.vo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户对应的实体类
 * @auth wq on 2019/1/3 16:03
 **/
@Slf4j
@RestController
//@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户信息
     * @return
     */
    @GetMapping(value = "list")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * 请求分页信息
     * @param page
     * @param pageSize
     * @return
     */
//    @GetMapping("page")
//    public ResponseEntity<PageInfoResult<User>> findPage(
//            @RequestParam(value = "page", defaultValue = "1",required = false) Integer page,
//            @RequestParam(value = "pageSize", defaultValue = "10",required = false) Integer pageSize){
//        List<User> list = userService.findPage(page, pageSize);
//        PageInfo<User> pageInfo = new PageInfo<>(list);
//        PageInfoResult<User> result = new PageInfoResult<>();
//        result.setTotal(pageInfo.getTotal());
//        result.setList(pageInfo.getList());
//        return ResponseEntity.ok(result);
//    }
    /**
     * 请求分页信息
     * @param user
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageInfoResult<User>> findPage(UserRequest user){
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        log.info("user info= {}", loginInfo);
        user.setUserId(loginInfo.getId());
        user.setUserType(loginInfo.getUserType());
        user.setEnterpriseId(loginInfo.getEnterpriseId());
        List<User> list = userService.findUserByPage(user);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        PageInfoResult<User> result = new PageInfoResult<>();
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return ResponseEntity.ok(result);
    }

    /**
     * 用户登录
     * @param user form表单提交的数据
     * @return 返回tokenId
     */
    @PostMapping("login")
    public ResponseEntity<String> login(User user){
        // 登录
        return ResponseEntity.ok(userService.login(user.getLoginName(), user.getPassword()));
    }

    /**
     * 查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("queryUser")
    public ResponseEntity<AuthUserInfo> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        return ResponseEntity.ok(userService.queryUser(username, password));
    }
    @PostMapping("queryUser2")
    public ResponseEntity<AuthUserInfo> queryUser2(@RequestBody User user) {
        return ResponseEntity.ok(userService.queryUser(user.getLoginName(), user.getPassword()));
    }

    /**
     * 注销用户
     * @param request
     * @return
     */
    @GetMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        userService.logout(request); // 注销
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 验证用户信息
     * @param data 需要验证的数据
     * @param type 验证的类型: 1:用户名验证 目前没有其他的需要验证的
     * @return
     */
    @GetMapping("check/ph/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type){
        return ResponseEntity.ok(userService.checkData(data, type));
    }

    /**
     * 用户管理界面新增用户信息
     * @param user
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> saveUser(@Valid User user, BindingResult bindingResult) {
        if(bindingResult.hasFieldErrors()) { // 验证是否正确
            throw new RuntimeException(bindingResult.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser(); // 获取登录用户
        user.setCreateUserId(loginInfo.getId());
        this.userService.saveUser(user); // 用户管理的新增用户
        return ResponseEntity.ok().build();
    }

    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("ph/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    /**
     * 根据用户信息
     * @param user
     * @return
     */
    @PostMapping("update")
    public ResponseEntity<Void> updateUser(User user) {
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        user.setModifyUserId(loginInfo.getId());
        user.setModifyDate(new Date());
        this.userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据用户id删除用户信息
     * @param id : 用户id
     * @return
     */
    @DeleteMapping("ph/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 解析上传excel的测试
     * @param file
     * @return
     */
    @PostMapping("upload")
    public ResponseEntity<Void> uploadExcel(@RequestParam("file") MultipartFile file) {
        this.userService.uploadExcel(file);
        return ResponseEntity.ok().build();
    }
}
