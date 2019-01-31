package xifu.com.mapper;

import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;
import xifu.com.dto.UserRequest;
import xifu.com.mapper.helper.InSqlHelper;
import xifu.com.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * 用户信息的mapper
 * @auth wq on 2019/1/3 15:33
 **/
public interface UserMapper extends Mapper<User> {
    /**
     * 获取用户对应的角色的id集合
     * @param userId
     * @return
     */
    @Select("select role_id from tb_user_role where user_id = #{value}")
    List<Long> getRoleIds(Long userId);

    /**
     * 根据ids获取对应的权限列表信息
     * @param roleIds
     * @return
     */
//    @Select("select DISTINCT auth_id from t_role_auth where" +
//            "role_id in " +
//            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
//            "#{item}" +
//            "</foreach>")
    @Select("SELECT DISTINCT auth_id FROM tb_role_auth WHERE role_id IN (#{roleIds})")
    @Lang(InSqlHelper.class) // in中通过循环来遍历会将(#{roleIds})替换为对应in的in条件
    List<Long> getAuthIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据权限的id集合获取对应的请求资源信息
     * @param authIds
     * @return
     */
//    @Select("select DISTINCT auth_url from t_resource_info " +
//            "where auth_id in " +
//            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
//            "#{item}" +
//            "</foreach>")
    @Select("select DISTINCT auth_url from tb_resource_info where auth_id in (#{authIds})")
    @Lang(InSqlHelper.class)
    List<String> findResourceByAuthIds(@Param("authIds") List<Long> authIds);

    /**
     * 新增企业用户和角色的中间表信息,角色的id固定为2
     * @param userId
     */
    @Insert("INSERT INTO `tb_user_role` (`user_id`, `role_id`) VALUES(#{value}, '2')")
    void insertUserOfRole(Long userId);

    /**
     * 根据用户信息查询普通用户(或企业用户)管理的用户信息
     * @param user
     * @return
     */
    List<User> findEnterpriseUsers(UserRequest user);

    /**
     * 超级管理员查询用户信息
     * @param user
     * @return
     */
    List<User> findAllUsers(UserRequest user);

    /**
     * 根据用户id集合获取用户对应的角色的名称
     * @param userIds: 用户id的集合
     * @return
     */
    List<Map<String, Object>> findUserRoleNameByUserIds(List<Long> userIds);

    /**
     * 新增用户和角色之间的关系
     * @param userId
     * @param roleIdList
     * @return
     */
    int insertUserToRoles(@Param("userId") Long userId, @Param("roleIdList") List<Long> roleIdList);

    /**
     * 根据用户id获取角色id的信息
     * @param userId : 用户id
     * @return
     */
    List<Long> findUserRoleIdByUserId(Long userId);

    /**
     * 根据用户id删除用户与角色的关系
     * @param userId 用户id
     */
    @Delete("DELETE FROM tb_user_role WHERE user_id = #{value}")
    void deleteUserRole(Long userId);
}
