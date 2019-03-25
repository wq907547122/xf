package xifu.com.intercepters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import xifu.com.config.JwtProperties;
import xifu.com.constant.RedisConstants;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.utils.JwtUtils;
import xifu.com.vo.UserInfo;

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
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();
    private StringRedisTemplate redisTemplate;
    private JwtProperties prop;
    /**
     * 不用登录的白名单
     */
    private static final List<String> NO_INTERSEPTOR_LIST = Arrays.asList(
            "/list"
    );
    // 私有构造方法
    private UserAuthInterceptor(UserAuthBuilder builder) {
        this.redisTemplate = builder.getRedisTemplate();
        this.prop = builder.getProp();
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
            token = request.getParameter(RedisConstants.REQUEST_TOKEN_ID); // 获取请求参数的配置信息
            if (StringUtils.isBlank(token)) {
                log.error("请求未登录，请求路径：{}", servletPath);
                throw new XiFuException(ExceptionEnum.NO_AUTH);
            }
        }
        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            if (infoFromToken == null) { // 如果没有登录用户
                log.error("请求未登录或登录已超时，请求路径：{}", servletPath);
                throw new XiFuException(ExceptionEnum.NO_AUTH);
            }
            THREAD_LOCAL.set(infoFromToken);
            return true;
        } catch (Exception e) {
            log.error("请求数据错误或者已经超时，请求路径：{}, 异常信息: {}", servletPath, e);
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }

//        String userStr = redisTemplate.opsForValue().get(RedisConstants.PREFIX_LOGIN_USER_TOKEN + token);
//        if (StringUtils.isBlank(userStr)) {
//            log.error("请求未登录或登录已超时，请求路径：{}", servletPath);
//            throw new XiFuException(ExceptionEnum.NO_AUTH);
//        }
//        AuthUserInfo authUserInfo = JsonUtils.jsonToPojo(userStr, AuthUserInfo.class);
//        if (authUserInfo == null) {
//            log.error("请求未登录或登录已超时，请求路径：{}", servletPath);
//            throw new XiFuException(ExceptionEnum.NO_AUTH);
//        }
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

    public static UserInfo getLoginUser() {
        return THREAD_LOCAL.get();
    }


    public static class UserAuthBuilder {
        private StringRedisTemplate redisTemplate;
        private JwtProperties prop;

        public UserAuthBuilder(){}
        public UserAuthBuilder(StringRedisTemplate redisTemplate){
            this.redisTemplate = redisTemplate;
        }
        public UserAuthBuilder redisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }
        public UserAuthBuilder prop(JwtProperties prop) {
            this.prop = prop;
            return this;
        }
        public UserAuthInterceptor build() {
            return new UserAuthInterceptor(this);
        }

        public StringRedisTemplate getRedisTemplate() {
            return redisTemplate;
        }

        public JwtProperties getProp() {
            return prop;
        }
    }
}
