package xifu.com.dto;

import lombok.Data;
import xifu.com.vo.PageRequestInfo;

import java.util.Date;

/**
 * 用于查询的电站的Dto对象,查询对象
 * @auth wq on 2019/1/30 10:46
 **/
@Data
public class StationRequest extends PageRequestInfo {
    // 企业id
    private Long enterpriseId;
    private Long domainId; // 选择区域的id
    // 电站名称
    private String stationName;
    private Date produceDate; // 并网时间 即投产时间
    private Byte onlineType; // 并网类型 1:地面式 2:分布式 3:户用
    private Byte inverterType; // 逆变器类型 0:集中式 1:组串式 2:户用
    private Byte stationBuildStatus; // 电站状态：1:并网 2:在建 3:规划
    private Long userId; // 当前登录的用户
    private Byte userType; // 当前登录的用户类型 // 用户类型：0:企业用户 1:注册用户 2：系统管理员
}
