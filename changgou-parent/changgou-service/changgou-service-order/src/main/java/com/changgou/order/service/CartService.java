package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

public interface CartService {
    /**
     * 加入购物车
     * @param number
     * @param id
     */
    void add(Integer number,Long id,String username);

    /**
     * 购物车集合查询
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
