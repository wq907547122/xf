package xifu.com.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import xifu.com.mapper.DevInfoMapper;
import xifu.com.mapper.DevModelVersionMapper;
import xifu.com.mapper.DevSignalMapper;
import xifu.com.mapper.DevVersionSignalMapper;
import xifu.com.pojo.devService.DevInfo;
import xifu.com.pojo.devService.DevModelVersion;
import xifu.com.pojo.devService.DevSignal;
import xifu.com.pojo.devService.DevVersionSignal;
import xifu.com.utils.JsonUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private DevVersionSignalMapper devVersionSignalMapper;
    @Autowired
    private DevInfoMapper devInfoMapper;
    @Autowired
    private DevSignalMapper devSignalMapper;
    // 是否重新初始化redis的缓存
    @Value("${xifu.isRedisCacheInit}")
    private Boolean isRedisCacheInit;
    private Long lockTime = 5 * 60 * 1000L; // 锁定时间5分钟,即5分钟内不查询数据库
    private Long cacheExpireTime = 24 * 60 * 60 * 1000L; // 过期时间为1天
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS; // 过期时间的单位 这里使用ms
    private String multKeySplit = "::"; // 多个字符串确定一个key的分割

    // =========== 设备版本缓存的相关代码 开始 ========>=======

    /**
     * 启动程序的时候初始化缓存数据 1：清空缓存 2：设置数据库中的缓存
     * 这个可以提供一个api的接口，共外面的调用
     */
    @PostConstruct
    public void init() {
        if (!isRedisCacheInit) { // 如果不需要初始化
            return;
        }
        // 1.清除缓存
        Set<String> keys = template.keys(CacheConstant.CACHE_DEV_PREX_PATTERN);
        if (!CollectionUtils.isEmpty(keys)) { // 批量删除key
            template.delete(keys);
        }
        // 2.查询数据
        List<DevModelVersion> devModelVersions = devModelVersionMapper.selectAll();
        this.batchInsertDevModelVersion(devModelVersions);
        Example example = new Example(DevInfo.class);
        example.createCriteria().andEqualTo("isLogicDelete", false);
        List<DevInfo> devInfos = devInfoMapper.selectByExample(example);
        this.batchDevs(devInfos);
        // 查询版本信号点信息
        List<String> modelVersionCodeList = new ArrayList<>();
        int count = 0;
        for(DevModelVersion v : devModelVersions) {
            count ++;
            modelVersionCodeList.add(v.getModelVersionCode());
            if (count % 5 == 0) { // 每5个版本添加一次版本信号点信息
                this.batchAddVersionSignals(findDevVersionSingalList(modelVersionCodeList));
                modelVersionCodeList.clear();
            }
        }
        if (!CollectionUtils.isEmpty(modelVersionCodeList)) {
            this.batchAddVersionSignals(findDevVersionSingalList(modelVersionCodeList));
        }
        // 缓存设备的信号点
        count = 0;
        List<Long> devIdList = new ArrayList<>();
        for(DevInfo d : devInfos) {
            count ++;
            devIdList.add(d.getId());
            if (count % 20 == 0) { // 20个设备添加一次
                batchAddDevSignal(getSignalPageInfoBatch(devIdList));
                devIdList.clear();
            }
        }
        if (!CollectionUtils.isEmpty(devIdList)) {
            batchAddDevSignal(getSignalPageInfoBatch(devIdList));
        }
        log.info("init redis cache end");
    }

    /**
     * 获取信号点的分页信息，避免一次查询数据过多
     * @param devIdList 设备列表
     * @return
     */
    private List<DevSignal> getSignalPageInfoBatch(List<Long> devIdList){
        Example example = new Example(DevSignal.class);
        example.createCriteria().andIn("devId", devIdList);
        return devSignalMapper.selectByExample(example);
    }

    private List<DevVersionSignal> findDevVersionSingalList(List<String> devVersionCodeList) {
        Example example = new Example(DevVersionSignal.class);
        example.createCriteria().andIn("modelVersionCode", devVersionCodeList);
        return this.devVersionSignalMapper.selectByExample(example);
    }
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 批量处理数据
        // 获取key的系列化方式
        final StringRedisSerializer keySerializer = (StringRedisSerializer)template.getKeySerializer();
        // 获取value的系列化方式
        final RedisSerializer<String> valueStrSerializer = (RedisSerializer<String>)template.getValueSerializer();
        boolean isMill = TimeUnit.MILLISECONDS.equals(timeUnit); // 单位是否是毫秒
        // 再批量新增,使用的管道，参考的单个的新增的
        template.executePipelined(new RedisCallback() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                // id的key和modelVersionCode的key
                byte[] key1 = null, key2 = null;
                for (DevModelVersion v  : list) {
                    Long id = v.getId();
                    if (id == null) {
                        continue;
                    }
                    String idStr = id.toString();
                    key1 = keySerializer.serialize(getModelVersionIdKey(idStr)); // 版本id的key
                    key2 = keySerializer.serialize(getModelVersionCodeKey(v.getModelVersionCode())); // 版本的
                    // 获取序列化的内容
                    byte[] objSeriaData = valueStrSerializer.serialize(JsonUtils.toJson(v));
                    byte[] idSeriaData = valueStrSerializer.serialize(idStr);
                    if (!isMill || !failsafeInvokePsetEx(connection, key1, objSeriaData)) {
                        // 单位是s的设置
                        connection.setEx(key1, cacheExpireTime, objSeriaData);
                    }
                    // 存放modelversionCode - > id的映射
                    if (!isMill || !failsafeInvokePsetEx(connection, key2, idSeriaData)) { // 只有如果单位不是毫秒的就直接进入，如果是毫秒的设置失败也直接重新设置
                        connection.setEx(key2, cacheExpireTime, idSeriaData);
                    }
                }
                connection.closePipeline();
                return null;
            }
            // 使用的源码的方式设置数据到redis,这里是设置毫秒的设置
            private boolean failsafeInvokePsetEx(RedisConnection connection, byte[] rawKey, byte[] rawValue) {
                boolean failed = false;
                try {
                    connection.pSetEx(rawKey, cacheExpireTime, rawValue); // 设置的毫秒
                } catch (UnsupportedOperationException e) {
                    // in case the connection does not support pSetEx return false to allow fallback to other operation.
                    failed = true;
                }
                return !failed;
            }
        });

//        for (DevModelVersion v : list) {
//           setDevModelVersion(v);
//        }
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
        String codeKey = getModelVersionCodeKey(devModelVersion.getModelVersionCode()); // 版本的key
        String idKey = getModelVersionIdKey(idStr); // id的key
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

    private String getModelVersionCodeKey(String modelVersionCode) {
        return CacheConstant.DEV_VERSION_CODE_PREX + modelVersionCode;
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
        String key1 = getModelVersionIdKey(version.getId().toString());
        String key2 = getModelVersionCodeKey(version.getModelVersionCode());
        // 移除缓存的key
        template.delete(Arrays.asList(key1, key2,
                key1 + CacheConstant.LOCKED_SUFFIX, key2 + CacheConstant.LOCKED_SUFFIX));
    }

    private String getModelVersionIdKey(String id) {
        return CacheConstant.DEV_VERSION_ID_PREX + id;
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

    /**
     * 批量删除缓存
     * @param list 集合中每个元素必须包含id和modelVersionCode
     */
    public void batchDelDevVersionCaches(List<DevModelVersion> list) {
        if (CollectionUtils.isEmpty(list)) {
            log.warn("无版本信息需要移除");
            return;
        }
        for(DevModelVersion dev : list) {
            removeDevVersion(dev);
        }
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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 批量处理数据
        // 获取key的系列化方式
        final StringRedisSerializer keySerializer = (StringRedisSerializer)template.getKeySerializer();
        // 获取value的系列化方式
        final RedisSerializer<String> valueStrSerializer = (RedisSerializer<String>)template.getValueSerializer();
        boolean isMill = TimeUnit.MILLISECONDS.equals(timeUnit); // 单位是否是毫秒
        template.executePipelined(new RedisCallback() {

            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key1 = null, key2 = null;
                for(DevInfo d : list) {
                    Long id = d.getId();
                    if (id == null) {
                        continue;
                    }
                    String idStr = id.toString();
                    key1 = keySerializer.serialize(getDevIdKey(idStr));
                    key2 = keySerializer.serialize(getDevNameCacheKey(d.getStationCode(), d.getDevName()));
                    // 获取序列化的内容
                    byte[] objSeriaData = valueStrSerializer.serialize(JsonUtils.toJson(d));
                    byte[] idSeriaData = valueStrSerializer.serialize(idStr);
                    if (!isMill || !failsafeInvokePsetEx(connection, key1, objSeriaData)) {
                        connection.setEx(key1, cacheExpireTime, objSeriaData);
                    }
                    if (!isMill || !failsafeInvokePsetEx(connection, key2, idSeriaData)) {
                        connection.setEx(key2, cacheExpireTime, idSeriaData);
                    }
                }
                connection.closePipeline();
                return null;
            }
            // 使用的源码的方式设置数据到redis,这里是设置毫秒的设置
            private boolean failsafeInvokePsetEx(RedisConnection connection, byte[] rawKey, byte[] rawValue) {
                boolean failed = false;
                try {
                    connection.pSetEx(rawKey, cacheExpireTime, rawValue); // 设置的毫秒
                } catch (UnsupportedOperationException e) {
                    // in case the connection does not support pSetEx return false to allow fallback to other operation.
                    failed = true;
                }
                return !failed;
            }
        });
//        for (DevInfo d : list) {
//            setDevInifo(d);
//        }
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
        // 查询数据库
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
        String key1 = getDevIdKey(devInfo.getId().toString());
        String key2 = getDevNameCacheKey(devInfo.getStationCode(), devInfo.getDevName());
        String devStr = JsonUtils.toJson(devInfo);
        deleteDevInfo(devInfo); // 删除一些锁定的信息
        ValueOperations<String, String> option = template.opsForValue();
        option.set(key1, devStr, cacheExpireTime, timeUnit);
        option.set(key2, key1, cacheExpireTime, timeUnit);
    }
    // 获取设备id的缓存redis的key的字符串
    private String getDevIdKey(String devId) {
        return CacheConstant.DEV_ID_PREX + devId;
    }

    // 删除设备的缓存
    public void deleteDevInfo(DevInfo devInfo) {
        if (devInfo == null || devInfo.getId() == null) {
            log.warn("缺少需要删除设备信息的内容");
            return;
        }
        String key1 = getDevIdKey(devInfo.getId().toString());
        String key2 = getDevNameCacheKey(devInfo.getStationCode(), devInfo.getDevName());
        template.delete(Arrays.asList(key1, key2,
                key1 + CacheConstant.LOCKED_SUFFIX, key2 + CacheConstant.LOCKED_SUFFIX));
    }

    private String getDevNameCacheKey(String stationCode, String devName) {
        return CacheConstant.DEV_NAME_PREX + getDevNameKey(stationCode, devName);
    }


    /**
     * 批量删除设备缓存的信息
     * @param devInfos
     */
    public void batchDelDevInfos(List<DevInfo> devInfos) {
        if (CollectionUtils.isEmpty(devInfos)) {
            log.warn("无设备信息需要移除");
            return;
        }
        for (DevInfo d : devInfos) {
            deleteDevInfo(d);
        }
    }
    // ====<<<==== 设备信息的缓存相关代码 结束 =======<<<=====

    // ===>>>>==== 版本的信号点内容的缓存 开始 =======>>>=====
    // 获取版本信号点的key
    private String getVersionSignalKeyByVersionCode(String versionCode){
        return CacheConstant.MODEL_SIGNAL_ID_VERSION_PREX + versionCode;
    }

    /**
     * 新增一个版本的信号点的缓存
     * 信号点的缓存是通过版本找到版本的所有信号点<modelVersionCode, <signalId, signal>>
     * @param signal
     */
    public void addVersionSignal(DevVersionSignal signal) {
        if (signal == null || signal.getId() == null) {
            return;
        }
        String key = getVersionSignalKeyByVersionCode(signal.getModelVersionCode());
        BoundHashOperations<String, Object, Object> opts = template.boundHashOps(key);
        opts.put(signal.getId().toString(), JsonUtils.toJson(signal));
    }

    /**
     * 批量新增版本模型信号点
     * @param list
     */
    public void batchAddVersionSignals(List<DevVersionSignal> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 循环新增，这里就不使用管道了
        for(DevVersionSignal signal : list) {
            this.addVersionSignal(signal);
        }
    }

    /**
     * 获取一个版本对应的版本信号点集合
     * 批量获取版本的所有信号点模型, 没有使用过期的时间
     * @param modelVersionCode
     * @return
     */
    public List<DevVersionSignal> getDevVersionSignalsByModelVersionCode(String modelVersionCode) {
        if (StringUtils.isBlank(modelVersionCode)) {
            return null;
        }
        String key = getVersionSignalKeyByVersionCode(modelVersionCode);
        BoundHashOperations<String, String, String> ops = template.boundHashOps(key);
        List<String> values = ops.values();
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        return values.stream().map(s -> JsonUtils.jsonToPojo(s, DevVersionSignal.class)).collect(Collectors.toList());
    }

    /**
     * 从redis缓存中，根据版本信息(modelVersionCode)和版本信号点id获取一个版本信号点的信息
     * @param modelVersionCode 版本编号
     * @param signalId 信号点id
     * @return
     */
    public DevVersionSignal getDevVersionSiganlById(String modelVersionCode, Long signalId) {
        if (StringUtils.isBlank(modelVersionCode) || signalId == null) {
            return null;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getVersionSignalKeyByVersionCode(modelVersionCode));
        String s = opts.get(signalId.toString());
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return JsonUtils.jsonToPojo(s, DevVersionSignal.class);
    }

    /**
     * 根据版本编号和信号点名称获取 版本信号点
     * @param modelVersionCode
     * @param signalName
     * @return
     */
    public DevVersionSignal getDevVersionSignalBySignalName(String modelVersionCode, String signalName) {
        if (StringUtils.isBlank(modelVersionCode) || StringUtils.isBlank(signalName)) {
            return null;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getVersionSignalKeyByVersionCode(modelVersionCode));
        List<String> values = opts.values();
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        Optional<String> first = values.stream().filter(s -> {
            DevVersionSignal v = JsonUtils.jsonToPojo(s, DevVersionSignal.class);
            return v == null ? false : StringUtils.equals(v.getSignalName(), signalName);
        }).findFirst();
        if (!first.isPresent()) { // 如果不存在,即没有找到
            return null;
        }
        return JsonUtils.jsonToPojo(first.get(), DevVersionSignal.class);
    }

    /**
     * 清除版本信号点模型的信号点
     * @param modelVersionCode
     */
    public void clearDevVersionSignalByModelVersionCode(String modelVersionCode){
        if (StringUtils.isBlank(modelVersionCode)) {
            return;
        }
        template.delete(getVersionSignalKeyByVersionCode(modelVersionCode));
//        BoundHashOperations<String, String, String> opts = template.boundHashOps(getVersionSignalKeyByVersionCode(modelVersionCode));
//        Set<String> keys = opts.keys();
//        if (!CollectionUtils.isEmpty(keys)) {
//            opts.delete(keys);
//        }
    }

    /**
     * 清除版本列表的模型信号点的缓存
     * @param modelVersionCodeList
     */
    public void clearDevVersionSignalByModelVersionCodeList(List<String> modelVersionCodeList) {
        if (CollectionUtils.isEmpty(modelVersionCodeList)) {
            return;
        }
        // 批量删除信号点
        modelVersionCodeList.forEach(v -> clearDevVersionSignalByModelVersionCode(v));
    }

    // ===<<<<==== 版本的信号点内容的缓存 结束 =======<<<=====



    // ===>>>>==== 设备信号点的缓存 开始 =======>>>=====
    // 设备信号点的缓存通过设备id -> 设备信号点的集合的缓存

    /**
     * 新增信号点信息
     * @param signal
     */
    public void addDevSignal(DevSignal signal) {
        if (signal == null || signal.getId() == null || signal.getDevId() == null) {
            log.warn("no dev signal need add");
            return;
        }
        String devSignalKey = getDevSignalKey(signal.getDevId().toString());
        BoundHashOperations<String, String, String> opts = template.boundHashOps(devSignalKey);
        opts.put(signal.getId().toString(), JsonUtils.toJson(signal));
    }

    /**
     * 批量新增设备信号点
     * @param signalList
     */
    public void batchAddDevSignal(List<DevSignal> signalList) {
        if (CollectionUtils.isEmpty(signalList)) {
            log.warn("batch add signal is empty");
            return;
        }
        // 组装保存数据<devId, <信号点id, 信号点的字符串>>
        Map<String, Map<String, String>> datas = new HashMap<>();
        for(DevSignal s : signalList) { // 准备存储数据
            if (s == null || s.getId() == null || s.getDevId() == null) { // 信息缺少，不入缓存
                continue;
            }
            String devId = s.getDevId().toString();
            if(!datas.containsKey(devId)) {
                datas.put(devId, new HashMap<>());
            }
            datas.get(devId).put(s.getId().toString(), JsonUtils.toJson(s));
        }
        if (CollectionUtils.isEmpty(datas)) { // 一般是集合中的信号点都是缺少信号点id或者设备id
            log.warn("batch add signal all loss data");
            return;
        }
        // 循环存储缓存
        for(Map.Entry<String, Map<String, String>> entry : datas.entrySet()) {
            BoundHashOperations<String, String, String> opts = template.boundHashOps(getDevSignalKey(entry.getKey()));
            opts.putAll(entry.getValue()); // 批量存储
        }
    }

    /**
     * 获取设备信号点
     * @param devId 设备id
     * @param signalId 信号点id
     * @return
     */
    public DevSignal getDevSignal(Long devId, Long signalId) {
        if (devId == null || signalId == null) {
            log.warn("get signal failed, has data is empty");
            return null;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getDevSignalKey(devId.toString()));
        String s = opts.get(signalId.toString());
        if (StringUtils.isBlank(s)) { // 没有数据缓存
            return null;
        }
        return JsonUtils.jsonToPojo(s, DevSignal.class);
    }

    /**
     * 返回一个设备的指定信号点id的集合
     * @param devId
     * @param signalIdList
     * @return
     */
    public List<DevSignal> getDevSignalList(Long devId, List<Long> signalIdList) {
        if (devId == null || CollectionUtils.isEmpty(signalIdList)) {
            log.warn("get signal failed, has data is empty");
            return null;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getDevSignalKey(devId.toString()));
        List<String> sinalIdStrList = signalIdList.stream().filter(sId -> sId != null).map(s -> s.toString()).collect(Collectors.toList());
        List<String> sList = opts.multiGet(sinalIdStrList);
        return castDevSignals(devId, sList); // 将字符串的list转换为信号点的对象集合
    }

    /**
     * 获取设备的所有信号点
     * @param devId 设备id
     * @return
     */
    public List<DevSignal> getDevSignalListByDevId(Long devId) {
        if (devId == null) {
            return null;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getDevSignalKey(devId.toString()));
        List<String> sList = opts.values();
        return castDevSignals(devId, sList); // 将字符串的list转换为信号点的对象集合
    }

    /**
     * 根据设备id获取设备信号点的map信息
     * 信号点的map信息: <信号点名称，信号点的对象>
     * @param devId 设备id
     * @return
     */
    public Map<String, DevSignal> getDevSignalNameMapByDevId(Long devId) {
        List<DevSignal> devSignalListByDevId = getDevSignalListByDevId(devId);
        if (CollectionUtils.isEmpty(devSignalListByDevId)) {
            return null;
        }
        Map<String, DevSignal> result = new HashMap<>();
        devSignalListByDevId.forEach(s -> result.put(s.getSignalName(), s));
        return result;
    }

    /**
     * 根据设备id获取设备信号点的map信息
     * 信号点的map信息: <信号点Id，信号点的对象>
     * @param devId 设备id
     * @return
     */
    public Map<Long, DevSignal> getDevSignalIdMapByDevId(Long devId) {
        List<DevSignal> devSignalListByDevId = getDevSignalListByDevId(devId);
        if (CollectionUtils.isEmpty(devSignalListByDevId)) {
            return null;
        }
        Map<Long, DevSignal> result = new HashMap<>();
        devSignalListByDevId.forEach(s -> result.put(s.getId(), s));
        return result;
    }
    // 获取信号点的字符串集合转换为对象
    private List<DevSignal> castDevSignals(Long devId, List<String> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            log.warn("no data devId = {}", devId);
            return null;
        }
        Class<DevSignal> devSignalClass = DevSignal.class;
        return sList.stream().filter(s -> StringUtils.isNotBlank(s)).
                map(s -> JsonUtils.jsonToPojo(s, devSignalClass)).collect(Collectors.toList());
    }

    /**
     * 获取设备信号点的缓存
     * @param devId
     * @return
     */
    private String getDevSignalKey(String devId) {
        return CacheConstant.DEV_SIGNAL_PREX + devId;
    }

    /**
     * 批量删除设备信号点的缓存的信息
     * @param devId 设备id
     */
    public void deleteDevSignalByDevId(Long devId) {
        if (devId == null) {
            return;
        }
        template.delete(getDevSignalKey(devId.toString()));
    }
    // 根据设备id删除信息
    public void deleteDevSignalByDevIdList(List<Long> devIdList) {
        if (CollectionUtils.isEmpty(devIdList)) {
            return;
        }
        devIdList.forEach(id -> deleteDevSignalByDevId(id));
    }

    /**
     * 删除设备的一个信号点缓存
     * @param devId 设备id
     * @param signalId 设备信号点
     */
    public void deleteDevSignalByDevIdAndSignalId(Long devId, Long signalId) {
        if (devId == null || signalId == null) {
            return;
        }
        BoundHashOperations<String, String, String> opts = template.boundHashOps(getDevSignalKey(devId.toString()));
        opts.delete(signalId.toString());
    }
    // ===<<<<==== 设备信号点的缓存 结束 =======<<<=====
}
