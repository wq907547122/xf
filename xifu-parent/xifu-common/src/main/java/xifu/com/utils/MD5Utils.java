package xifu.com.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密的算法工具类
 * @auth wq on 2019/1/16 9:23
 **/
public class MD5Utils {
    public static final String KEY_MD5 = "MD5";

    /**
     * 获取存数字的MD5加密的39位的信息摘要
     * @param msg
     * @return
     */
    public static String getResult(String msg){
        BigInteger bigInteger=null;
        try{
            MessageDigest md = MessageDigest.getInstance(KEY_MD5);
            byte[] inputData = msg.getBytes();
            md.update(inputData);
            bigInteger = new BigInteger(md.digest());
        }catch (Exception e){
            return null;
        }
        return bigInteger.toString();
    }

    /**
     * 生成32位的md5加密的信息
     * @param password
     * @return
     */
    public static String md5Password(String password) {
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance(KEY_MD5);
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        String result = getResult("mytest");
        System.out.println("result = " + result);
        result = md5Password("admin@123interest");
        System.out.println("result = " + result);
        result = md5Password("admin");
        System.out.println("result = " + result);
    }
}
