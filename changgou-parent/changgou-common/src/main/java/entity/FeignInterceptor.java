package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;


public class FeignInterceptor implements RequestInterceptor {
    /**
     * feign调用之前执行拦截
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         *获取用户的令牌
         * 将令牌在封装到头文件中
         */
        //记录了当前用户请求的所有数据，包含请求头和请求参数等；
        ServletRequestAttributes  requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求头中的数据
        //获取所有请求头中的名字
        Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headKey = headerNames.nextElement();
            //获取请求头的值
            String headerValue = requestAttributes.getRequest().getHeader(headKey);
            //将请求头信息封装到Feign调用的请求头中
            requestTemplate.header(headKey,headerValue);
        }
    }

}
