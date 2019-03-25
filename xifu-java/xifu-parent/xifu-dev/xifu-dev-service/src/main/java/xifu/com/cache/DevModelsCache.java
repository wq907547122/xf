package xifu.com.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import xifu.com.mapper.DevInfoMapper;
import xifu.com.mapper.DevModelVersionMapper;
import xifu.com.pojo.devService.DevInfo;
import xifu.com.pojo.devService.DevModelVersion;
import xifu.com.utils.JsonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 设备模块的缓存信息
 * 主要缓存设备、版本、归一化、信号点
 * @auth wq on 2019/3/21 13:34
 **/
@Slf4j
@Component
public class DevModelsCache {
    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private DevModelVersionMapper devModelVersionMapper;
    @Autowired
    private DevInfoMapper devInfoMapper;
    private Long lockTime = 5 * 60 * 1000L; // 锁定时间5分钟,即5分钟内不查询数据库
    private Long cacheExpireTime = 24 * 60 * 60 * 1000L; // 过期时间为1天
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS; // 过期时间的单位 这里使用ms
    private String multKeySplit = "::"; // 多个字符串确定一个key的分割

    // =========== 设备版本缓存的相关代码 开始 ========>=======
    /**
     * 根据版本号获取缓存中的版本信息
     * 在redis中保存的信息是  modelVersionCode -> 找到当前的版本id -> 找到对应的版本信息
     * 如果数据为空是从数据库中查询，并且锁定查询数据库的数据在5分钟内不重新查询数据库
     * @param modelVersionCode 版本编号
     * @return
     */
    public DevModelVersion getDevVersionByCode(String modelVersionCode) {
        if (StringUtils.isBlank(modelVersionCode)) {
            return null;
        }
        return getDevModelVersion(modelVersionCode, false);
    }

    /**
     * 批量保存版本数据
     * @param list
     */
    public void batchInsertDevModelVersion(List<DevModelVersion> list) {
       for (DevModelVersion v : list) {
           setDevModelVersion(v);
       }
    }
    /**
     * 通过id查询数据版本信息
     * @param id 版本的唯一键id
     * @return
     */
    public DevModelVersion getDevVersionById(Long id) {
        if (id == null) {
            return null;
        }
        return getDevModelVersion(id.toString(), true);
    }
    /**
     * 保存设备版本的缓存信息
     * @param devModelVersion 需要保存的缓存内容
     */
    public void setDevModelVersion(DevModelVersion devModelVersion) {
        if (devModelVersion == null || devModelVersion.getId() == null) { // 如果是空的数据
            log.warn("[保存设备版本信息入缓存] 版本信息为空或者缺少版本的id");
            return;
        }
        String idStr = devModelVersion.getId().toString(); // 版本id转换为字符串
        String codeKey = CacheConstant.DEV_VERSION_CODE_PREX + devModelVersion.getModelVersionCode(); // 版本的key
        String idKey = CacheConstant.DEV_VERSION_ID_PREX + idStr; // id的key
        String value = JsonUtils.toJson(devModelVersion);
        ValueOperations<String, String> options = template.opsForValue();
        removeDevVersion(devModelVersion); // 移除缓存
        // <id, 版本信息>
        options.set(idKey, value, cacheExpireTime, timeUnit);
        // <modelVersionCode, id>真正保存版本信息的是id保存的，这里为了不一个版本的字符串保存做多个保存，这里就让他指向当前版本的id在由版本的id保存对应的值
        // 取值流程 modelVersionCode -> 获取id -> 获得版本信息
        // 如果是版本编号，第一次取得的是版本的id，然后在通过版本id获取对应真正的版本信息的json字符串内容
        options.set(codeKey, idStr, cacheExpireTime, timeUnit);
    }

    /**
     * 移除版本的缓存
     * 需要移除版本号、版本id、版本的锁、版本id的锁
     * @param version
     */
    public void removeDevVersion(DevModelVersion version) {
        if (version == null || version.getId() == null) {
            log.warn("[移除版本信息的错误]");
            return;
        }
        String key1 = CacheConstant.DEV_VERSION_ID_PREX + version.getId();
        String key2 = CacheConstant.DEV_VERSION_CODE_PREX + version.getModelVersionCode();
        // 移除缓存的key
        template.delete(Arrays.asList(key1, key2,
                key1 + CacheConstant.LOCKED_SUFFIX, key2 + CacheConstant.LOCKED_SUFFIX));
    }

    /**
     * 获取缓存的数据
     * 如果没有缓存数据，就从数据库中查询
     * @param sKey 缓存数据 如果 isId == true，则sKey是id的字符串
     * @param isIdSearch 是否是通过id查询。true：通过id查询  false：通过版本号查询
     * @return
     */
    private DevModelVersion getDevModelVersion(String sKey, boolean isIdSearch) {
        ValueOperations<String, String> options = template.opsForValue();
        String key = isIdSearch ? CacheConstant.DEV_VERSION_ID_PREX + sKey :CacheConstant.DEV_VERSION_CODE_PREX + sKey;
        String modelStr = options.get(key);
        if (StringUtils.isBlank(modelStr)) { // 查询数据库
            synchronized (this) {
                modelStr = options.get(key); // 再次获取
                if (StringUtils.isBlank(modelStr)) {
                    // 判断当前是否是在锁定查询的时间范围内,防止缓存穿透,只查询一次数据库
                    String lockeKey = key + CacheConstant.LOCKED_SUFFIX; // 锁定的key
                    String locked = options.get(lockeKey);
                    if (StringUtils.isBlank(locked)) { // 没有锁定，就需要查询数据库
                        options.set(lockeKey, "1", lockTime, timeUnit);
                        // 查询数据库
                        DevModelVersion devModelVersion = null;
                        if (isIdSearch) { // 是否是id查询
                            devModelVersion = devModelVersionMapper.selectByPrimaryKey(Long.valueOf(sKey));
                        } else {
                            DevModelVersion search = new DevModelVersion();
                            search.setModelVersionCode(sKey);
                            try {
                                devModelVersion = devModelVersionMapper.selectOne(search);
                            } catch (Exception e) { // 如果查询出现异常，就直接返回空的数据
                                log.error("search has exception:", e);
                            }
                        }
                        if (devModelVersion == null) {
                            return null;
                        }
                        // 从新保存到redis中
                        setDevModelVersion(devModelVersion);
                        return devModelVersion;
                    } else { // 如果已经锁定了，就说明已经查了，但是没有查询到值，就直接返回空
                        return null;
                    }
                }
            }
        } else if (!isIdSearch){ // 如果存在值,并且是版本的查询，因为版本保存的是版本id
            return getDevModelVersion(modelStr, true); // 最终是通过id获取这个对应的值 modelStr = 设备版本的id的字符串
        }
        return JsonUtils.jsonToPojo(modelStr, DevModelVersion.class);
    }
    // ====<====== 设备版本缓存的相关代码 结束 ==============

    // ====>>>==== 设备信息的缓存相关代码 开始 =======>>>=====
    // 根据设备名称获取设备信息

    /**
     * 查询某一个电站下的根据设备名称获取设备信息
     * @param stationCode 电站编号
     * @param devName 设备名称
     * @return
     */
    public DevInfo getDevInfoByName(String stationCode, String devName){
        return getDevInfo(getDevNameKey(stationCode, devName), false, true);
    }
    // 获取根据设备名称的拼接字符串
    private String getDevNameKey(String stationCode, String devName) {
        return new StringBuffer().append(stationCode).append(multKeySplit).append(devName).toString();
    }

    /**
     * 批量保存设备信息
     * @param list
     */
    public void batchDevs(List<DevInfo> list) {
        for (DevInfo d : list) {
            setDevInifo(d);
        }
    }
    /**
     * 根据设备id查询数据
     * @param devId 设备id
     * @return
     */
    public DevInfo getDevInfoById(Long devId) {
        return getDevInfoById(devId, true);
    }

    /**
     * 根据id获取设备
     * 可以选择是否查询数据库
     * @param devId 设备id
     * @param isSearchDb 如果缓存没有，是否查询数据库 true: 查询数据库， false：不查询数据库
     * @return
     */
    public DevInfo getDevInfoById(Long devId, boolean isSearchDb) {
        if (devId == null) {
            return null;
        }
        return getDevInfo(devId.toString(), true, isSearchDb);
    }

    /**
     * 获取设备信息
     * @param sKey 获取设备信息的key
     * @param isIdSearch 是否是设备的id查询缓存
     * @param isSearchDb 如果没有数据，是否查询数据库 true:查询数据库 false: 不查询数据库
     * @return
     */
    private DevInfo getDevInfo(String sKey, boolean isIdSearch, boolean isSearchDb) {
        ValueOperations<String, String> option = template.opsForValue();
        String key = isIdSearch ? CacheConstant.DEV_ID_PREX + sKey : CacheConstant.DEV_NAME_PREX + sKey;
        String devStr = option.get(key);
        if (StringUtils.isNotBlank(devStr)) {
            if (!isIdSearch) { // 如果不是id查询就是根据名称查询，就查询对应id保存的数据
                return getDevInfo(devStr, true, isSearchDb);
            } else {
                return JsonUtils.jsonToPojo(devStr, DevInfo.class);
            }
        }
        if (!isSearchDb) { // 如果不查询数据库,就直接返回空，因为缓存里面没有数据
            return null;
        }
        // TODO 查询数据库
        synchronized (this) { // 避免给数据库造成压力，这里使用同步，值允许一个进入
            devStr = option.get(key);
            if (StringUtils.isNotBlank(devStr)) { // 如果其他的线程去处理的时候查询了，就会有值
                return JsonUtils.jsonToPojo(devStr, DevInfo.class);
            }
            String lockKey = key + CacheConstant.LOCKED_SUFFIX;
            String locked = option.get(lockKey);
            if (StringUtils.isNotBlank(locked)) { // 如果当前是锁定的
                return null;
            }
            option.set(lockKey, "1", lockTime, timeUnit); // 设置当前数据锁定，5分钟不查询数据库
            DevInfo devInfo = null;
            if (isIdSearch) {
                devInfo = devInfoMapper.selectByPrimaryKey(Long.valueOf(sKey));
            } else { // 如果是设备名称
                String[] stationCodeAndDevName = sKey.split(multKeySplit);
                if (stationCodeAndDevName.length != 2) { // 如果长度不是2就不查询了
                    return null;
                }
                DevInfo search = new DevInfo();
                search.setStationCode(stationCodeAndDevName[0]);
                search.setDevName(stationCodeAndDevName[1]);
                search.setIsLogicDelete(false);
                search.setIsMonitorDev(false);
                try {
                    devInfo = devInfoMapper.selectOne(search);
                }catch (Exception e) {
                    log.error("[查询设备信息] 出现异常：", e);
                    return null;
                }
            }
            setDevInifo(devInfo);
            return devInfo;
        }
    }

    public void setDevInifo(DevInfo devInfo) {
        if (devInfo == null || devInfo.getId() == null) {
            log.warn("no dev for set");
            return;
        }
        String key1 = CacheConstant.DEV_ID_PREX + devInfo.getId();
        String key2 = CacheConstant.DEV_NAME_PREX + getDevNameKey(devInfo.getStationCode(), devInfo.getDevName());
        String devStr = JsonUtils.toJson(devInfo);
        deleteDevInfo(devInfo); // 删除一些锁定的信息
        ValueOperations<String, String> option = template.opsForValue();
        option.set(key1, devStr, cacheExpireTime, timeUnit);
        option.set(key2, key1, cacheExpireTime, timeUnit);
    }

    // 删除设备的缓存
    public void deleteDevInfo(DevInfo devInfo) {
        if (devInfo == null || devInfo.getId() == null) {
            log.warn("缺少需要删除设备信息的内容");
            return;
        }
        String key1 = CacheConstant.DEV_ID_PREX + devInfo.getId();
        String key2 = CacheConstant.DEV_NAME_PREX + getDevNameKey(devInfo.getStationCode(), devInfo.getDevName());
        template.delete(Arrays.asList(key1, key2,
                key1 + CacheConstant.LOCKED_SUFFIX, key2 + CacheConstant.LOCKED_SUFFIX));
    }
    // ====<<<==== 设备信息的缓存相关代码 结束 =======<<<=====
}
