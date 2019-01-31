package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 文件系统的启动类
 * @auth wq on 2019/1/18 17:56
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("xifu.com.file.mapper")
public class FileManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileManageApplication.class, args);
    }
}
