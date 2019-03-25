package xifu.com.pojo.devService;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 设备的版本信息的实体表
 * @auth wq on 2019/3/19 9:58
 **/
@Data
@Table(name = "tb_dev_model_version")
public class DevModelVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT COMMENT '版本id',
    private Long parentId; // bigint(16) DEFAULT NULL COMMENT '父版本编码',
    private String name; // varchar(64) DEFAULT NULL COMMENT '版本名称',
    private String modelVersionCode; // varchar(64) NOT NULL COMMENT '版本的编号',
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业编码,归属企业,null表示属于共享信息',
    private String stationCode; // varchar(32) DEFAULT NULL COMMENT '电站编码',
    private Integer devTypeId; // int(8) NOT NULL DEFAULT '0' COMMENT '设备类型id',
    private String venderName; // varchar(32) NOT NULL DEFAULT '0' COMMENT '设备供应商名称',
    private String protocolCode; // varchar(20) NOT NULL COMMENT '协议编码:104,MQTT,modbus,TN(铁牛)',
    private String interfaceVersion; // varchar(20) DEFAULT '' COMMENT '接口协议版本',
    private Integer type; // tinyint(4) NOT NULL COMMENT '1 系统内置，2 用户导入 —内置数据不可删',
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建日期',
    private Date modifiedDate; // datetime DEFAULT NULL COMMENT '修改日期',
}
