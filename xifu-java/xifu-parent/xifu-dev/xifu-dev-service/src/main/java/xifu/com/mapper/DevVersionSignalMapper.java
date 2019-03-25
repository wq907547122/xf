package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import xifu.com.pojo.devService.DevVersionSignal;

/**
 * 设备版本对应的信号点模型的mapper
 * @auth wq on 2019/3/19 10:46
 **/
public interface DevVersionSignalMapper extends Mapper<DevVersionSignal>, MySqlMapper<DevVersionSignal> {
}
