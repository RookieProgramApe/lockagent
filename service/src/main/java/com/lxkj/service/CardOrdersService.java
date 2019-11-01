package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.exception.BusinessException;
import com.lxkj.entity.*;
import com.lxkj.mapper.CardOrdersMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 商家卡片订单Service
 * @author Zhanqian
 * @date 2019/11/1 17:05
 */
@Service
public class CardOrdersService extends ServiceImpl<CardOrdersMapper, CardOrders> {

    @Resource
    private GiftcardService giftcardService;

    /**
     * 完成卡片订单-（终审、分配卡片）
     *
     * @return
     */
//    @Transactional
//    public void finshCardOrders(String id) {
//        CardOrders o = this.getById(id);
//        if (o.getStatus() == 1) {//当前环节-完成审核，开始分配卡片
//
//            Integer totalCountA = 0;
//            Integer totalCountB = 0;
//            Integer totalCountC = 0;
//
//            if (o.getCountA() != null ||!o.getCountA().equals(0)){
//                totalCountA = giftcardService.count(new QueryWrapper<Giftcard>().eq("type", 1));  //查询轻奢卡剩余库存
//                if (o.getCountA() > totalCountA) {
//                    throw new BusinessException("轻奢卡库存不足，请联系平台工作人员");
//                }
//            }
//
//            if (o.getCountB() != null ||!o.getCountB().equals(0)){
//                totalCountB = giftcardService.count(new QueryWrapper<Giftcard>().eq("type", 2));  //查询贵族卡剩余库存
//                if (o.getCountB() > totalCountB) {
//                    throw new BusinessException("贵族卡库存不足，请联系平台工作人员");
//                }
//            }
//
//            if (o.getCountC() != null ||!o.getCountC().equals(0)){
//                totalCountC = giftcardService.count(new QueryWrapper<Giftcard>().eq("type", 3));  //查询至尊卡剩余库存
//                if (o.getCountC() > totalCountC) {
//                    throw new BusinessException("至尊卡库存不足，请联系平台工作人员");
//                }
//            }
//
//            int buyCount = this.count(new QueryWrapper<>().eq());//购买量(套)
//            int i = this.queryCount(o.getCardType());//卡片剩余库存(套)
//            if (buyCount > i) {
//                throw new BusinessException("卡片库存不足");
//            }
//            int lb=retailstate==1?1:0;
//            //代理商卡片生成
//            var addList = giftcardService.list(new QueryWrapper<Giftcard>().eq("status", 1).eq("`type`", o.getCardType()).orderByAsc("serial").last("limit " + sum + ""));
//            addList.stream().forEach(p -> {
//                //新增关联
//                RetailerGiftcard rg = new RetailerGiftcard();
//                rg.setMemberId(o.getMemberId());
//                rg.setOrderId(o.getId());
//                rg.setGiftcardId(p.getId());
//                rg.setLb(lb);
//                rg.insert();
//                //更新
//                p.setStatus(2).updateById();
//            });
//            //更新状态
//            this.update(new UpdateWrapper<CardOrder>().set("status", 2).set("retailstate",retailstate).eq("id", o.getId()));
//            //1已收费(可计分销)
//            if(retailstate==1){
//                //卡片一级分销奖励金额
//                var retailer = rtailerService.getById(o.getRetailerId());//该代理商信息
//                if (StringUtils.isNotBlank(retailer.getParentMemberId())) {
//                    /*************************************************************一级卡片奖励****************************************************************************/
//                    Retailer retailer_p = this.rtailerService.getOne(Wrappers.<Retailer>query().eq("member_id", retailer.getParentMemberId()));//直接领导人
//                    if (retailer_p != null) {
//                        // 根据不同卡片类型分配不同卡片奖励
//                        var frstAward = new BigDecimal(0);
//                        if(o.getCardType().equals(1)){
//                            frstAward = retailer_p.getFirstAward();
//                        }else if(o.getCardType().equals(2)) {
//                            frstAward = retailer_p.getFirstAwardb();
//                        }else if(o.getCardType().equals(3)) {
//                            frstAward = retailer_p.getFirstAwardc();
//                        }else{
//                            frstAward = new BigDecimal(0);
//                        }
//
//                        if (frstAward.compareTo(BigDecimal.ZERO) > 0) {
//                            var sacount = frstAward.multiply(new BigDecimal(sum));//一级卡片奖励金额
//                            rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer_p.getBalance().add(sacount)).eq("id", retailer_p.getId()));
//                            Transaction transaction = new Transaction();
//                            transaction.setMemberId(retailer_p.getMemberId());
//                            transaction.setOrderId(o.getId());
//                            transaction.setType(80);
//                            transaction.setAmount(sacount);
//                            transaction.setStatus(1);
//                            transactionService.save(transaction);
//                        }
//                    }
//                    /*************************************************************二级卡片奖励****************************************************************************/
//                    if (retailer_p != null && StringUtils.isNotBlank(retailer_p.getParentMemberId())) {
//                        Retailer retailer_p_p = this.rtailerService.getOne(Wrappers.<Retailer>query().eq("member_id", retailer_p.getParentMemberId()));//上上级领导人
//                        // 根据不同卡片类型分配不同卡片奖励
//                        var secondAward = new BigDecimal(0);
//                        if(o.getCardType().equals(1)){
//                            secondAward = retailer_p_p.getSecondAward();
//                        }else if(o.getCardType().equals(2)) {
//                            secondAward = retailer_p_p.getSecondAwardb();
//                        }else if(o.getCardType().equals(3)) {
//                            secondAward = retailer_p_p.getSecondAwardc();
//                        }else{
//                            secondAward = new BigDecimal(0);
//                        }
//                        if (secondAward.compareTo(BigDecimal.ZERO) > 0) {
//                            var dacount = secondAward.multiply(new BigDecimal(sum));//一级卡片奖励金额
//                            rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer_p_p.getBalance().add(dacount)).eq("id", retailer_p_p.getId()));
//                            Transaction transaction = new Transaction();
//                            transaction.setMemberId(retailer_p_p.getMemberId());
//                            transaction.setOrderId(o.getId());
//                            transaction.setType(80);
//                            transaction.setAmount(dacount);
//                            transaction.setStatus(1);
//                            transactionService.save(transaction);
//                        }
//                    }
//                }
//            }
//
//        }
//    }
}
