package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import xifu.com.dto.RoleRequest;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.Role;
import xifu.com.service.RoleService;
import xifu.com.vo.PageInfoResult;
import xifu.com.vo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色表的controller层
 * @auth wq on 2019/1/16 15:04
 **/
@RestController
@RequestMapping("role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取角色的分页信息
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<PageInfoResult<Role>> findRolePage(RoleRequest roleDto, HttpServletRequest request){
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser(); // userLoginUtils.getLoginInfo(request);
        roleDto.setEnterpriseId(loginInfo.getEnterpriseId()); // 登录用户所属的企业
        roleDto.setRoleIds(loginInfo.getRoleIds()); // 登录用户具有的角色id集合
        roleDto.setUserType(loginInfo.getUserType()); // 登录用户的类型 0：企业管理员  1：普通用户 2：超级管理员
        if(roleDto.getPage() == null) {
            roleDto.setPage(1);
        }
        if(roleDto.getPageSize() == null) {
            roleDto.setPageSize(10);
        }
        PageInfo<Role> pageInfo = new PageInfo<>(roleService.findRolePage(roleDto));
        return ResponseEntity.ok(new PageInfoResult<>().setTotal(pageInfo.getTotal()).setList(pageInfo.getList()));
    }

    /**
     * 保存角色
     * @param role
     * @param request
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> saveRole(@Valid Role role, BindingResult result, HttpServletRequest request) {
        if(result.hasFieldErrors()) { // 验证是否正确
            throw new RuntimeException(result.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        role.setCreateDate(new Date());
        role.setCreateUserId(loginInfo.getId());
        this.roleService.saveRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("update")
    public ResponseEntity<Void> updateRole(@Valid Role role, BindingResult result, HttpServletRequest request) {
        if(result.hasFieldErrors()) { // 验证是否正确
            throw new RuntimeException(result.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        role.setModifyDate(new Date());
        role.setModifyUserId(loginInfo.getId());
        this.roleService.updateRole(role);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据id获取角色信息
     * @param id:角色id
     * @return
     */
    @GetMapping("ph/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.roleService.getRoleById(id));
    }

    /**
     * 根据角色id删除角色信息
     * @param id:角色id
     * @return
     */
    @DeleteMapping("ph/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        this.roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据登录用户获取当前的角色信息
     * @param request
     * @return
     */
    @GetMapping("findByLoginUser")
    public ResponseEntity<List<Role>> findByLoginUser(HttpServletRequest request) {
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        List<Role> list = this.roleService.findByLoginUser(loginInfo.getId(), loginInfo.getUserType(), loginInfo.getEnterpriseId());
        return ResponseEntity.ok(list);
    }
}
