package xifu.com.pojo.devService;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 设备对应的信号点信息
 * @auth wq on 2019/3/19 10:15
 **/
@Data
@Table(name = "tb_dev_signal")
public class DevSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT COMMENT '信号点唯一id，全库唯一',
    private Long devId; // bigint(16) DEFAULT NULL COMMENT '设备id',
    private String signalName; // varchar(64) NOT NULL COMMENT '信号实例名称',
    private String signalAlias; // varchar(64) DEFAULT '' COMMENT '信号别名',
    private String signalUnit; // varchar(32) DEFAULT NULL COMMENT '单位',
    private Integer registerType; // int(8) DEFAULT NULL COMMENT '寄存器类型',
    private Integer bit; // int(8) DEFAULT NULL COMMENT 'bit位',
    private String modelVersionCode; // varchar(64) DEFAULT '' COMMENT '点表版本',
    private Integer signalGroup; // int(8) DEFAULT '0' COMMENT '主信号地址，103的groupid',
    private Integer signalAddress; // int(8) NOT NULL DEFAULT '0' COMMENT '信号点地址',
    private Byte registerNum; // smallint(2) DEFAULT '0' COMMENT 'modbus协议的寄存器个数',
    private Double gain; // decimal(10,6) DEFAULT NULL COMMENT '增益',
    private Double offset; // decimal(12,6) DEFAULT NULL COMMENT '偏移量',
    private Integer signalType; // int(8) DEFAULT NULL COMMENT '1：遥测；2：单点遥信；3：双点遥信；4：单点遥控；5：双点遥控,6：遥脉；7：遥调；8：事件；9：告警； 15：线圈；16：离散输入；17：保持寄存器；18：输入寄存器',
    private Long modelId; // bigint(16) DEFAULT NULL COMMENT '模型点表的id,对应版本的信号点的id',
    private Integer dataType; // int(8) DEFAULT NULL COMMENT '1：无符号整数；2：有符号整数；3：浮点数；4：字符串；5：时间',
    private Boolean isAlarmFlag; // tinyint(1) DEFAULT '0' COMMENT '1表示该信号点为遥测告警标志位， 0表示否',
    private Boolean isAlarmVal; // tinyint(1) DEFAULT '0' COMMENT '1表示该信号点为遥测告警有效值，0表示否',
    private Boolean isLimited; // tinyint(1) DEFAULT '0' COMMENT '是否存在数据范围设置',
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建日期',
    private Date modifiedDate; // datetime DEFAULT NULL COMMENT '修改日期',
    @Transient
    private String devName; // 设备名称，不入库,主要是做点表导入的时候做一个存储转换的作用
}
