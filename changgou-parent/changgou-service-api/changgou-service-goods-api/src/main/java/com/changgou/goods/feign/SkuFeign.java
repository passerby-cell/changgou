package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.goods.feign *
 * @since 1.0
 */
@FeignClient(value = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /***
     * 商品信息的递减
     * Map<key,value>,key:要递减的商品id，value：要递减的商品数量
     * @return
     */
    @GetMapping("/decr")
    public Result decrCount(@RequestParam Map<String, Integer> decrmap);

    /**
     * 查询符合条件的状态的SKU的列表
     *
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);

    @GetMapping(value = "/search/{page}/{size}")
    Result<PageInfo<Sku>> findPage(@PathVariable int page, @PathVariable int size);

    /**
     * 根据条件搜索的SKU的列表
     *
     * @param sku
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);


    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name = "id") Long id);


}
