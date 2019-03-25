package xifu.com.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xifu.com.constant.RedisConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * feign请求添加请求参数的配置
 * 主要是给请求参数添加请求头的tokenId的参数，否则权限验证不通过
 * @author wuqiang
 */
@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if (StringUtils.isBlank(tokenId)) { // 首先确定请求头里面是否有tokenId的信息，如果没有再从请求参数里面获取
            tokenId = request.getParameter(RedisConstants.REQUEST_TOKEN_ID);
        }
        if (StringUtils.isNotBlank(tokenId)) { // 给feign的请求头添加tokenId的参数信息
            template.header(RedisConstants.REQUEST_TOKEN_ID, tokenId);
        }
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String name = headerNames.nextElement();
//                String values = request.getHeader(name);
//                template.header(name, values);
//
//            }
//            log.info("feign interceptor header:{}",template);
//        }
       /* Enumeration<String> bodyNames = request.getParameterNames();
        StringBuffer body =new StringBuffer();
        if (bodyNames != null) {
            while (bodyNames.hasMoreElements()) {
                String name = bodyNames.nextElement();
                String values = request.getParameter(name);
                body.append(name).append("=").append(values).append("&");
            }
        }
        if(body.length()!=0) {
            body.deleteCharAt(body.length()-1);
            template.body(body.toString());
            //logger.info("feign interceptor body:{}",body.toString());
        }*/
    }
}