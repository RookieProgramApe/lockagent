package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.exception.BusinessException;
import com.lxkj.entity.*;
import com.lxkj.mapper.CardOrderMapper;
import com.lxkj.mapper.WarehousingMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    @Autowired
    protected JdbcTemplate jdbcTemplate;
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

    public Integer queryCount(Integer type) {
        int i = giftcardService.count(new QueryWrapper<Giftcard>().eq("status", 1).eq("`type`", type));
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
            int i = this.queryCount(o.getCardType());//卡片剩余库存(套)
            int sum = buyCount * 50;//总共多少张
            if (buyCount > i) {
                throw new BusinessException("卡片库存不足");
            }
            int lb=retailstate==1?1:0;
            //代理商卡片生成
            var addList = giftcardService.list(new QueryWrapper<Giftcard>().eq("status", 1).eq("`type`", o.getCardType()).orderByAsc("serial").last("limit " + sum + ""));
            addList.stream().forEach(p -> {
                //新增关联
                RetailerGiftcard rg = new RetailerGiftcard();
                rg.setMemberId(o.getMemberId());
                rg.setOrderId(o.getId());
                rg.setGiftcardId(p.getId());
                rg.setLb(lb);
                rg.setType(p.getType());
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
                // 事业合伙人分销
                if(retailer.getType().equals(1)){
                    if (StringUtils.isNotBlank(retailer.getParentMemberId())) {
                        /*************************************************************一级卡片奖励****************************************************************************/
                        Retailer retailer_p = this.rtailerService.getOne(Wrappers.<Retailer>query().eq("member_id", retailer.getParentMemberId()));//直接领导人
                        if (retailer_p != null) {
                            // 根据不同卡片类型分配不同卡片奖励
                            var frstAward = new BigDecimal(0);
                            if(o.getCardType().equals(1)){
                                frstAward = retailer_p.getFirstAward();
                            }else if(o.getCardType().equals(2)) {
                                frstAward = retailer_p.getFirstAwardb();
                            }else if(o.getCardType().equals(3)) {
                                frstAward = retailer_p.getFirstAwardc();
                            }else{
                                frstAward = new BigDecimal(0);
                            }

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
                            // 根据不同卡片类型分配不同卡片奖励
                            var secondAward = new BigDecimal(0);
                            if(o.getCardType().equals(1)){
                                secondAward = retailer_p_p.getSecondAward();
                            }else if(o.getCardType().equals(2)) {
                                secondAward = retailer_p_p.getSecondAwardb();
                            }else if(o.getCardType().equals(3)) {
                                secondAward = retailer_p_p.getSecondAwardc();
                            }else{
                                secondAward = new BigDecimal(0);
                            }
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
    }

    /**
     * 完成商家卡片订单-（终审、分配卡片）
     *
     * @return
     */
    @Transactional
    public JsonResults finshCardOrders1(String cardOrderId) {
        var o = this.getById(cardOrderId);

        // 获取商家信息
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", o.getMemberId()));
        // 获取商家的事业合伙人信息
        Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));

        if (o.getStatus() == 1) {//当前环节-完成初审核
            int sum = o.getCount();//购买量（张）
            int i = retailerGiftcardService.count(new QueryWrapper<RetailerGiftcard>()
                    .eq("member_id", retailer1.getMemberId())
                    .eq("`type`", o.getCardType())
                    .eq("`status`", 0)
                    .eq("`state`", 0));//卡片剩余库存
            if (sum > i) {
                // throw new BusinessException("卡片库存不足");
                return new JsonResults(200, "卡片库存不足");
            }
            List<RetailerGiftcard> addList = jdbcTemplate.query("select rg.*,(select g.serial from giftcard g where g.id=rg.giftcard_id) as serial from retailer_giftcard rg where rg.member_id=? and rg.`status`=0 and rg.`state`=0 and rg.`type`=? ORDER BY serial limit ?",
                    new BeanPropertyRowMapper<>(RetailerGiftcard.class), retailer1.getMemberId(), o.getCardType(), sum);
            //获取事业合伙人未分配给商家的卡片
//            var addList = retailerGiftcardService.list(new QueryWrapper<RetailerGiftcard>()
//                    .eq("member_id", retailer1.getMemberId())
//                    .eq("status", 0)
//                    .eq("`state`", 0)
//                    .eq("`type`", o.getCardType())
//                    .last("limit " + sum + ""));
            addList.stream().forEach(p -> {
                //新增关联
                RetailerGiftcard rg = new RetailerGiftcard();
                rg.setMemberId(o.getMemberId());
                rg.setOrderId(o.getId());
                rg.setGiftcardId(p.getGiftcardId());
                rg.setType(o.getCardType());
                // rg.setStatus(1);
                // 分配给商家
                rg.insert();
                // 更新代理商手中卡片状态
                p.update(new UpdateWrapper<RetailerGiftcard>().set("`status`", 1).eq("`id`", p.getId()));
            });

            //更新状态
            this.update(new UpdateWrapper<CardOrder>().set("status", 2).eq("id", o.getId()));
        }
        return new JsonResults(200, "操作成功");
    }

    /**
     * 完成抽奖商家卡片分配
     *
     * @return
     * @param retailerId 商家代理商表的id
     * @param sum 分配卡片数量
     */
    @Transactional
    public JsonResults finshCardOrders2(String retailerId, Integer sum) {

        // 获取商家信息
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("`id`", retailerId));
        // 获取商家的事业合伙人信息
//        Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));
        // 默认为轻奢卡
        int cardType = 1;
        int i = retailerGiftcardService.count(new QueryWrapper<RetailerGiftcard>()
//                    .eq("member_id", retailer1.getMemberId())
                    .eq("member_id", "16f76f03610398f9c7bd5ecc625cfcfd")
                    .eq("`type`", cardType)
                    .eq("`status`", 0)
                    .eq("`state`", 0));//卡片剩余库存
            if (sum > i) {
                // throw new BusinessException("卡片库存不足");
                return new JsonResults(200, "卡片库存不足");
            }
            List<RetailerGiftcard> addList = jdbcTemplate.query("select rg.*,(select g.serial from giftcard g where g.id=rg.giftcard_id) as serial from retailer_giftcard rg where rg.member_id=? and rg.`status`=0 and rg.`state`=0 and rg.`type`=? ORDER BY serial limit ?",
//                    new BeanPropertyRowMapper<>(RetailerGiftcard.class), retailer1.getMemberId(), cardType, sum);
                    new BeanPropertyRowMapper<>(RetailerGiftcard.class), "16f76f03610398f9c7bd5ecc625cfcfd", cardType, sum);

            addList.stream().forEach(p -> {
                //新增关联
                RetailerGiftcard rg = new RetailerGiftcard();
                rg.setMemberId(retailer.getMemberId());
                rg.setOrderId("===" + retailer.getId());
                rg.setGiftcardId(p.getGiftcardId());
                rg.setType(cardType);
                // 分配给商家
                rg.insert();
                // 更新代理商手中卡片状态
                p.update(new UpdateWrapper<RetailerGiftcard>().set("`status`", 1).eq("`id`", p.getId()));
            });

            //更新状态
            // this.update(new UpdateWrapper<CardOrder>().set("status", 2).eq("id", o.getId()));
        return new JsonResults(200, "操作成功");
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
            Cargo cargo = cargoService.getById(o.getCargoId());

            // 更新虚拟销量 随机数1-5
            cargo.setBaseSaleCount(cargo.getBaseSaleCount() + (new Random().nextInt(5) + 1));
            cargoService.update(new UpdateWrapper<Cargo>().set("base_sale_count", cargo.getBaseSaleCount() + new Random().nextInt(10)).eq("`id`", cargo.getId()));

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
            /*******************************提货奖励********************************/
            //提货奖励
            if(StringUtils.isNotBlank(o.getGiftcardId())){
//                // 获取当前卡的代理商
//                var retailerGiftcard1 = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", o.getGiftcardId()).eq("`status`", 1));
//                // 获取上级卡片代理商
//                var retailerGiftcard = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", o.getGiftcardId()).eq("`status`", 0));
                List<RetailerGiftcard> rgList = retailerGiftcardService.list(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", o.getGiftcardId()));
                rgList.forEach(rg -> {
                    if (rg != null) {
                        var retailerReward = retailerRewardService.getOne(new QueryWrapper<RetailerReward>().eq("member_id", rg.getMemberId()).eq("cargo_id", o.getCargoId()));
                        if (retailerReward != null) {
//                         && retailerReward.getFigure().compareTo(BigDecimal.ZERO) > 0
                            Retailer retailer = this.rtailerService.getById(retailerReward.getRetailerId());
                            /*************************区分卡片类型分配提货奖励***********************/
                            // 根据不同卡片类型分配不同提货奖励
                            /************************后续可能要加**************************/
//                            var figure = new BigDecimal(0);
//
//                            if(cargo.getCardType().equals(1)){
//                                figure = retailerReward.getFigure();
//                            }else if(cargo.getCardType().equals(2)) {
//                                figure = retailerReward.getFigureb();
//                            }else if(cargo.getCardType().equals(3)) {+
//                                figure = retailerReward.getFigurec();
//                            }else{
//                                figure = new BigDecimal(0);
//                            }
                            // 目前是根据使用的卡的类型区分提货奖励
                            var figure = new BigDecimal(0);
                            Giftcard giftcard = giftcardService.getById(o.getGiftcardId());
                            if(giftcard.getType().equals(1)){
                                figure = retailerReward.getFigure();
                            }else if(giftcard.getType().equals(2)) {
                                figure = retailerReward.getFigureb();
                            }else if(giftcard.getType().equals(3)) {
                                figure = retailerReward.getFigurec();
                            }else{
                                figure = new BigDecimal(0);
                            }
                            // 获取提货佣金
                            rtailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer.getBalance().add(figure)).eq("id", retailer.getId()));
                            Transaction transaction = new Transaction();
                            transaction.setMemberId(retailer.getMemberId());
                            transaction.setOrderId(o.getId());
                            transaction.setType(81);
                            transaction.setAmount(figure);
                            transaction.setStatus(1);
                            transactionService.save(transaction);
                        }
                    }
                });


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
     * 积分商品
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

            Cargo cargo = cargoService.getById(o.getCargoId());
            // 更新虚拟销量 随机数1-5
            cargo.setBaseSaleCount(cargo.getBaseSaleCount() + (new Random().nextInt(5) + 1));
            cargoService.update(new UpdateWrapper<Cargo>().set("base_sale_count", cargo.getBaseSaleCount() + new Random().nextInt(10)).eq("`id`", cargo.getId()));
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