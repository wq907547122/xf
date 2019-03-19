package xifu.com.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * 密码加密和解密的方式
 * @auth wq on 2019/1/21 10:58
 **/
public class CodecUtil {

    /**
     * 密码的加密算法
     * @param password
     * @param salt
     * @return
     */
    public static String md5Hex(String password, String salt) {
        if(StringUtils.isBlank(salt)) {
            salt = password.hashCode() + "";
        }
        return DigestUtils.md5Hex(DigestUtils.md5Hex(password) + salt);
    }

    /**
     * sha的加密方式
     * @param password
     * @param salt
     * @return
     */
    public static String shaHex(String password, String salt) {
        if(StringUtils.isBlank(salt)) {
            salt = password.hashCode() + "";
        }
        return DigestUtils.sha512Hex(DigestUtils.sha512Hex(password) + salt);
    }

    /**
     * 获取随机的盐值
     * @return
     */
    public static String generaSalt() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}
