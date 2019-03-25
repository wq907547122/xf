package xifu.com.cache;

/**
 * 设备相关缓存的常量
 * @auth wq on 2019/3/19 15:15
 **/
public interface CacheConstant {
    /**
     * 根据id获取缓存数据的常量
     */
    String DEV_VERSION_ID_PREX = "dev:version:id:";
    /**
     * 设备版本号对应设备版本缓存数据的前缀常量
     */
    String DEV_VERSION_CODE_PREX = "dev:version:code:";
    /**
     * 锁的后缀，防止多次查询数据库
     */
    String LOCKED_SUFFIX = ":locked";
    /**
     * 设备信息的前缀
     */
    String DEV_ID_PREX = "dev:id:";
    /**
     * 设备名称的前缀
     */
    String DEV_NAME_PREX = "dev:name:";
}
