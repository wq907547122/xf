package xifu.com.pojo;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 角色表
 * @auth wq on 2019/1/16 14:48
 **/
@Table(name = "tb_role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT,
    @NotEmpty(message = "角色名称不能为空")
    private String name; // varchar(255) NOT NULL COMMENT '角色名',
    private String roleType; // varchar(32) DEFAULT NULL COMMENT '角色类型: system:系统管理员角色 enterprise：企业管理员角色 operator：运维人员角色 normal：普通人员角色',
    private String description; // varchar(500) DEFAULT NULL COMMENT '角色描述',
    @NotNull(message = "角色状态不能为空")
    private Byte status; // tinyint(4) DEFAULT '0' COMMENT '角色状态：0:启用 1:禁用',
    @NotNull(message = "企业不能为空")
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业id',
    private Long createUserId; // bigint(16) DEFAULT NULL COMMENT '创建用户id',
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建时间',
    private Long modifyUserId; // bigint(16) DEFAULT NULL COMMENT '修改人id',
    private Date modifyDate; // datetime DEFAULT NULL COMMENT '修改时间',
    @Transient
    private List<Long> authIds; // 角色具有的权限
}
