package xifu.com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xifu.com.dto.TreeDto;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.intercepters.UserAuthInterceptor;
import xifu.com.mapper.DomainInfoMapper;
import xifu.com.mapper.EnterpriseMapper;
import xifu.com.pojo.DomainInfo;
import xifu.com.pojo.Enterprise;
import xifu.com.vo.AuthUserInfo;
import xifu.com.vo.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 区域表对应实体类的service层
 * @auth wq on 2019/1/22 14:17
 **/
@Slf4j
@Service
public class DomainInfoService {
    @Autowired
    private DomainInfoMapper domainInfoMapper;
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    /**
     * 获取企业下的所有区域，包括区域的子区域
     * @param id:企业id
     * @return
     */
    public List<DomainInfo> findDomainByEnterpriseId(Long id) {
        List<DomainInfo> list = domainInfoMapper.getNodeTree(id);
        if(CollectionUtils.isEmpty(list)) {
            throw new XiFuException(ExceptionEnum.DOMAIN_NOT_FOUND);
        }
        return list;
    }

    /**
     * 保存区域的信息
     * @param domainInfo
     */
    public void insert(DomainInfo domainInfo) {
        this.domainInfoMapper.insertSelective(domainInfo);
    }

    /**
     * 修改区域信息
     * @param domainInfo
     */
    public void update(DomainInfo domainInfo) {
        this.domainInfoMapper.updateByPrimaryKeySelective(domainInfo);
    }

    /**
     * 根据区域id获取区域
     * @param id : 区域的id
     * @return
     */
    public DomainInfo getById(Long id) {
        DomainInfo domainInfo = this.domainInfoMapper.selectByPrimaryKey(id);
        if(domainInfo == null) {
            throw new XiFuException(ExceptionEnum.DOMAIN_NOT_FOUND);
        }
        return domainInfo;
    }

    /**
     * 根据id删除区域
     * @param id
     */
    public void delete(Long id) {
        // 1.判断区域是否有子区域
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setParentId(id);
        List<DomainInfo> select = this.domainInfoMapper.select(domainInfo);
        if(!CollectionUtils.isEmpty(select)) {
            throw new XiFuException(ExceptionEnum.DOMAIN_EXSIT_CHILDREN_FOUND);
        }
        // TODO 2.判断区域下是否有电站
        this.domainInfoMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取当前登录用户的区域树结构，包含企业,这个方法比较消耗数据库的，后面看可以如何优化
     * @return
     */
    public List<TreeDto> findTreeWithEnterprise() {
        UserInfo loginUser = UserAuthInterceptor.getLoginUser();
        List<TreeDto> list = null;
        switch (loginUser.getUserType()) {
            case 0: // 企业用户
            case 1: // 普通用户
                list = getNormalTree(loginUser.getEnterpriseId());
                break;
            case 2:
                // 超级管理员查询所有
                list = getAdminTree();
                break;
            default: // 未知的用户类型
                throw new XiFuException(ExceptionEnum.INVALID_USER_PARAM);
        }
        return list;
    }

    /**
     * 获取超级管理员的树节点信息
     * @return
     */
    private List<TreeDto> getAdminTree() {
       // 获取企业
       List<Enterprise> enterprises = enterpriseMapper.selectAll();
       if (CollectionUtils.isEmpty(enterprises)) {
           log.error("[区域树结构查询] 不存在企业");
           throw new XiFuException(ExceptionEnum.ENTERPRISE_NOT_FOUND);
       }
       // 获取区域
       List<TreeDto> list = new ArrayList<>();
       for (Enterprise enterprise : enterprises) {
           getEnterpriseTree(list, enterprise);
       }
       return list;
    }

    /**
     * 获取企业的区域树结构,主要是从数据库中查询
     * @param list
     * @param enterprise
     */
    private void getEnterpriseTree(List<TreeDto> list, Enterprise enterprise) {
        List<DomainInfo> nodeTree = this.domainInfoMapper.getNodeTree(enterprise.getId());
        TreeDto treeDto = new TreeDto();
        treeDto.setId(enterprise.getId() + "@e"); // 是企业
        treeDto.setLabel(enterprise.getName());
        if (CollectionUtils.isEmpty(nodeTree)) {
            treeDto.setIsLeaf(true);
        } else {
            treeDto.setChildren(createTree(nodeTree));
        }
        list.add(treeDto);
    }

    // 构造区域的树节点==>转换为DomainInfo -> TreeDto
    private List<TreeDto> createTree(List<DomainInfo> domainInfos){
        return domainInfos.stream().map(d -> {
            TreeDto treeDto = new TreeDto();
            treeDto.setId(d.getId() + "@domain");
            treeDto.setLabel(d.getName());
            List<DomainInfo> children = d.getChildren();
            if (CollectionUtils.isEmpty(children)) {
                treeDto.setIsLeaf(true);// 没有子节点
            } else  {
                treeDto.setChildren(createTree(children)); // 递归调用
            }
            return treeDto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取企业管理员和普通用户的树节点
     * @param enterpriseId
     * @return
     */
    private List<TreeDto> getNormalTree(Long enterpriseId) {
        List<TreeDto> list = new ArrayList<>();
        Enterprise enterprise = this.enterpriseMapper.selectByPrimaryKey(enterpriseId);
        getEnterpriseTree(list, enterprise); // 查询企业下的区域，组装为统一的格式
        return list;
    }
}
