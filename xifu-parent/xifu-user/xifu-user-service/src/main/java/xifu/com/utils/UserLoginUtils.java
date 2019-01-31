package xifu.com.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xifu.com.constant.RedisConstants;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前登录用户的登录信息
 * @auth wq on 2019/1/22 10:27
 **/
@Component
public class UserLoginUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 获取登录用户信息
     * @param request
     * @return
     */
    public AuthUserInfo getLoginInfo(HttpServletRequest request) {
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if(StringUtils.isBlank(tokenId)) {
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
        String authInfo = redisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(authInfo)) { // 登录已经过期或者无权限，就返回无权限的操作
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        AuthUserInfo authUserInfo = JsonUtils.jsonToPojo(authInfo, AuthUserInfo.class);
        if(authUserInfo == null) {
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        return authUserInfo;
    }
}
