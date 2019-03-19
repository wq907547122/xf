package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.Enterprise;
import xifu.com.pojo.User;
import xifu.com.service.EnterpriseService;
import xifu.com.vo.PageInfoResult;
import xifu.com.vo.PageRequestInfo;
import xifu.com.vo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业表的实体类对应的controller层
 * @auth wq on 2019/1/17 9:36
 **/
@RestController
@RequestMapping("enterprise")
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 获取当前用户所在的企业，超级管理员是查询所有企业
     * @param request
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Enterprise>> list(HttpServletRequest request){
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        if(loginInfo.getId() == 1) { // 超级管理员
            return ResponseEntity.ok(enterpriseService.findAll());
        }
        // 普通用户或者企业管理员，只查询当前的企业
        return ResponseEntity.ok(enterpriseService.findByUserId(loginInfo.getId()));
    }

    /**
     * 新增企业
     * @param enterprise
     * @param user
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> save(@Valid Enterprise enterprise, BindingResult result, User user, HttpServletRequest request){
        if(result.hasFieldErrors()) { // 验证是否正确
            throw new RuntimeException(result.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        enterpriseService.saveEnterprise(enterprise, user, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改企业
     * @param enterprise
     * @param request
     * @return
     */
    @PostMapping("update")
    public ResponseEntity<Void> update(@Valid Enterprise enterprise, BindingResult result, HttpServletRequest request) {
        if(result.hasFieldErrors()) { // 验证是否正确
            throw new RuntimeException(result.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        enterpriseService.updateEnterprise(enterprise, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 获取企业的分页信息
     * @param pageInfo
     * @param enterprise
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageInfoResult<Enterprise>> queryPage(PageRequestInfo pageInfo, Enterprise enterprise){
        List<Enterprise> enterprises = enterpriseService.queryPage(pageInfo.getPage(), pageInfo.getPageSize(), enterprise);
        PageInfo<Enterprise> info = new PageInfo<>(enterprises);
        return ResponseEntity.ok(new PageInfoResult<>().setTotal(info.getTotal()).setList(info.getList()));
    }

    @GetMapping("ph/{id}")
    public ResponseEntity<Enterprise> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(enterpriseService.getById(id));
    }
}
