package xifu.com.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 企业表对应的实体类
 * @auth wq on 2019/1/17 9:23
 **/
@Table(name = "tb_enterprise")
@Data
public class Enterprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT COMMENT '主键',
    @NotEmpty(message = "企业名称不能为空")
    private String name; // varchar(225) NOT NULL COMMENT '企业名称',
    private String description; // varchar(1000) DEFAULT NULL COMMENT '企业简介',
    private Long parentId; // bigint(16) DEFAULT NULL COMMENT '父ID, 用于确定上级企业',
    private String avatarPath; // varchar(255) DEFAULT NULL COMMENT '企业缩影图片',
    private String address; // varchar(255) DEFAULT NULL COMMENT '企业地址',
    private String contactPeople; // varchar(64) DEFAULT NULL COMMENT '联系人',
    @Pattern(regexp = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$", message = "手机格式不正确")
    private String contactPhone; // varchar(64) DEFAULT NULL COMMENT '联系方式',
    @Email(message = "邮箱格式不正确")
    private String email; // varchar(64) DEFAULT NULL COMMENT '邮箱',
    private Integer deviceLimit; // int(8) DEFAULT '0' COMMENT '接入设备限制数',
    private Integer userLimit; // int(6) DEFAULT '0' COMMENT '企业下属用户限制数',
    private Long createUserId; // bigint(16) DEFAULT NULL COMMENT '创建用户ID',
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建时间',
    private Long modifyUserId; // bigint(16) DEFAULT NULL COMMENT '修改用户ID',
    private Date modifyDate; // datetime DEFAULT NULL COMMENT '修改时间',
    private Double longitude; // decimal(10,6) DEFAULT NULL COMMENT '区域经度，用于地图汇聚',
    private Double latitude; // decimal(10,6) DEFAULT NULL COMMENT '纬度',
    private Double radius; // decimal(10,3) DEFAULT NULL COMMENT '区域半径，地图呈现区域范围大小',
    private String logo; // varchar(255) DEFAULT NULL COMMENT '企业LOGO',
}
