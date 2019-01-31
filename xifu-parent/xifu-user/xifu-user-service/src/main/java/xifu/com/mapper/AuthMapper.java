package xifu.com.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import xifu.com.pojo.Auth;

import java.util.List;

/**
 * 角色对应权限表的mapper
 * @auth wq on 2019/1/16 16:36
 **/
public interface AuthMapper extends Mapper<Auth> {
    /**
     * 根据用户id获取用户的所有权限列表
     * @param userId
     * @return
     */
    @Select("SELECT * FROM tb_auth WHERE id in" +
            "(SELECT auth_id FROM tb_role_auth WHERE role_id in(SELECT role_id FROM tb_user_role WHERE user_id = #{value}))")
    List<Auth> findAuthByUserId(Long userId);
}
