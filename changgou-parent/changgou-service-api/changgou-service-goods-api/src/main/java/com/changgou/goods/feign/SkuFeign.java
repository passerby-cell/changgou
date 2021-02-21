package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import com.github.pagehelper.PageInfo;
import entity.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/1/31 6:03
 */
@FeignClient(value = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    @GetMapping(value = "/search/{page}/{size}")
    Result<PageInfo<Sku>> findPage(@PathVariable int page, @PathVariable int size);

    @GetMapping
    Result<List<Sku>> findAll();

    @PostMapping(value = "/search")
    Result<List<Sku>> findList(@RequestBody(required = false) @ApiParam(name = "Sku对象", value = "传入JSON数据", required = false) Sku sku);

    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);
}
