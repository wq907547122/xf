package xifu.com.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 白名单信息
 * @auth wq on 2019/1/15 16:14
 **/
@Data
@ConfigurationProperties(prefix = "xifu.params")
public class WhiteInfos {
    // 白名单
    List<String> whiteList;
    // 黑名单
    List<String> blackList;
}
