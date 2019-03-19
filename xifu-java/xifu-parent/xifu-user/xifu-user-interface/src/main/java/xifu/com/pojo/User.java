package xifu.com.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 用户表信息
 * @auth wq on 2019/1/3 15:04
 **/
@Table(name = "tb_user")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 4, max = 32, message = "用户名必须为4-32位")
    private String loginName; // 用户登录名称
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 32, message = "密码必须为4-32位")
    @JsonIgnore // 查询的时候忽略返回给web的字段
    private String password; // 密码
    @JsonIgnore // 查询的时候忽略返回给web的字段
    private String salt; // 密码的盐值
    private String niceName; // 昵称
    private String phone; // 电话号码
    private String email; // 邮箱
    private Character gender; // 性别 0: 男 1：女
    private Byte status; // 状态  0：正常 1：禁用
    private Byte userType; // 用户类型 0:企业用户 1:注册用户 2：系统管理员
    private Long enterpriseId; // 用户所属的企业ID enterpriseId
    private String qq; // QQ号码
    private Long createUserId; //       bigint(16) null comment '创建用户id',
    private Date createDate; //          datetime null comment '创建时间',
    private Long modifyUserId; //       bigint(16) null comment '修改时间',
    private Date modifyDate; //          datetime null comment '修改时间',
    @Transient // 不持久化到数据库,短暂的
    private String enterpriseName; // 企业名称
    @Transient
    private String roleNames; // 用户所属的角色的名称，名称之间使用英文逗号隔开
    @Transient
    private String roleIds; // 用户所属角色的id
    @Transient
    private String stationCodes; // 用户管理的电站编号
}
