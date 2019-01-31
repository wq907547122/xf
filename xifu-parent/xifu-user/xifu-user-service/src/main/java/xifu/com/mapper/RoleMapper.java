package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import xifu.com.dto.RoleAuthDto;
import xifu.com.pojo.Role;

import java.util.List;

/**
 * 角色表的mapper
 * @auth wq on 2019/1/16 14:53
 **/
public interface RoleMapper extends Mapper<Role>{
    /**
     * 保存角色与权限的关系
     * @param roleAuthDtos
     * @return
     */
    int saveRoleAuthInfos(List<RoleAuthDto> roleAuthDtos);

    /**
     * 根据角色id获取他的所有权限id的集合
     * @param id
     * @return
     */
    List<Long> findAuthIdsByRoleId(Long id);

    /**
     * 根据角色id删除 角色与权限的关系表
     * @param id
     */
    void deleteRoleAuthByRoleId(Long id);

    /**
     * 根据角色id查询当前角色是否存在绑定的用户
     * @param id
     * @return
     */
    int countBindUserByRoleId(Long id);

    /**
     * 根据用户查询角色信息
     * @param userId 用户id
     * @return
     */
    List<Role> findRoleByUserId(Long userId);
}
