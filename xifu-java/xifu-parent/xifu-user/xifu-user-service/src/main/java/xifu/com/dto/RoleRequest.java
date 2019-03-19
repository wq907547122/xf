package xifu.com.dto;

import lombok.Data;
import xifu.com.vo.PageRequestInfo;

import java.util.Date;
import java.util.List;

/**
 * 前端查询用户的信息
 * @auth wq on 2019/1/23 14:31
 **/
@Data
public class RoleRequest extends PageRequestInfo {
    private Long enterpriseId; // 企业id
    private String name; // 角色名称
    private Date startDate; // 创建开始时间
    private Date endDate; // 创建结束时间
    private List<Long> roleIds; // 登录用户具有的角色id集合
    private Byte userType; // 登录用户的类型 0: 企业用户 1：普通用户 2：超级用户
}
