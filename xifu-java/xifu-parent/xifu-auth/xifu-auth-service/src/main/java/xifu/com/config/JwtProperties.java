package xifu.com.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import xifu.com.utils.RsaUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * jwt需要的配置信息，通过配置文件获取
 * @auth wq on 2019/3/14 10:06
 **/
@Data
@ConfigurationProperties("xifu.jwt")
public class JwtProperties {
    private String secret; // 登录校验的密钥
    private String pubKeyPath; // 公钥文件的路径
    private String priKeyPath; // 私钥文件的路径
    private Integer exprie; // 加密的过期时间
    private String cookieName; // cookie中存储用户登录信息的key

    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥


    /**
     * 对象一旦实例化后，就应该读取公钥和私钥
     * 如果公钥和私钥没有，就生成公钥和私钥的文件
     * @throws Exception
     */
    @PostConstruct //构造函数执行完成后执行
    public void init()throws Exception{
        //公钥私钥不存在，先生成
        File pubkeyPath=new File(pubKeyPath);
        File prikeyPath=new File(priKeyPath);
        if(!pubkeyPath.exists()||!prikeyPath.exists()){
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }
        //读取公钥和私钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey=RsaUtils.getPrivateKey(priKeyPath);
    }
}
