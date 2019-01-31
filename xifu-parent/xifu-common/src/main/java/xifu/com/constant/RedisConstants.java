package xifu.com.constant;

/**
 * redis的常量
 * @auth wq on 2019/1/15 15:01
 **/
public interface RedisConstants {
    /**
     * 用户登录后保存的在redis的常量前缀
     */
    String PREFIX_LOGIN_USER_TOKEN = "token:user:";
    /**
     * 前端请求过来的request中获取tokenId的key
     */
    String REQUEST_TOKEN_ID = "tokenId";
    /**
     * token的过期时间 如果没有非白名单的请求15分钟后就过期单位ms
     */
    Long TOKEN_EXPIRED = 15 * 60 * 1000L; // 1 * 24 * 60 * 60 * 1000L;
}
