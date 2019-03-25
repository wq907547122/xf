package xifu.com.client;

import org.springframework.cloud.openfeign.FeignClient;
import xifu.com.api.StationApi;
import xifu.com.config.FeignConfiguration;

/**
 * 引入调用的用户对应的电站模块的api接口
 * @auth wq on 2019/3/19 15:29
 **/
@FeignClient(value = "xifu-user", configuration = FeignConfiguration.class)
public interface StationClient extends StationApi {
}
