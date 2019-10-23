package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.exception.BusinessException;
import com.lxkj.entity.*;
import com.lxkj.mapper.CardOrderMapper;
import com.lxkj.mapper.WarehousingMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 卡片订单 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-18
 */
@Service
public class CardOrderService extends ServiceImpl<CardOrderMapper, CardOrder> {
    @Autowired
    private RetailerService retailerService;
    @Resource
    private GiftcardService giftcardService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;
    @Autowired
    private RetailerService rtailerService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MemberService mmberService;
    @Autowired
    private RetailerRewardService retailerRewardService;
    @Autowired
    private CargoService cargoService;
    @Resource
    private WarehousingMapper warehousingMapper;
    @Autowired
    private BargainOrderService bargainOrderService;
    /**
     * 卡片剩余库存
     *
     * @return
     */
    public Integer queryCount() {
        int i = giftcardService.count(new QueryWrapper<Giftcard>().eq("status", 1));
        if (i < 50) {
            return 0;
        }
        var sum = new BigDecimal(i).divide(new BigDecimal("50"), 0, RoundingMode.HALF_DOWN);
        return sum.intValue();
    }


    /**
     * 完成卡片订单-（终审、分配卡片、卡片二级分销奖励）
     *
     * @return
     */
    @Transactional
    public void finshCardOrders(String cardOrderId,int retailstate) {
        var o = this.getById(cardOrderId);
        if (o.getStatus() == 4) {//当前环节-完成初审核
            int buyCount = o.getCount();//购买量(套)
            int i = this.queryCount();//卡片剩余库存(套)
            int sum = buyCount * 50;//总共多少张
            if (buyCount > i) {
                throw new BusinessException("卡片库存不足");
            }
            int lb=retailstate==1?1:0;
            //代理商卡片生成
            var addList = giftcardService.list(new QueryWrapper<Giftcard>().eq("status", 1).orderByAsc("serial").last("limit " + sum + ""));
            addList.stream().forEach(p -> {
                //新增关联
                RetailerGiftcard rg = new RetailerGiftcard();
                rg.setMemberId(o.getMemberId());
                rg.setOrderId(o.getId());
                rg.setGiftcardId(p.getId());
                rg.setLb(lb);
                rg.insert();
                //更新
                p.setStatus(2).updateById();
            });
            //更新状态
            this.update(new UpdateWrapper<CardOrder>().set("status", 2).set("retailstate",retailstate).eq("id", o.getId()));
            //1已收费(可计分销)
            if(retailstate==1){
                //卡片一级分销奖励金额
                var retailer = rtailerService.getById(o.getRetailerId());//该代理商信息
                if (StringUtils.isNotBlank(retailer.getParentMemberId())) {
                    /*************************************************************一级卡片奖励****************************************************************************/
                    Retailer retailer_p = this.rtailerService.getOne(Wrappers.<Retailer>query().eq("member_id", retailer.getParentMemberId()));//直接领导人
                    if (retailer_p != null) {
                        var frstAward = retailer_p.getFirstAward();
                        if (frstAward.compareTo(BigDecimal.ZERO) > 0) {
                            var sacount = frstAward.multiply(new BigDecimal(sum));//一级卡片奖励金额
                            rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer_p.getBalance().add(sacount)).eq("id", retailer_p.getId()));
                            Transaction transaction = new Transaction();
                            transaction.setMemberId(retailer_p.getMemberId());
                            transaction.setOrderId(o.getId());
                            transaction.setType(80);
                            transaction.setAmount(sacount);
                            transaction.setStatus(1);
                            transactionService.save(transaction);
                        }
                    }
                    /*************************************************************二级卡片奖励****************************************************************************/
                    if (retailer_p != null && StringUtils.isNotBlank(retailer_p.getParentMemberId())) {
                        Retailer retailer_p_p = this.rtailerService.getOne(Wrappers.<Retailer>query().eq("member_id", retailer_p.getParentMemberId()));//上上级领导人
                        var secondAward = retailer_p_p.getSecondAward();
                        if (secondAward.compareTo(BigDecimal.ZERO) > 0) {
                            var dacount = secondAward.multiply(new BigDecimal(sum));//一级卡片奖励金额
                            rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer_p_p.getBalance().add(dacount)).eq("id", retailer_p_p.getId()));
                            Transaction transaction = new Transaction();
                            transaction.setMemberId(retailer_p_p.getMemberId());
                            transaction.setOrderId(o.getId());
                            transaction.setType(80);
                            transaction.setAmount(dacount);
                            transaction.setStatus(1);
                            transactionService.save(transaction);
                        }
                    }
                }
            }

        }
    }


    /**
     * 完成提货订单-（更改为已支付、提货奖励、库存减少、增加积分、消费积分）
     *
     * @return
     */
    @Transactional
    public void finishOrder(String no_order) {
        var o = orderService.getOne(new QueryWrapper<Order>().eq("ordernum", no_order));
        if (o != null && o.getStatus() == 1) {//待支付
            var member = mmberService.getById(o.getMemberId());
            //更新订单状态
            orderService.update(new UpdateWrapper<Order>().set("status", 2).eq("id", o.getId()));
            //更新卡片状态
            if(StringUtils.isNotBlank(o.getGiftcardId())){
                retailerGiftcardService.update(new UpdateWrapper<RetailerGiftcard>().set("state", 1).eq("giftcard_id", o.getGiftcardId()));
                giftcardService.update(new UpdateWrapper<Giftcard>().set("status", 3).eq("id", o.getGiftcardId()));
            }
            //奖励积分
            BigDecimal credit_reward_rate = this.configService.queryForDecimal("credit_reward_rate");
            var integral = member.getIntegral();
            if (credit_reward_rate.compareTo(BigDecimal.ZERO) > 0) {
                var addCredit = o.getTotalPrice().multiply(credit_reward_rate);
                integral = integral.add(addCredit);
                mmberService.update(new UpdateWrapper<Member>().set("integral", integral).eq("id", member.getId()));
                new MemberCredit()
                        .setMemberId(member.getId())
                        .setTitle("获得消费积分")
                        .setType(1)
                        .setOrderId(o.getId())
                        .setAmount(o.getTotalPrice())
                        .setPoint(addCredit)
                        .insert();
            }
            //增加消费积分流水
            if (o.getCredit().compareTo(BigDecimal.ZERO) > 0) {
                integral = integral.subtract(o.getCredit());
                mmberService.update(new UpdateWrapper<Member>().set("integral", integral).eq("id", member.getId()));
                new MemberCredit()
                        .setMemberId(member.getId())
                        .setTitle("积分消费")
                        .setType(-1)
                        .setOrderId(o.getId())
                        .setAmount(o.getCreditDiscount())
                        .setPoint(o.getCredit())
                        .insert();
            }
            //提货奖励
            if(StringUtils.isNotBlank(o.getGiftcardId())){
                var retailerGiftcard = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", o.getGiftcardId()));//卡的代理商
                if (retailerGiftcard != null) {
                    var retailerReward = retailerRewardService.getOne(new QueryWrapper<RetailerReward>().eq("member_id", retailerGiftcard.getMemberId()).eq("cargo_id", o.getCargoId()));
                    if (retailerReward != null && retailerReward.getFigure().compareTo(BigDecimal.ZERO) > 0) {
                        Retailer retailer = this.rtailerService.getById(retailerReward.getRetailerId());
                        rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer.getBalance().add(retailerReward.getFigure())).eq("id", retailer.getId()));
                        Transaction transaction = new Transaction();
                        transaction.setMemberId(retailer.getMemberId());
                        transaction.setOrderId(o.getId());
                        transaction.setType(81);
                        transaction.setAmount(retailerReward.getFigure());
                        transaction.setStatus(1);
                        transactionService.save(transaction);
                    }
                }
            }
            //更新库存
            if(!o.getSkuId().equals("-1")){
                var i = warehousingMapper.sumSku(o.getSkuId());
                //砍价订单更新状态
                if(o.getType()==2&&StringUtils.isNotBlank(o.getBargainOrderId())){
                    bargainOrderService.update(new UpdateWrapper<BargainOrder>()
                            .set("status",2)
                            .eq("id",o.getBargainOrderId()));
                }
                new CargoSku().setId(o.getSkuId()).setInventory(i == null ? 0 : i).updateById();
            }

        }
    }

    /**
     * 完成提货订单-（更改为已支付、提货奖励、库存减少、增加积分、消费积分）  积分兑换
     * @param no_order
     */
    @Transactional
    public void finishOrder1(String no_order) {
        var o = orderService.getOne(new QueryWrapper<Order>().eq("ordernum", no_order));
        if (o != null && o.getStatus() == 1) {//待支付
            var member = mmberService.getById(o.getMemberId());
            //更新订单状态
            orderService.update(new UpdateWrapper<Order>().set("status", 2).eq("id", o.getId()));
            //更新卡片状态
            if(StringUtils.isNotBlank(o.getGiftcardId())){
                retailerGiftcardService.update(new UpdateWrapper<RetailerGiftcard>().set("`use`", 1).eq("giftcard_id", o.getGiftcardId()));
                giftcardService.update(new UpdateWrapper<Giftcard>().set("`use`", 1).eq("id", o.getGiftcardId()));
            }
            //奖励积分
            BigDecimal credit_reward_rate = this.configService.queryForDecimal("credit_reward_rate");
            var integral = member.getIntegral();
            if (credit_reward_rate.compareTo(BigDecimal.ZERO) > 0) {
                var addCredit = o.getTotalPrice().multiply(credit_reward_rate);
                integral = integral.add(addCredit);
                mmberService.update(new UpdateWrapper<Member>().set("integral", integral).eq("id", member.getId()));
                new MemberCredit()
                        .setMemberId(member.getId())
                        .setTitle("获得消费积分")
                        .setType(1)
                        .setOrderId(o.getId())
                        .setAmount(o.getTotalPrice())
                        .setPoint(addCredit)
                        .insert();
            }
            //增加消费积分流水
            if (o.getCredit().compareTo(BigDecimal.ZERO) > 0) {
                integral = integral.subtract(o.getCredit());
                mmberService.update(new UpdateWrapper<Member>().set("integral", integral).eq("id", member.getId()));
                new MemberCredit()
                        .setMemberId(member.getId())
                        .setTitle("积分消费")
                        .setType(-1)
                        .setOrderId(o.getId())
                        .setAmount(o.getCreditDiscount())
                        .setPoint(o.getCredit())
                        .insert();
            }
            //提货奖励
            if(StringUtils.isNotBlank(o.getGiftcardId())){
                var retailerGiftcard = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", o.getGiftcardId()));//卡的代理商
                if (retailerGiftcard != null) {
                    var retailerReward = retailerRewardService.getOne(new QueryWrapper<RetailerReward>().eq("member_id", retailerGiftcard.getMemberId()).eq("cargo_id", o.getCargoId()));
                    if (retailerReward != null && retailerReward.getFigure().compareTo(BigDecimal.ZERO) > 0) {
                        Retailer retailer = this.rtailerService.getById(retailerReward.getRetailerId());
                        rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer.getBalance().add(retailerReward.getFigure())).eq("id", retailer.getId()));
                        Transaction transaction = new Transaction();
                        transaction.setMemberId(retailer.getMemberId());
                        transaction.setOrderId(o.getId());
                        transaction.setType(81);
                        transaction.setAmount(retailerReward.getFigure());
                        transaction.setStatus(1);
                        transactionService.save(transaction);
                    }
                }
            }
            //更新库存
            if(!o.getSkuId().equals("-1")){
                var i = warehousingMapper.sumSku(o.getSkuId());
                //砍价订单更新状态
                if(o.getType()==2&&StringUtils.isNotBlank(o.getBargainOrderId())){
                    bargainOrderService.update(new UpdateWrapper<BargainOrder>()
                            .set("status",2)
                            .eq("id",o.getBargainOrderId()));
                }
                new CargoSku().setId(o.getSkuId()).setInventory(i == null ? 0 : i).updateById();
            }

        }
    }


    /**
     * 提现处理
     */
    @Transactional
    public void finshiTransaction(String id, Integer status,String res) {
        var bean=transactionService.getById(id);
        if(bean.getStatus()!=0){
            throw new BusinessException("提现状态不对");
        }
        if(status==1){//成功
            transactionService.update(new UpdateWrapper<Transaction>().eq("id",bean.getId()).set("status",1).set("comment",res));
        }else if(status==99){//失败
            Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", bean.getMemberId()));
            if(retailer!=null){
                retailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer.getBalance().add(bean.getAmount()).add(bean.getCommission())).eq("id", retailer.getId()));
            }
            transactionService.update(new UpdateWrapper<Transaction>().eq("id",bean.getId()).set("comment",res).set("status",99));
        }

    }
}