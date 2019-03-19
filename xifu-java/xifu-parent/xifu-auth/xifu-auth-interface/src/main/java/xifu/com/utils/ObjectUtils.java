package xifu.com.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 从jwt解析得到的数据是Object类型，转换为具体类型可能出现空指针，这个工具类进行了一些转换
 * @auth wq on 2019/3/14 9:47
 **/
public class ObjectUtils {
    public static String toString(Object obj){
        if(obj==null){
            return null;
        }
        return obj.toString();
    }

    public static Long toLong(Object obj){
        if(obj==null){
            return 0L;
        }
        if(obj instanceof Double||obj instanceof Float){
            return Long.valueOf(StringUtils.substringBefore(obj.toString(),"."));
        }
        if(obj instanceof Number){
            return Long.valueOf(obj.toString());
        }
        if(obj instanceof String){
            return Long.valueOf(obj.toString());
        }else{
            return 0L;
        }
    }

    /**
     * 将以逗号分隔的字符串转换为Long集合
     * @param obj
     * @return
     */
    public static List<Long> toLongList(Object obj) {
        if(obj == null || !(obj instanceof  String)) {
            return null;
        }
        try {
            return Arrays.stream(
                    Arrays.stream(StringUtils.split(obj.toString(), ",")).mapToLong(s -> Long.valueOf(s)).toArray())
                    .boxed().collect(Collectors.toList());
        } catch (Exception e) { // 如果转换异常返回空字符串
            return null;
        }
    }

    /**
     * 将以逗号隔开的数据转换为list集合
     * @param obj
     * @return
     */
    public static List<String> toStringList(Object obj) {
        if(obj == null || !(obj instanceof  String)) {
            return null;
        }
        return Arrays.stream(
                StringUtils.split(obj.toString(), ",")
        ).collect(Collectors.toList());
    }

    public static Integer toInt(Object obj){
        return toLong(obj).intValue();
    }

    /**
     * 将字符串转换为byte类型
     * @param obj
     * @return
     */
    public static Byte toByte(Object obj) {
        return toLong(obj).byteValue();
    }
}
