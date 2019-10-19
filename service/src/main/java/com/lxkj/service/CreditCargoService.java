package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.*;
import com.lxkj.mapper.CargoMapper;
import com.lxkj.mapper.CreditCargoMapper;
import com.lxkj.mapper.CreditWarehousingMapper;
import com.lxkj.mapper.WarehousingMapper;
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
public class CreditCargoService extends ServiceImpl<CreditCargoMapper, CreditCargo> {
    @Autowired
    private CreditCargoAttachmentService creditCargoAttachmentService;
    @Autowired
    private CreditOrderService creditOrderService;
    @Autowired
    private CreditCargoSkuService creditCargoSkuService;
    @Autowired
    private WarehousingService warehousingService;
    @Resource
    private CreditWarehousingMapper creditWarehousingMapper;
    /**
     * 格式化
     */
    public void getData(CreditCargo p) {
        //封面
        var picture = this.creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id", p.getId()).eq("type", 1));
        p.setPicture(picture == null ? "" : picture.getUrl());
        //视频
        var mov = this.creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id", p.getId()).eq("type", 2));
        p.setMov(mov == null ? "" : mov.getUrl());
        //展示销量
        int i = getSaleNum(p.getId());
        p.setSaleNum(p.getBaseSaleCount() + i);
        //获取规格
        List<CreditCargoSku> sku = this.creditCargoSkuService.list(Wrappers.<CreditCargoSku>query().eq("cargo_id", p.getId()).eq("status",1));
        p.setSku(sku);
    }


    /**
     * 获取规格
     * @param bean
     */
    private void getSku(CreditCargo bean) {
        List<CreditCargoSku> sku = this.creditCargoSkuService.list(Wrappers.<CreditCargoSku>query().eq("cargo_id", bean.getId()).eq("status",1));
        bean.setSku(sku);
    }

    /**
     * 获得真实销量
     */
    public Integer getSaleNum(String id) {
        int i = creditOrderService.count(new QueryWrapper<CreditOrder>().eq("cargo_id", id).in("status", 2,3,4));
        return i;
    }


    /**
     * 更新库存
     */
    public synchronized void updateInventory(String skuId) {
        System.out.println(skuId+"fsdf 更新库存");
        var i=creditWarehousingMapper.sumSku(skuId);
        new CreditCargoSku().setId(skuId).setInventory(i).updateById();

    }
}