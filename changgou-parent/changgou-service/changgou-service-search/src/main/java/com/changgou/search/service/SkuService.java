package com.changgou.search.service;

import com.changgou.goods.pojo.Sku;

import java.util.List;
import java.util.Map;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/1/31 6:10
 */
public interface SkuService {
    /***
     * 搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String, String> searchMap);
    /**
     * 导入数据到索引库中
     */
    void importData();

}
