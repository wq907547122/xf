package xifu.com.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * gateway的过滤器,鉴权的操作
 * @auth wq on 2019/2/13 10:03
 **/
@Slf4j
@Component
public class MyAuthGloadFilter implements GlobalFilter, Ordered {
    // 登录接口，以及定时任务等不需要验证是否登录的接口的白名单信息,上传下载文件不需要权限验证
    private static final List<String> WHITE_URL_LIST = Arrays.asList(
            "/auth/login", "/fileManager/upload", "/fileManager/downloadFile"
    );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isNoFilter (request)) { // 不需要做验证校验权限的url请求
            return chain.filter(exchange);
        }
        String tokenId = request.getHeaders().getFirst("tokenId");
        log.info("tokenId = {}", tokenId);
//        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isBlank(tokenId)) { // 如果没有tokenId，返回没有权限的页面
            tokenId = request.getQueryParams().getFirst("tokenId");
            if (StringUtils.isBlank(tokenId)) { // 如果请求参数也没有，就直接不允许
                log.info( "token is empty..." );
//            byte[] bytes = "{\"status\":\"401\",\"msg\":\"无操作权限\"}".getBytes(StandardCharsets.UTF_8);
                byte[] bytes = "{\"status\":\"401\",\"msg\":\"NO_AUTH\"}".getBytes(); // 返回json格式的无权限的结果
                DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(bytes);
                return exchange.getResponse().writeWith(Flux.just(wrap));
                // TODO 鉴权不成功的就直接返回没有权限的页面
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
            }

        }
        // TODO 判断用户是否可以请求这个资源
        return chain.filter(exchange);
    }

    /**
     * 判断是否是不需要过滤的路径
     * @param request
     * @return
     */
    private boolean isNoFilter(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        if (WHITE_URL_LIST.contains(path)) {
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() { // 过滤器执行的优先级，越大优先级越低
        return -100;
    }
}
