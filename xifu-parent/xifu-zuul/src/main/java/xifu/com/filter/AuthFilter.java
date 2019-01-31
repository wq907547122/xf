package xifu.com.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import xifu.com.config.WhiteInfos;
import xifu.com.constant.RedisConstants;
import xifu.com.pojo.User;
import xifu.com.utils.JsonUtils;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录权限验证的请求信息
 * @auth wq on 2019/1/15 16:11
 **/
@Component
@EnableConfigurationProperties(WhiteInfos.class)
public class AuthFilter extends ZuulFilter{
    @Autowired
    private WhiteInfos prop;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String servletPath = request.getServletPath();
        // 白名单不需要鉴权,就不需要去做过滤的事情
//        return !prop.getWhiteList().contains(servletPath);
        return false; // TODO 先不鉴权，后面需要修改这里
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String servletPath = request.getServletPath();

        // 验证用户是否有tokenId信息
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if(StringUtils.isBlank(tokenId)) {
            noAuth(ctx, null);
            return null;
        }
        // 获取redis中当前用户的登录的信息
        String redisKey = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId; // 拼接对应的redis的key
        String userStr = redisTemplate.opsForValue().get(redisKey); // 获取登录用户的信息：角色ids、权限id、具有的资源url集合
        if(StringUtils.isBlank(userStr)) { // 如果用户没有对应的token
            noAuth(ctx, tokenId);
            return null;
        }
        // 将json字符串转换为鉴权的信息
        AuthUserInfo user = JsonUtils.jsonToPojo(userStr, AuthUserInfo.class);
        if(user == null) { // 如果用户是空,直接返回没有权限的界面
            noAuth(ctx, tokenId);
            return null;
        }
        // TODO 看是否需要在这里设置过期时间,可以使用固定的15分钟自动过期
        redisTemplate.expire(redisKey, RedisConstants.TOKEN_EXPIRED, TimeUnit.MILLISECONDS); // 设置过期时间,这里是15分钟过期
        if(user.getId() == 1) { // 系统管理员
            return null;
        }
        // 如果没有对应的请求资源信息，就没有权限请求
        if(hasAuth(servletPath, user.getResourceIds())) {
            // 1.注销用户
            noAuth(ctx, tokenId);
        }
        return null;
    }

    /**
     * 判断是否具有权限
     * @param path
     * @param auths
     * @return
     */
    private boolean hasAuth(String path, List<String> auths) {
        if (CollectionUtils.isEmpty(auths)) {
            return false;
        }
        for (String auth : auths) {
            if(StringUtils.startsWith(path, auth)) {
                return true;
            }
        }
        return false;
    }

    private void noAuth(RequestContext ctx, String tokenId) {
        if(tokenId != null) { // 这里需要判断是否需要做移除对应的缓存，需要前端和后端来处理
            redisTemplate.delete(tokenId); // TODO 确定是否需要 移除对应的登录信息
        }
        // 过滤该请求，不对其进行路由
        ctx.setSendZuulResponse(false);
        //返回错误代码
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value()); // 无权限的鉴权
    }

}
