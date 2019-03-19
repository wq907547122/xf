package xifu.com.client;

import org.springframework.cloud.openfeign.FeignClient;
import xifu.com.api.UserApi;

/**
 * @auth wq on 2019/2/12 13:44
 **/
@FeignClient(name = "xifu-user")
public interface UserClient extends UserApi {
}
