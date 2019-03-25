package xifu.com.pojo.devService;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 版本对应的信号点信息
 * 一个版本对应一种信号点的模型
 * @auth wq on 2019/3/19 10:07
 **/
@Data
@Table(name = "tb_dev_version_signal")
public class DevVersionSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT,
    private String signalName; // varchar(64) DEFAULT NULL COMMENT '信号点名称，是不能做修改的',
    private Integer signalAddress; // int(8) DEFAULT NULL COMMENT '信号点的寄存器地址',
    private String protocolCode; // varchar(64) DEFAULT NULL COMMENT '协议编码',
    private String modelVersionCode; // varchar(64) DEFAULT '' COMMENT '版本的编号',
    private String signalAlias; // varchar(64) DEFAULT '' COMMENT '信号别名，主要是展示用，可以给前台修改和展示',
    private String signalUnit; // varchar(32) DEFAULT '' COMMENT '单位',
    private Integer signalType; // int(8) DEFAULT NULL COMMENT '1：遥测；2：单点遥信；3：双点遥信；4：单点遥控；5：双点遥控,6：遥脉；7：遥调；8：事件；9：告警',
    private Integer teleType; // int(8) DEFAULT '1' COMMENT ' 1:变位信号  2：异常告警  3：保护事件  4：通知状态  5：告警信息',
    private Integer dataType; // int(8) DEFAULT NULL COMMENT '1：无符号整数；2：有符号整数；3：浮点数；4：字符串；5：时间',
    private Double gain; // double DEFAULT NULL COMMENT '增益',
    private Double offset; // double DEFAULT NULL COMMENT '偏移量',
    private Byte registerNum; // smallint(2) DEFAULT NULL COMMENT '寄存器个数',
    private Integer signalGroup; // int(8) DEFAULT NULL COMMENT '主信号地址，103的groupid',
    private Integer bit; // int(8) DEFAULT NULL,
    private Date createDate; // datetime DEFAULT NULL COMMENT '创建日期',
    private Date modifiedDate; //  datetime DEFAULT NULL COMMENT '修改日期',
}
