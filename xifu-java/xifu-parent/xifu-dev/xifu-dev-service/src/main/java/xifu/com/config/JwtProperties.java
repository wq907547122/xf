package xifu.com.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xifu.com.utils.RsaUtils;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @auth wq on 2019/3/14 13:36
 **/
@ConfigurationProperties("xifu.jwt")
@Data
public class JwtProperties {
    private String pubKeyPath; // 路径

    private PublicKey publicKey; // 公钥

    @PostConstruct //构造函数执行完成后执行
    public void init()throws Exception{
        //读取公钥和私钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
    }
}
