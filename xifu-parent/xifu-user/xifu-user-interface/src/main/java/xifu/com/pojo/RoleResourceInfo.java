package xifu.com.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色对应的资源信息
 * @auth wq on 2019/1/16 16:19
 **/
@Data
@Table(name = "tb_resource_info")
public class RoleResourceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //                    bigint(16) not null auto_increment,
    private String resourceKey; //         varchar(64) not null comment '资源的key值，唯一',
    private Long authId; //              bigint(16) default NULL comment 't_auth权限表的ID',
    private String resourceName; //        varchar(64) default NULL comment '资源名称',
    private String description; //          varchar(255) default NULL comment '资源描述',
    private String authUrl; //             varchar(255) not null comment '资源的URL路径',
    private String requestType; //         varchar(16) default NULL comment '资源的请求类型 POST:post请求 GET:get请求 PUT:put请求 DELETE:delete请求',
}
