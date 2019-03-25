package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import xifu.com.pojo.devService.DevModelVersion;

/**
 * 设备版本信息的mapper
 * @auth wq on 2019/3/19 10:45
 **/
public interface DevModelVersionMapper extends Mapper<DevModelVersion>, MySqlMapper<DevModelVersion> {
}
