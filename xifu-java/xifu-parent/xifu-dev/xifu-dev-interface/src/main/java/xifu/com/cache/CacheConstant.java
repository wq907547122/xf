package xifu.com.cache;

/**
 * 设备相关缓存的常量
 * @auth wq on 2019/3/19 15:15
 **/
public interface CacheConstant {
    // 设备微服务中所有缓存的前缀
    String CACHE_DEV_PREX_PATTERN = "dev:*";
    /**
     * 根据id获取缓存数据的常量，存储方式是简单的key:value的存储
     */
    String DEV_VERSION_ID_PREX = "dev:version:id:";
    /**
     * 设备版本号对应设备版本缓存数据的前缀常量，存储方式是简单的key:value的存储
     */
    String DEV_VERSION_CODE_PREX = "dev:version:code:";
    /**
     * 锁的后缀，防止多次查询数据库
     */
    String LOCKED_SUFFIX = ":locked";
    /**
     * 设备信息的前缀，存储方式是简单的key:value的存储
     */
    String DEV_ID_PREX = "dev:id:";
    /**
     * 设备名称的前缀，存储方式是简单的key:value的存储
     */
    String DEV_NAME_PREX = "dev:name:";
    /**
     * 设备版本信号点信息的缓存信息前缀
     * 存储的是hset <modelVersionCode, <modelSignalId, modelSignal>>
     */
    String MODEL_SIGNAL_ID_VERSION_PREX = "dev:model:signals:map:";
    /**
     * 设备信号点缓存的前缀
     * 存储是hset,存储模型结构为：<设备id, <信号点id, 信号点信息>>
     */
    String DEV_SIGNAL_PREX = "dev:signal:map:";
}
