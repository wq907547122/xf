package xifu.com.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import xifu.com.vo.UserInfo;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auth wq on 2019/3/14 9:51
 **/
public class JwtUtils {
    /**
     *私钥加密token
     *
     *@param userInfo 载荷中的数据
     *@param privateKey 私钥
     *@param expireMinutes 过期时间，单位秒
     *@return
     *@throws Exception
     */
    public static String generateToken(UserInfo userInfo, PrivateKey privateKey, int expireMinutes)throws Exception{
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_ID,userInfo.getId())
                .claim(JwtConstans.JWT_KEY_LOGIN_NAME,userInfo.getLoginName())
                .claim(JwtConstans.JWT_KEY_NICE_NAME, userInfo.getNiceName())
                .claim(JwtConstans.JWT_KEY_ENTERPRISE_ID, userInfo.getEnterpriseId())
                .claim(JwtConstans.JWT_KEY_USER_TYPE, userInfo.getUserType())
                .claim(JwtConstans.JWT_KEY_ROLE_IDS, listToString(userInfo.getRoleIds()))
                .claim(JwtConstans.JWT_KEY_AUTH_IDS, listToString(userInfo.getAuthIds()))
                .claim(JwtConstans.JWT_KEY_RESOURCE_IDS, (userInfo.getResourceIds() == null || userInfo.getResourceIds().isEmpty()) ? "" :
                        userInfo.getResourceIds().stream().collect(Collectors.joining(",")))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256,privateKey)
                .compact();
    }

    /**
     * 将long的list转换为字符串
     * @param list
     * @return
     */
    private static String listToString(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(item -> item.toString()).collect(Collectors.joining(","));
    }

    /**
     *私钥加密token
     *
     *@param userInfo 载荷中的数据
     *@param privateKey 私钥字节数组
     *@param expireMinutes 过期时间，单位秒
     *@return
     *@throws Exception
     */
    public static String generateToken(UserInfo userInfo,byte[]privateKey,int expireMinutes)throws Exception{
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_ID,userInfo.getId())
                .claim(JwtConstans.JWT_KEY_LOGIN_NAME,userInfo.getLoginName())
                .claim(JwtConstans.JWT_KEY_NICE_NAME, userInfo.getNiceName())
                .claim(JwtConstans.JWT_KEY_ENTERPRISE_ID, userInfo.getEnterpriseId())
                .claim(JwtConstans.JWT_KEY_USER_TYPE, userInfo.getUserType())
                .claim(JwtConstans.JWT_KEY_ROLE_IDS, listToString(userInfo.getRoleIds()))
                .claim(JwtConstans.JWT_KEY_AUTH_IDS, listToString(userInfo.getAuthIds()))
                .claim(JwtConstans.JWT_KEY_RESOURCE_IDS, (userInfo.getResourceIds() == null || userInfo.getResourceIds().isEmpty()) ? "" :
                        userInfo.getResourceIds().stream().collect(Collectors.joining(",")))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256,RsaUtils.getPrivateKey(privateKey))
                .compact();
    }

    /**
     *公钥解析token
     *
     *@param token 用户请求中的token
     *@param publicKey 公钥
     *@return
     *@throws Exception
     */
    private static Jws<Claims> parserToken(String token, PublicKey publicKey){
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    /**
     *公钥解析token
     *
     *@param token 用户请求中的token
     *@param publicKey 公钥字节数组
     *@return
     *@throws Exception
     */
    private static Jws<Claims> parserToken(String token, byte[]publicKey)throws Exception{
        return Jwts.parser().setSigningKey(RsaUtils.getPublicKey(publicKey))
                .parseClaimsJws(token);
    }

    /**
     *获取token中的用户信息
     *
     *@param token 用户请求中的令牌
     *@param publicKey 公钥
     *@return 用户信息
     *@throws Exception
     */
    public static UserInfo getInfoFromToken(String token,PublicKey publicKey) throws Exception{
        Jws<Claims> claimsJws=parserToken(token,publicKey);
        return getUserInfo(claimsJws);
    }

    private static UserInfo getUserInfo(Jws<Claims> claimsJws) {
        Claims body=claimsJws.getBody();
        UserInfo user = new UserInfo();
        Long id = ObjectUtils.toLong(body.get(JwtConstans.JWT_KEY_ID));
        if (id == null) { // 如果用户没有id就直接返回 null
            return null;
        }
        user.setId(id);
        user.setLoginName(ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_LOGIN_NAME)));
        user.setNiceName(ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_NICE_NAME)));
        user.setUserType(ObjectUtils.toByte(body.get(JwtConstans.JWT_KEY_USER_TYPE)));
        user.setEnterpriseId(ObjectUtils.toLong(body.get(JwtConstans.JWT_KEY_ENTERPRISE_ID)));
        user.setRoleIds(ObjectUtils.toLongList(body.get(JwtConstans.JWT_KEY_ROLE_IDS)));
        user.setAuthIds(ObjectUtils.toLongList(body.get(JwtConstans.JWT_KEY_AUTH_IDS)));
        user.setResourceIds(ObjectUtils.toStringList(body.get(JwtConstans.JWT_KEY_RESOURCE_IDS)));
        return user;
    }

    /**
     *获取token中的用户信息
     *
     *@param token 用户请求中的令牌
     *@param publicKey 公钥字节数组
     *@return 用户信息
     *@throws Exception
     */
    public static UserInfo getInfoFromToken(String token, byte[]publicKey)throws Exception{
        Jws<Claims> claimsJws=parserToken(token,publicKey);
        return getUserInfo(claimsJws);
    }
}
