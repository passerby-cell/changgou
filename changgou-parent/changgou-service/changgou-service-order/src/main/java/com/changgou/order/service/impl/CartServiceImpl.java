package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    //数据存储到redis
    @Autowired
    private RedisTemplate redisTemplate;
    //feign调用
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;

    /**
     * 加入购物车
     *
     * @param number
     * @param id
     */
    @Override
    public void add(Integer number, Long id, String username) {
        if (number<=0){
            redisTemplate.boundHashOps("cart_"+username).delete(id);
            //如果此时购物车数量为空，则连购物车一起移除
            Long size = redisTemplate.boundHashOps("cart_" + username).size();
            if (size==null||size<=0){
                redisTemplate.delete("cart_"+username);
            }
            return;
        }
        //查询商品的详情
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();
        //将数据封装并存到redis中
        createOrderIterm(number, id, username, sku, spu);

    }

    /**
     * 购物车集合查询
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        //获取指定命名空间下所有数据
        return redisTemplate.boundHashOps("cart_"+username).values();
    }

    /**
     * 创建一个orderiteam对象
     * @param number
     * @param id
     * @param username
     * @param sku
     * @param spu
     */
    private void createOrderIterm(Integer number, Long id, String username, Sku sku, Spu spu) {
        //将加入购物车的商品信息封装成OrderIterm
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(number);
        orderItem.setMoney(number * orderItem.getPrice());
        orderItem.setImage(spu.getImage());
        //将购物车数据存入到redis
        redisTemplate.boundHashOps("cart_" + username).put(id, orderItem);
    }
}
