package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 恢复数据
     *
     * @param spuId
     */
    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否删除的商品
        if (!spu.getIsDelete().equals("1")) {
            throw new RuntimeException("此商品未删除！");
        }
        //未删除
        spu.setIsDelete("0");
        //未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /***
     * 逻辑删除
     * @param spuId
     */
    @Override
    @Transactional
    public void logicDelete(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否下架的商品
        if (!spu.getIsMarketable().equals("0")) {
            throw new RuntimeException("必须先下架再删除！");
        }
        //删除
        spu.setIsDelete("1");
        //未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 批量下架
     *
     * @param id
     */
    @Override
    public int pullMany(Long[] id) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(id));
        criteria.andEqualTo("isMarketable", "1");
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        return spuMapper.updateByExampleSelective(spu, example);
    }

    /**
     * 批量上架
     *
     * @param spuid
     * @return
     */
    @Override
    public int putMany(Long[] spuid) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(spuid));
        criteria.andEqualTo("isDelete", "0");
        criteria.andEqualTo("status", "1");
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        return spuMapper.updateByExampleSelective(spu, example);

    }

    /**
     * 商品上架
     *
     * @param spuid
     */
    @Override
    public void put(Long spuid) {
        Spu spu = spuMapper.selectByPrimaryKey(spuid);
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("不能对已删除的产品进行上架！");
        }
        if (spu.getStatus().equalsIgnoreCase("0")) {
            throw new RuntimeException("不能对未审核的产品进行上架！");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品下架
     *
     * @param spuid
     */
    @Override
    public void pull(Long spuid) {
        Spu spu = spuMapper.selectByPrimaryKey(spuid);
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("不能对已下架的产品进行审核！");
        } else {
            spu.setIsMarketable("0");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    /**
     * 商品审核及上架
     *
     * @param spuid
     */
    @Override
    public void audit(Long spuid) {
        Spu spu = spuMapper.selectByPrimaryKey(spuid);
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("不能对已删除的产品进行审核！");
        } else {
            spu.setIsMarketable("1");//商品上架
            spu.setStatus("1");//审核通过
            spuMapper.updateByPrimaryKeySelective(spu);
        }

    }

    /**
     * 根据id查询goods
     *
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(Long id) {
        //先查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //在查询sku
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 添加商品信息
     *
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        //spu一个
        Spu spu = goods.getSpu();
        //判断spu的id是否为空
        if (spu.getId() == null) {
            //若为空则执行新增操作
            spu.setId(idWorker.nextId());
            spu.setIsMarketable("1");
            spu.setIsDelete("0");
            spuMapper.insertSelective(spu);
        } else {
            //若不为空则修改
            spuMapper.updateByPrimaryKey(spu);
            //删除之前的sku
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        String name = spu.getName();
        //sku是一个lint集合
        Date date = new Date();
        for (Sku sku : goods.getSkuList()) {
            if (StringUtils.isEmpty(sku.getSpec())) {
                sku.setSpec("{}");//给空对象防止为空
            }
            //将spec转成map
            Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
            for (Map.Entry<String, String> m : map.entrySet()) {
                name += " " + m.getValue();
            }
            sku.setId(idWorker.nextId());
            sku.setName(name);
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setSpuId(spu.getId());
            sku.setCategoryId(spu.getCategory3Id());
            sku.setCategoryName(category.getName());
            sku.setBrandName(brand.getName());
            skuMapper.insertSelective(sku);
        }

    }

    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //检查是否被逻辑删除  ,必须先逻辑删除后才能物理删除
        if (!spu.getIsDelete().equals("1")) {
            throw new RuntimeException("此商品不能删除！");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

}
