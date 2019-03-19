package xifu.com.service;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import xifu.com.dto.RoleAuthDto;
import xifu.com.dto.RoleRequest;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.RoleMapper;
import xifu.com.pojo.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色表的service层
 * @auth wq on 2019/1/16 15:00
 **/
@Slf4j
@Service
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 查询所有角色
     * @return
     */
    public List<Role> findAllRole() {
        List<Role> roles = roleMapper.selectAll();
        if(CollectionUtils.isEmpty(roles)) {
            throw new XiFuException(ExceptionEnum.ROLE_NOT_FOUND);
        }
        return roles;
    }

    /**
     * 查询角色的分页信息
     * @param roleDto : 查询参数
     * @return
     */
    public List<Role> findRolePage(RoleRequest roleDto) {
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        switch (roleDto.getUserType()) {
            case 0: // 企业用户
                criteria.andEqualTo("enterpriseId", roleDto.getEnterpriseId());
                break;
            case 1: // 普通用户具有的角色
                criteria.andIn("id", roleDto.getRoleIds());
                break;
            case 2: // 超级管理员
                break;
            default: // 没有定义的类型，就不让查询
                throw new XiFuException(ExceptionEnum.INVALID_ROLE_PARAM);
        }
        if(StringUtils.isNotBlank(roleDto.getName())) {
            criteria.andLike("name", "%" + roleDto.getName().trim() + "%");
        }
        if(roleDto.getStartDate() != null) {
            criteria.andGreaterThanOrEqualTo("createDate", roleDto.getStartDate());
        }
        if(roleDto.getEndDate() != null) {
            criteria.andLessThanOrEqualTo("createDate", roleDto.getEndDate());
        }
        PageHelper.startPage(roleDto.getPage(), roleDto.getPageSize()); // 启用分页助手
        List<Role> roles = roleMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(roles)) { // 如果没有查询到就抛出异常
            throw new XiFuException(ExceptionEnum.ROLE_NOT_FOUND);
        }
        return roles;
    }

    /**
     * 保存角色
     * @param role
     */
    @Transactional
    public void saveRole(Role role) {
        if(CollectionUtils.isEmpty(role.getAuthIds())) { // 如果没有权限信息
            log.error("[新增角色] 缺少角色信息");
            throw new XiFuException(ExceptionEnum.ADD_ROLE_FAILED);
        }
        // 1.保存角色
        int count = this.roleMapper.insertSelective(role);
        if(count != 1 || role.getId() == null) {
            log.error("[新增角色] 保存角色失败");
            throw new XiFuException(ExceptionEnum.ADD_ROLE_FAILED);
        }
        // 2.保存角色与权限之间的关系
        Long roleId = role.getId();
        List<RoleAuthDto> roleAuthDtos = role.getAuthIds().stream()
                .map(authId -> new RoleAuthDto(roleId, authId)).collect(Collectors.toList());
        // 保存关系
        count = this.roleMapper.saveRoleAuthInfos(roleAuthDtos);
        if (count != roleAuthDtos.size()) {
            log.error("[新增角色] 保存角色与权限的关系数据失败");
            throw new XiFuException(ExceptionEnum.ADD_ROLE_FAILED);
        }
    }

    /**
     * 根据角色id获取角色信息
     * @param id
     * @return
     */
    public Role getRoleById(Long id) {
        // 1.查询角色信息
        Role role = this.roleMapper.selectByPrimaryKey(id);
        if(role == null) {
            throw new XiFuException(ExceptionEnum.ROLE_NOT_FOUND);
        }
        // 2.查询角色的权限
        List<Long> authIds = this.roleMapper.findAuthIdsByRoleId(id);
        role.setAuthIds(authIds);
        return role;
    }

    /**
     * 修改角色
     * @param role
     */
    @Transactional // 统一事务的回退或者提交
    public void updateRole(Role role) {
        // 1.修改角色
        this.roleMapper.updateByPrimaryKeySelective(role);
        // 2.删除旧的角色与权限的关系
        this.roleMapper.deleteRoleAuthByRoleId(role.getId());
        // 3.新增角色与用户的关系
        Long roleId = role.getId();
        List<RoleAuthDto> roleAuthDtos = role.getAuthIds().stream()
                .map(authId -> new RoleAuthDto(roleId, authId)).collect(Collectors.toList());
        // 保存关系
        int count = this.roleMapper.saveRoleAuthInfos(roleAuthDtos);
        if (count != roleAuthDtos.size()) {
            log.error("[修改角色] 保存角色与权限的关系数据失败");
            throw new XiFuException(ExceptionEnum.ADD_ROLE_FAILED);
        }
    }

    /**
     * 根据角色id删除
     * @param id
     */
    @Transactional
    public void deleteRole(Long id) {
        // 1.判断是否有用户绑定角色
        int count = this.roleMapper.countBindUserByRoleId(id);
        if (count > 0) { // 如果角色有绑定的用户,就不能删除
            throw new XiFuException(ExceptionEnum.ROLE_HAS_BIND_USER);
        }
        // 1.删除角色
        this.roleMapper.deleteByPrimaryKey(id);
        // 2.删除角色与权限的关系
        this.roleMapper.deleteRoleAuthByRoleId(id);
    }

    /**
     * 根据用户类型和用户id获取角色信息
     * @param userId ：查询用户的id
     * @param userType : 用户类型 0:企业用户 1:注册用户 2：系统管理员
     * @param enterpriseId : 企业id
     * @return
     */
    public List<Role> findByLoginUser(Long userId, Byte userType, Long enterpriseId) {
        List<Role> list = null;
        switch (userType) {
            case 0:// 查询当前企业下的所有角色
                Example example0 = new Example(Role.class);
                example0.createCriteria()
                        .orEqualTo("enterpriseId", enterpriseId) // 除了超级管理员以外的都可以查询出来
                        .andEqualTo("status", (byte)0) // 角色状态是启动的
                        .orEqualTo("roleType", "operator"); // 企业用户可以创建运维人员,这个正常应该创建一个枚举，这里暂时就不创建枚举类型的了
                list = this.roleMapper.selectByExample(example0);
                break;
            case 1: // 普通用户，查询当前用户具有的角色
                list = this.roleMapper.findRoleByUserId(userId);
                break;
            case 2: // 查询所有角色,最好不要使用超级管理员查询
                Example example = new Example(Role.class);
                example.createCriteria()
                        .andNotEqualTo("id", 1L) // 除了超级管理员以外的都可以查询出来
                        .andEqualTo("status", (byte)0); // 角色状态是启动的
                list = this.roleMapper.selectByExample(example);
                break;
            default:
                log.error("[角色信息] 未知的用户类型");
                throw new XiFuException(ExceptionEnum.ROLE_NOT_FOUND);
        }
        if(CollectionUtils.isEmpty(list)) {
            log.error("[角色信息] 获取用户信息为空,userId={}, 企业id={}", userId, enterpriseId);
            throw new XiFuException(ExceptionEnum.ROLE_NOT_FOUND);
        }
        return list;
    }
}
