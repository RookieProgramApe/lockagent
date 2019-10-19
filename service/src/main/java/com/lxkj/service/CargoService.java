package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lxkj.entity.*;
import com.lxkj.mapper.CargoMapper;
import com.lxkj.mapper.WarehousingMapper;
import com.lxkj.service.CargoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class CargoService extends ServiceImpl<CargoMapper, Cargo> {
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CargoSkuService cargoSkuService;
    @Autowired
    private WarehousingService warehousingService;
    @Autowired
    private CargoCategoryService cargoCategoryService;
    @Resource
    private WarehousingMapper warehousingMapper;
    /**
     * 格式化
     */
    public void getData(Cargo p) {
        //封面
        var picture = this.cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id", p.getId()).eq("type", 1));
        p.setPicture(picture == null ? "" : picture.getUrl());
        //视频
        var mov = this.cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id", p.getId()).eq("type", 2));
        p.setMov(mov == null ? "" : mov.getUrl());
        //展示销量
        int i = getSaleNum(p.getId());
        p.setSaleNum(p.getBaseSaleCount() + i);
        //获取规格
        List<CargoSku> sku = this.cargoSkuService.list(Wrappers.<CargoSku>query().eq("cargo_id", p.getId()).eq("status",1));
        List<CargoCategory> categoryList = this.cargoCategoryService.list(Wrappers.<CargoCategory>query().eq("cargo_id", p.getId()));
        p.setSku(sku);
        p.setCategoryList(categoryList);
    }


    /**
     * 获取规格
     * @param bean
     */
    private void getSku(Cargo bean) {
        List<CargoSku> sku = this.cargoSkuService.list(Wrappers.<CargoSku>query().eq("cargo_id", bean.getId()).eq("status",1));
        bean.setSku(sku);
    }

    /**
     * 获得真实销量
     */
    public Integer getSaleNum(String id) {
        int i = orderService.count(new QueryWrapper<Order>().eq("cargo_id", id).in("status", 2,3,4));
        return i;
    }


    /**
     * 更新库存
     */
    public synchronized void updateInventory(String skuId) {
        var i=warehousingMapper.sumSku(skuId);
        new CargoSku().setId(skuId).setInventory(i).updateById();

    }
}