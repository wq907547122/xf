package xifu.com.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 区域表对应的实体类
 * @auth wq on 2019/1/22 14:04
 **/
@Table(name = "tb_domain_info")
@Data
public class DomainInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT,
    private String name; // varchar(64) NOT NULL COMMENT '区域名',
    private String description; // varchar(1000) DEFAULT NULL COMMENT '区域描述',
    private Long parentId; // bigint(16) DEFAULT NULL COMMENT '父ID',
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业编号',
    private Double longitude; // decimal(10,6) DEFAULT NULL COMMENT '区域经度，用于地图汇聚',
    private Double latitude; // decimal(10,6) DEFAULT NULL COMMENT '纬度',
    private Double radius; // decimal(10,3) DEFAULT NULL COMMENT '区域半径，地图呈现区域范围大小',
    private Double domainPrice; // decimal(7,3) DEFAULT NULL COMMENT '区域电价，计算收益是会使用',
    private String currency; // varchar(16) DEFAULT NULL COMMENT '货币：￥ $ €',
    private Long createUserId; // bigint(16) DEFAULT NULL,
    private Date createDate; // datetime DEFAULT NULL,
    private Long modifyUserId; // bigint(16) DEFAULT NULL,
    private Date modifyDate; // datetime DEFAULT NULL,
    private String path; // varchar(64) DEFAULT NULL,
    /**
     * 当前节点的子节点信息
     */
    @Transient
    private List<DomainInfo> children;
}
