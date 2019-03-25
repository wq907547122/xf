package xifu.com.pojo.devService;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 设备的实体表
 * @auth wq on 2019/3/19 9:44
 **/
@Data
@Table(name = "tb_dev_info")
public class DevInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  bigint(16) NOT NULL AUTO_INCREMENT COMMENT '设备id',
    private String devName; // varchar(64) DEFAULT '' COMMENT '设备名称',
    private String devAlias; // varchar(64) DEFAULT '' COMMENT '设备别名',
    private String stationCode; // varchar(64) DEFAULT NULL COMMENT '电站编码',
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业编码',
    private Long modelVersionId; // bigint(16) DEFAULT '' COMMENT '设备的版本模型的id',
    private String modelVersionCode; // varchar(64) DEFAULT '' COMMENT '点表版本，唯一标识',
    private Long parentModelVersionId; // bigint(16) DEFAULT NULL COMMENT '主设备信号版本id',
    private Integer devTypeId; // int(8) DEFAULT NULL COMMENT '设备类型id',
    private Long parentId; // bigint(16) DEFAULT NULL COMMENT '父设备id，针对通管机下挂类型',
    private String parentSn; // varchar(32) DEFAULT NULL COMMENT '父设备esn',
    private Long relatedDevId; // bigint(16) DEFAULT NULL COMMENT '关联设备id',
    private Long matrixId; // bigint(16) DEFAULT NULL COMMENT '子阵id',
    private Long phalanxId; // bigint(16) DEFAULT NULL COMMENT '方阵id',
    private String snCode; // varchar(32) DEFAULT NULL COMMENT 'esn号',
    private String pnCode; // varchar(32) DEFAULT NULL COMMENT 'pn码,生产制造厂商',
    private String kksCode; // varchar(32) DEFAULT NULL COMMENT 'kks编码，在地面站中有用到',
    private String neVersion; // varchar(32) DEFAULT '' COMMENT '网元版本号',
    private String devIp; // varchar(32) DEFAULT '' COMMENT '设备ip',
    private Integer devPort; // int(4) DEFAULT NULL COMMENT '端口号',
    private String linkedHost; // varchar(32) DEFAULT '' COMMENT '接入服务器的host，针对分布式场景到字段',
    private Integer secondAddress; // int(4) DEFAULT NULL COMMENT '二级地址,通常在交互报文中带有此地址',
    private String protocolCode; // varchar(20) DEFAULT '' COMMENT '协议编码:104 103 hwmodbus',
    private Double longitude; // decimal(10,6) DEFAULT NULL COMMENT '经度',
    private Double latitude; // decimal(10,6) DEFAULT NULL COMMENT '纬度',
    private Boolean isLogicDelete; // tinyint(1) DEFAULT '0' COMMENT '设备是否被逻辑删除 0表示否',
    private String oldSn; // varchar(32) DEFAULT '' COMMENT '记录设备替换时换掉的设备esn',
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建日期',
    private Date modifiedDate; // datetime DEFAULT NULL COMMENT '修改日期',
    private Boolean isMonitorDev; // tinyint(1) DEFAULT 0 COMMENT '设备是否来源于监控 1:监控导入设备的 ；0：集维侧的设备',
}
