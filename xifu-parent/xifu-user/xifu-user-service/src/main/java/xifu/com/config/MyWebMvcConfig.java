package xifu.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xifu.com.intercepters.UserAuthInterceptor;

/**
 * 添加拦截器
 * @auth wq on 2019/1/29 10:40
 **/
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer{
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserAuthInterceptor.UserAuthBuilder()
                .redisTemplate(redisTemplate).build())
                .addPathPatterns("/**");
    }
}
