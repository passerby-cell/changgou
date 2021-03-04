package com.changgou.gateway.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /***
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求的URI
        String path = request.getURI().getPath();
        //如果是登陆或者一些不需要做权限认证的请求，则直接放行
        if (URLFilter.hasAuthorize(path)) {
            return chain.filter(exchange);
        }
        //获取头文件中的令牌信息
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        boolean hasToken = true;
        //如果头文件中没有，则从请求参数中获取
        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            hasToken = false;
        }

        //从cookie中获取令牌数据
        if (StringUtils.isEmpty(token)) {
            HttpCookie first = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (first != null) {
                token = first.getValue();
            }
        }

        //如果为空，则输出错误代码,没有令牌则拦截
        if (StringUtils.isEmpty(token)) {
            //设置方法不允许被访问，405错误代码
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            //响应空数据
            return response.setComplete();
        }
        //令牌判断，是否为空，如果不为空，将令牌放到头文件中放行

        /*//解析令牌数据
        try {

        } catch (Exception e) {
            e.printStackTrace();
            //解析失败，响应401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
*/
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        } else {
            //判断当前令牌是否有bearer前缀，如果没有则添加
            if (!token.startsWith("bearer") && !token.startsWith("Bearer")) {
                token = "bearer " + token;
            }
        }
        if (!hasToken){
            //将令牌放到头文件中
            request.mutate().header(AUTHORIZE_TOKEN, token);
        }
        //有效则放行
        return chain.filter(exchange);
    }


    /***
     * 过滤器执行顺序,越小越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}