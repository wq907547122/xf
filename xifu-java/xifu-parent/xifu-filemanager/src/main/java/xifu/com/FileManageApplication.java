package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 文件系统的微服务启动类
 * @auth wq on 2019/3/12 16:09
 **/
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("xifu.com.file.mapper")
public class FileManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileManageApplication.class, args);
    }
}
