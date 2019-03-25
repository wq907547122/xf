package xifu.com.pojo.devService;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 告警模型的表数据
 * @auth wq on 2019/3/19 10:22
 **/
@Data
@Table(name = "tb_dev_alarm_model")
public class DevAlarmModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigint(16) NOT NULL AUTO_INCREMENT COMMENT '映射建，唯一识别。用于系统内部。',
    private Long enterpriseId; // bigint(16) DEFAULT NULL COMMENT '企业编号',
    private String stationCode; // varchar(64) DEFAULT NULL COMMENT '电站编号',
    private String modelVersionCode; // varchar(64) NOT NULL COMMENT '归属型号版本编号',
    private Integer alarmId; // int(11) DEFAULT NULL COMMENT '告警编号:点表中提供的告警编号，该编号不是由系统生成的，而是来源于点表的，稳定的字典信息',
    private Long modelId; // bigint(11) DEFAULT NULL COMMENT '信号点模型id',
    private Integer causeId; // int(11) DEFAULT NULL COMMENT '原因id:来源于点表',
    private Integer devTypeId; // smallint(6) DEFAULT NULL COMMENT '设备类型id',
    private Byte severityId; // smallint(2) NOT NULL COMMENT '告警级别--  1:严重 2：一般 3：提示',
    private String metroUnit; // varchar(32) DEFAULT NULL COMMENT '计量单位:要求使用英文规范的计量单位信息',
    private String alarmName; // varchar(128) DEFAULT NULL COMMENT '告警名称',
    private String alarmSubName; // varchar(128) DEFAULT NULL,
    private Integer sigAddress; // int(8) DEFAULT NULL COMMENT '信号地址：信号地址是和具体设备相关的某一个信号的存取地址，一般的规范，比如104，modbus等都有这个概念',
    private Integer bitIndex; // tinyint(4) DEFAULT NULL COMMENT '信号bit位位置：有的信号会占据某一个寄存器的一个位，这个时候需要用到该信息。确定一个信号到底是取一个bit位还是取几个寄存器，需要根据信号的数据类型来确定',
    private String alarmCause; // varchar(2000) DEFAULT NULL,
    private String repairSuggestion; // varchar(2000) DEFAULT NULL COMMENT '修复建议',
    private Integer alarmType; //  tinyint(4) DEFAULT NULL COMMENT '告警类型, 1:告警，2：时间，3：自检',
    private Integer teleType; // tinyint(4) DEFAULT '2' COMMENT '替代alarmtype 展示，遥信信号拆分为  1:变位信号  2：异常告警  3：保护事件  4：通信状态  5：告知信息',
    private Boolean isSubscribed; // tinyint(1) DEFAULT '0' COMMENT '是否推送',
    private Boolean isDeleted; // tinyint(1) DEFAULT '0' COMMENT '是否被删除，用户删除信号点模型时该列置为true',
    private Boolean isStationLev; // tinyint(1) DEFAULT '0' COMMENT '是否为电站级别设置，该级别具有最高优先级，不会被托管域及二级域设置覆盖',
    private Integer sourceId; // tinyint(4) DEFAULT '1' COMMENT '告警来源， 一般告警，遥测告警，bit位告警等',
    private Long updateUserId; // bigint(16) DEFAULT NULL COMMENT '更新用户',
    private Date updateDate; // timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
}
