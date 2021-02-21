package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/1/29 6:58
 */

public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
