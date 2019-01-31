package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 用户的启动接口
 * @auth wq on 2019/1/3 15:00
 **/
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("xifu.com.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);
    }
}
