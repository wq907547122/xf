package xifu.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xifu.com.dto.TreeDto;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.pojo.DomainInfo;
import xifu.com.service.DomainInfoService;
import xifu.com.vo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 区域对应的实体类对应的controller层
 * @auth wq on 2019/1/22 15:09
 **/
@RestController
@RequestMapping("domain")
public class DomainInfoController {
    @Autowired
    private DomainInfoService domainInfoService;

    /**
     * 根据企业的id获取企业下的所有区域信息
     * @param id
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<DomainInfo>> findDomainByEnterpriseId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(domainInfoService.findDomainByEnterpriseId(id));
    }

    /**
     * 保存企业信息
     * @param domainInfo
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> save(DomainInfo domainInfo, HttpServletRequest request) {
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        domainInfo.setCreateUserId(loginInfo.getId());
        domainInfo.setCreateDate(new Date());
        setPath(domainInfo);
        domainInfoService.insert(domainInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改区域信息
     * @param domainInfo
     * @param request
     * @return
     */
    @PostMapping("update")
    public ResponseEntity<Void> update(DomainInfo domainInfo, HttpServletRequest request) {
        UserInfo loginInfo = UserAuthInterceptor.getLoginUser();
        domainInfo.setModifyUserId(loginInfo.getId());
        domainInfo.setModifyDate(new Date());
        setPath(domainInfo);
        domainInfoService.update(domainInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 设置路径
     * @param domainInfo
     */
    private void setPath(DomainInfo domainInfo) {
        Long tmpId = 0L;
        if (domainInfo.getParentId() != null && !tmpId.equals(domainInfo.getParentId())) {
            DomainInfo parentDomain = domainInfoService.getById(domainInfo.getParentId());
            if (null != parentDomain) {
                domainInfo.setPath(parentDomain.getPath()+"@"+parentDomain.getId());
            }else {
                domainInfo.setPath("0");
            }
        } else {
            domainInfo.setParentId(tmpId);
            domainInfo.setPath("0");
        }
    }

    /**
     * 删除区域
     * @param id
     * @return
     */
    @DeleteMapping("ph/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        domainInfoService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取包含企业的树节点
     * @return
     */
    @GetMapping("tree")
    public ResponseEntity<List<TreeDto>> findTreeWithEnterprise() {
        return ResponseEntity.ok(domainInfoService.findTreeWithEnterprise());
    }
}
