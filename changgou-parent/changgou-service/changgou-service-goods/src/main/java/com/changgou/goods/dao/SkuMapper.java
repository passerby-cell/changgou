package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface SkuMapper extends Mapper<Sku> {
    /**
     * 库存递减
     * @param key
     * @param value
     * @return
     */
    @Update("update tb_sku set num = num-#{num} where id = #{id} and num>=#{num}")
    int decrCount(@Param("id") String key, @Param("num") Integer value);
}
