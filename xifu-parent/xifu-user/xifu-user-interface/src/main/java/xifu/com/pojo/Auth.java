package xifu.com.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色对应的权限表
 * @auth wq on 2019/1/16 16:34
 **/
@Table(name = "tb_auth")
@Data
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL comment '权限ID',
    private String authName; // varchar(64) DEFAULT NULL comment '权限名称',
    private String description; // varchar(255) DEFAULT NULL comment '权限描述',
    private Byte roleType; // smallint(1) DEFAULT '0' COMMENT '权限类型：0：web端(系统权限) 1：终端(app权限); 默认0'
    private Byte sysAuth; // smallint(1) DEFAULT '0' COMMENT '权限是否是系统预置权限:  1：预置  0：后来添加; 默认0'
}
