package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 性能数据入库的微服务
 * @auth wq on 2019/1/7 17:06
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling // 可以执行任务
public class DbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class);
    }
}
