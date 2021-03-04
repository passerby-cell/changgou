package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车操作
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private TokenDecode tokenDecode;
    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     *
     * @param num 加入购物车的数量
     * @param id  商品id
     */
    @GetMapping("/add")
    public Result add(Integer num, Long id) {
        String username = tokenDecode.getUserInfo().get("username");
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK, "添加购物车成功！");
    }

    /**
     * 购物车列表
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> list() {
        String username = tokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result<List<OrderItem>>(true, StatusCode.OK, "查询购物车成功！", orderItems);
    }
}
