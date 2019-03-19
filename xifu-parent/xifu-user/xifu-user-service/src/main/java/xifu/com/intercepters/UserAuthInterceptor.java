package xifu.com.intercepters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import xifu.com.constant.RedisConstants;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.utils.JsonUtils;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 用户信息的拦截器,之后需要获取用户的信息可以通过UserAuthInterceptor.getLoginUser()
 * @auth wq on 2019/1/29 10:05
 **/
@Slf4j
public class UserAuthInterceptor implements HandlerInterceptor {
    // 这个对象是在request请求做线程隔离的，每一个请求的请求线程是相同的,资源共享
    private static final ThreadLocal<AuthUserInfo> THREAD_LOCAL = new ThreadLocal<>();
    private StringRedisTemplate redisTemplate;
    /**
     * 不用登录的白名单
     */
    private static final List<String> NO_INTERSEPTOR_LIST = Arrays.asList(
            "/login", "/logout", "/check/ph/", "/queryUser", "/queryUser2", "/list", "/upload"
    );
    // 私有构造方法
    private UserAuthInterceptor (UserAuthBuilder builder) {
        this.redisTemplate = builder.getRedisTemplate();
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        for (String w : NO_INTERSEPTOR_LIST) { // 如果是白名单的不需要做拦截
            if (StringUtils.startsWith(servletPath, w)) {
                return true;
            }
        }
        String token = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if (StringUtils.isBlank(token)) { // 如果获取的用户信息是空的
            log.error("请求未登录，请求路径：{}", servletPath);
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        String userStr = redisTemplate.opsForValue().get(RedisConstants.PREFIX_LOGIN_USER_TOKEN + token);
        if (StringUtils.isBlank(userStr)) {
            log.error("请求未登录或登录已超时，请求路径：{}", servletPath);
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        AuthUserInfo authUserInfo = JsonUtils.jsonToPojo(userStr, AuthUserInfo.class);
        if (authUserInfo == null) {
            log.error("请求未登录或登录已超时，请求路径：{}", servletPath);
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        THREAD_LOCAL.set(authUserInfo);
        return true;
    }

    /**
     * 请求完成释放资源
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        try {
            THREAD_LOCAL.remove();
        }catch (Exception e) {

        }
    }

    public static AuthUserInfo getLoginUser() {
        return THREAD_LOCAL.get();
    }


    public static class UserAuthBuilder {
        private StringRedisTemplate redisTemplate;

        public UserAuthBuilder(){}
        public UserAuthBuilder(StringRedisTemplate redisTemplate){
            this.redisTemplate = redisTemplate;
        }
        public UserAuthBuilder redisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }
        public UserAuthInterceptor build() {
            return new UserAuthInterceptor(this);
        }

        public StringRedisTemplate getRedisTemplate() {
            return redisTemplate;
        }
    }
}
