package xifu.com.dto;

/**
 * 用户与角色之间的中间表
 * @auth wq on 2019/1/24 14:40
 **/
public class RoleAuthDto {
    private Long roleId; // 角色id
    private Long authId; // 权限id

    public RoleAuthDto(Long roleId, Long authId) {
        this.roleId = roleId;
        this.authId = authId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
}
