package xifu.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(JwtProperties.class)
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtProperties prop;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对于web添加拦截器， 新增多有线程共享的数据，这样在任何一个环节都可以获取到当前请求的用户信息
        registry.addInterceptor(new UserAuthInterceptor.UserAuthBuilder()
                 // 创建 UserAuthInterceptor 需要 redisTemplate，所以使用构造器传spring上下文的redisTemplate
                .redisTemplate(redisTemplate).prop(prop).build())
                .addPathPatterns("/**");
    }
}
