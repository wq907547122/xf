package xifu.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 微服务的网关信息
 * @auth wq on 2019/1/3 11:47
 **/
@EnableZuulProxy
@SpringBootApplication
@EnableDiscoveryClient
public class ZuulAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulAuthApplication.class);
    }
}
