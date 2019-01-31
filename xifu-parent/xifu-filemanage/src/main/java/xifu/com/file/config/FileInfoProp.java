package xifu.com.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @auth wq on 2019/1/18 18:06
 **/
@Data
@ConfigurationProperties(prefix = "xifu.file")
public class FileInfoProp {
    String windowPath; // 文件windows的存储路径
    String linuxPath; // 文件linux的存储路径
    Integer fileMaxSize; // 文件的最大值
}
