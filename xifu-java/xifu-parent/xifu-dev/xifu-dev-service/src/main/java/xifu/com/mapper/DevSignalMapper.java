package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import xifu.com.pojo.devService.DevSignal;

/**
 * 设备对应信号点的mapper
 * @auth wq on 2019/3/19 10:47
 **/
public interface DevSignalMapper extends Mapper<DevSignal>, MySqlMapper<DevSignal> {
}
