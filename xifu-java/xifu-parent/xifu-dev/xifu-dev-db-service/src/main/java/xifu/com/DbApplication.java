package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 基本性能数据和数据入库(elasticsearch非关系型数据库)的微服务启动类
 * @auth wq on 2019/3/12 16:54
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling // 可以执行定时任务
public class DbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);
    }
}
