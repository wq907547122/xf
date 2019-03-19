package xifu.com.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息
 * @auth wq on 2019/3/14 9:50
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    // 用户id
    private Long id;
    /**
     * 用户登录的名称
     */
    private String loginName;
    /**
     * 登录用户的昵称
     */
    private String niceName;
    /**
     * 用户类型
     */
    private Byte userType;
    /**
     * 用户所属企业id
     */
    private Long enterpriseId;
    /**
     * 角色id
     */
    private List<Long> roleIds;
    /**
     * 权限的id信息
     */
    private List<Long> authIds;
    /**
     * 角色对应权限的资源
     */
    private List<String> resourceIds;
}
