package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 设备方面的微服务启动器
 * @auth wq on 2019/3/18 13:51
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("xifu.com.mapper")
public class DevServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevServerApplication.class, args);
    }
}
