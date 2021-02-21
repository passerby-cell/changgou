package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/1/31 6:25
 */
@RestController
@RequestMapping(value = "/search")
@CrossOrigin
public class SkuEsController {

    @Autowired
    private SkuService skuService;

    /**
     * @param searchMap 搜索的条件 map
     * @return resultMap  返回的结果 map
     */
    @RequestMapping
    public Map search(@RequestParam(required = false) Map searchMap) {
        Object pageNum = searchMap.get("pageNum");
        if (pageNum == null) {
            searchMap.put("pageNum", "1");
        }
        if (pageNum instanceof Integer) {
            searchMap.put("pageNum", pageNum.toString());
        }

        return skuService.search(searchMap);
    }

//    /**
//     * 搜索
//     *
//     * @param searchMap
//     * @return
//     */
//    @GetMapping
//    public Map search(@RequestParam(required = false) Map searchMap) {
//        return skuService.search(searchMap);
//    }
//    @PostMapping
//    public Map searchbrand(@RequestParam(required = false) Map searchMap) {
//        return skuService.search(searchMap);
//    }

    /**
     * 导入数据
     *
     * @return
     */
    @GetMapping("/import")
    public Result search() {
        skuService.importData();
        return new Result(true, StatusCode.OK, "导入数据到索引库中成功！");
    }
}