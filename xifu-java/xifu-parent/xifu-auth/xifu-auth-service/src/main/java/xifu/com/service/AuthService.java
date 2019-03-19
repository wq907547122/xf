package xifu.com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import xifu.com.client.UserClient;
import xifu.com.config.JwtProperties;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.pojo.User;
import xifu.com.utils.JwtUtils;
import xifu.com.vo.AuthUserInfo;
import xifu.com.vo.UserInfo;

/**
 * 用户权限认证的service
 * @auth wq on 2019/2/12 13:44
 **/
@Service
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;

    /**
     * 用户登录
     * @param user
     * @return
     */
    public String login(User user) {
        AuthUserInfo authUserInfo = userClient.queryUser(user.getLoginName(), user.getPassword());
        if (authUserInfo == null) { // 登录失败
            throw new XiFuException(ExceptionEnum.USER_NAME_OR_PASSWORD_ERROR);
        }
        // 加密信息
        try {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(authUserInfo, userInfo);
            return JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExprie());
        } catch (Exception e) {
            log.error("[用户登录] 加密失败，请检查是否存在公钥");
            throw new XiFuException(ExceptionEnum.USER_NAME_OR_PASSWORD_ERROR);
        }
//        // 存储到reids中
//        String useJson = JsonUtils.toJson(authUserInfo); // 转换为字符串
//        String tokenId = XifuRandomUtils.getUUIDString(); // 获取uuid的字符串
//        String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
//        redisTemplate.opsForValue().set(key, useJson, RedisConstants.TOKEN_EXPIRED, TimeUnit.MILLISECONDS);
//        return tokenId;
    }
}
