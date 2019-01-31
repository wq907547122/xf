package xifu.com.dto;

import lombok.Data;
import xifu.com.vo.PageRequestInfo;

/**
 * 用户查询分页的
 * @auth wq on 2019/1/25 15:09
 **/
@Data
public class UserRequest extends PageRequestInfo {
    private String loginName; // 登录名称
    private String phone; // 电话号码
    private Long userId; // 登录用户的id
    private Byte userType; // 用户类型：0:企业用户 1:注册用户 2：系统管理员
    private Long enterpriseId; // 企业id
}
