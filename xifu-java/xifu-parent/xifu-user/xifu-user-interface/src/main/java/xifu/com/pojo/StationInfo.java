package xifu.com.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 电站表对应的实体类
 * @auth wq on 2019/1/30 9:35
 **/
@Data
@Table(name = "tb_station_info")
public class StationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    private String stationCode; // varchar(32) NOT NULL COMMENT '电站编号',
    private String stationName; // varchar(255) NOT NULL COMMENT '电站名称',
    private Double installedCapacity; // decimal(20,4) DEFAULT NULL COMMENT '装机容量kW',
    private Byte stationBuildStatus; // tinyint(4) DEFAULT NULL COMMENT '电站状态：1:并网 2:在建 3:规划',
    private Date produceDate; // date DEFAULT NULL COMMENT '投产时间',
    private Byte onlineType; // tinyint(4) DEFAULT NULL COMMENT '并网类型：1:地面式 2:分布式 3:户用',
    private Byte stationType; // tinyint(4) DEFAULT NULL COMMENT '电站类型：1:渔光、2:农光、3:牧光',
    private Byte inverterType; // tinyint(4) DEFAULT NULL COMMENT '逆变器类型：0:集中式 1:组串式 2:户用',
    private Double installAngle; // decimal(6,2) DEFAULT NULL COMMENT '安装角度',
    private Byte assemblyLayout; // tinyint(1) DEFAULT NULL COMMENT '组件布置方式：1:横排 2:竖排',
    private Double floorSpace; // decimal(16,6) DEFAULT NULL COMMENT '占地面积平方千米',
    private Double amsl; // decimal(20,2) DEFAULT NULL COMMENT '平均海拔',
    private Short lifeCycle; // tinyint(4) DEFAULT NULL COMMENT '计划运营年限',
    private Long safeRunDatetime; // bigint(16) DEFAULT NULL COMMENT '安全运行开始时间（精确到日的毫秒数）',
    private Boolean isPovertyRelief; // tinyint(1) DEFAULT '0' COMMENT '是否扶贫站：0:不是 1:是',
    private String stationFileId; // varchar(32) DEFAULT NULL COMMENT '电站缩略图',
    private String stationAddr; // varchar(255) DEFAULT NULL COMMENT '电站详细地址',
    private String stationDesc; // varchar(1000) DEFAULT NULL COMMENT '电站简介',
    private String contactPeople; // varchar(64) DEFAULT NULL COMMENT '联系人',
    private String phone; // varchar(32) DEFAULT NULL COMMENT '联系方式',
    private Double stationPrice; // decimal(7,3) DEFAULT NULL COMMENT '电价',
    private Double latitude; // decimal(12,6) DEFAULT NULL COMMENT '纬度',
    private Double longitude; // decimal(12,6) DEFAULT NULL COMMENT '经度',
    private Integer timeZone; // int(11) DEFAULT NULL COMMENT '电站所在时区',
    private String areaCode; // varchar(64) DEFAULT NULL COMMENT '行政区域编号（对应省市县编号以@符号连接）',
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业编号',
    private Long domainId; // bigint(16) DEFAULT NULL COMMENT '区域编号',
    private Boolean isDelete; // tinyint(1) DEFAULT '0' COMMENT '电站是否逻辑删除',
    private Long createUserId; // bigint(16) DEFAULT NULL,
    private Date createDate; // datetime DEFAULT NULL,
    private Long updateUserId; // bigint(16) DEFAULT NULL,
    private Date updateDate; // datetime DEFAULT NULL,
    private Boolean isMonitor; // tinyint(1) DEFAULT '0' COMMENT '设备是否来源于监控 1:监控的电站; 0：集维侧的电站'
    @Transient
    private Byte userType; // 用户类型，不持久化到数据库
}
