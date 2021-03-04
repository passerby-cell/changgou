package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    /**
     * feign调用之前执行拦截
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("自定义拦截器");
        /***
         * 从数据库加载查询用户信息
         * 1、没有令牌，Feign调用之前，生成令牌（admin）
         * 2、Feign调用之前，令牌需要携带过去
         * 3、Feign调用之前，令牌需要存储到Header文件中去
         * 4、请求->Feign调用->拦截器RequestInterceptor->Feign调用之前拦截
         */
        //生成admin令牌
       String token = AdminToken.adminToken();
       System.out.println(token);
       requestTemplate.header("Authorization", "bearer "+token);
    }

}
