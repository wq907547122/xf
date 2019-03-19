package xifu.com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Java与json字符串的互转
 * @auth wq on 2019/1/15 15:06
 **/
public class JsonUtils {
    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * 将java对象转换为json字符串
     * @return
     */
    public static String toJson(Object org){
        try {
            return MAPPER.writeValueAsString(org);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    /**
     * 将json结果集转化为对象
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> clazz) {
        try {
            T t = MAPPER.readValue(jsonData, clazz);
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            return null;
        }
    }

}
