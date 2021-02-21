package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/2/15 19:51
 */
@FeignClient("search")
@RequestMapping(value = "/search")
public interface SkuFeign {
    /**
     * 调用搜索微服务
     * @param searchMap
     * @return
     */
    @RequestMapping
    Map search(@RequestParam(required = false) Map searchMap);
}
