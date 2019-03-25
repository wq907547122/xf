package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import xifu.com.pojo.devService.DevInfo;

/**
 * 设备实体表对应的mapper
 * @auth wq on 2019/3/19 10:44
 **/
public interface DevInfoMapper extends Mapper<DevInfo>, MySqlMapper<DevInfo> {
}
