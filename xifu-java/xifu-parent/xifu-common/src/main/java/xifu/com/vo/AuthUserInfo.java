package xifu.com.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户登录之后保存到redis中的用户的信息
 * @auth wq on 2019/1/16 15:32
 **/
@Data
public class AuthUserInfo {
    /**
     * 用户id
     */
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
