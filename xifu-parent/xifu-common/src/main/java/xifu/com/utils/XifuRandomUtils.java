package xifu.com.utils;

import java.util.Random;
import java.util.UUID;

/**
 * 生成随机的字符串的工具类
 * @auth wq on 2019/1/16 10:02
 **/
public class XifuRandomUtils {

    /**
     * 获取uuid的随机字符串
     * @return
     */
    public static String getUUIDString(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取固定长度的水机字符串的所有字符串的集合 10 + 26 + 26 = 62
     */
    private static final char[] charArr= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 获取指定位数的随机字符串
     * @param len: 需要生成的数据的位数
     * @return
     */
    public static String getRandomStrByLen(int len){
        if(len <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int size = charArr.length; // 获取当前的数组的字符集长度
        Random random=new Random();
        for(int i = 0; i < len; i++){
            stringBuffer.append(charArr[random.nextInt(size)]);
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        String randomStrByLen = getRandomStrByLen(128);
        System.out.println("randomStrByLen = " + randomStrByLen);
    }
}
