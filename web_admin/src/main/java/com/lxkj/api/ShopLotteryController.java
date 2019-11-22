package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.*;
import com.lxkj.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家抽奖接口
 * @author Zhanqian
 * @date 2019/11/12 10:18
 */
@Api(tags = "商家抽奖接口")
@Slf4j
@RestController
@RequestMapping("/api/lottery")
public class ShopLotteryController extends BaseController {

    @Autowired
    private ShopLotteryService shopLotteryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private LotteryPrizeService prizeService;

    @Autowired
    private RetailerGiftcardService rgService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private GiftcardService giftcardService;

    @Autowired
    private LotteryRecordService recordService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private MemberService memberService;

    @ApiOperation("查询当前抽奖的信息")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "lotteryId", value = "抽奖id", required = true)
    })
    @PostMapping("/queryLotteryById")
    public JsonResults<ShopLottery> queryLotteryById(String lotteryId) {
        String memberId = this.getToken();
        // 查询抽奖的基本信息
        ShopLottery lottery = shopLotteryService.getById(lotteryId);

        // 查询抽奖商家的信息
        lottery.setShop(shopService.getById(lottery.getShopId()));

        // 查询奖池信息
        lottery.setPrizeList(prizeService.list(new QueryWrapper<LotteryPrize>().eq("lottery_id", lottery.getId())));

        // 查询当前用户的中奖记录
        List<LotteryRecord> myRecords = recordService.list(new QueryWrapper<LotteryRecord>().eq("member_id", memberId).eq("lottery_id", lottery.getId()).isNotNull("prize_id").ne("prize_id", ""));
//        // 查询用户中奖记录的详细信息
//        myRecords.stream().forEach(p -> {
//
//            // 查询中奖奖品信息
//            p.setPrize(prizeService.getById(p.getPrizeId()));
//        });
        lottery.setMyRecords(myRecords);


        // 查询所有的中奖记录
        List<LotteryRecord> records = recordService.list(new QueryWrapper<LotteryRecord>().eq("lottery_id", lottery.getId()).isNotNull("prize_id").ne("prize_id", ""));

//        // 查询中奖记录的详细信息
//        records.stream().forEach(p -> {
//            // 查询中奖用户信息
//            p.setMember(memberService.getById(p.getMemberId()));
//
//            // 查询中奖奖品信息
//            p.setPrize(prizeService.getById(p.getPrizeId()));
//        });
        lottery.setRecords(records);

        return BuildSuccessJson(lottery, "查询成功");
    }

    @ApiOperation("返回一个当前商家的卡号和卡密")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "lotteryId", value = "抽奖id", required = true)
    })
    @PostMapping("/queryGifcardByLotteryId")
    public JsonResults<Giftcard> queryGifcardByLotteryId(String lotteryId) {
        // 查询抽奖的基本信息
        ShopLottery lottery = shopLotteryService.getById(lotteryId);

        // 查询抽奖商家的信息
        Shop shop = shopService.getById(lottery.getShopId());

        // 查询服务商信息
        // Retailer retailer = retailerService.getById(shop.getRetailerId());

        // 查询一张轻奢卡片
        Giftcard giftcard = this.jdbcTemplate.queryForObject("select * from giftcard where id=(select giftcard_id from retailer_giftcard where member_id=? and state=0 and `type`=1 LIMIT 1)", Giftcard.class, shop.getMemberId());

        return BuildSuccessJson(giftcard, "查询成功");
    }

    @ApiOperation("新增抽奖记录")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "lotteryId", value = "抽奖id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "prizeId", value = "奖品id（如果没抽中则为null）", required = true)
    })
    @PostMapping("/addLotteryRecord")
    public JsonResults addLotteryRecord(String lotteryId, String prizeId) {
        String memberId = this.getToken();
        if (StringUtils.isNotBlank(lotteryId)){
            LotteryRecord record = new LotteryRecord().setLotteryId(lotteryId).setPrizeId(prizeId).setMemberId(memberId);
            record.setMemberName(memberService.getById(memberId).getNickname());

            ShopLottery lottery = shopLotteryService.getById(lotteryId);
            // 抽奖总人数
            lottery.setPartakeCount(lottery.getPartakeCount() + 1);
            // 中奖了
            if (StringUtils.isNotBlank(prizeId)){
                // 更新中奖人数
                lottery.setWinCount(lottery.getWinCount() + 1);
                // 更新奖品库存
                LotteryPrize prize = prizeService.getById(prizeId);
                prize.setCount(prize.getCount() > 0?prize.getCount()-1:0);
                prize.updateById();

                record.setPrizeName(prizeService.getById(prizeId).getCargoName());
            }
            // 插入抽奖记录
            recordService.save(record);
            shopLotteryService.updateById(lottery);
        }

        return BuildSuccessJson("保存成功");
    }

    @ApiOperation("获得安心值")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "integral", value = "安心值数量", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "lotteryId", value = "抽奖id", required = true),
    })
    @PostMapping("/addIntegral")
    public JsonResults queryLotteryCount(Integer integral, String lotteryId) {
        String memberId = this.getToken();
        if(integral != null && !integral.equals(0)){
            // 获取当前用户信息
            Member member = memberService.getById(memberId);
            // 更新安心值
            memberService.update(new UpdateWrapper<Member>().set("integral", member.getIntegral().add(new BigDecimal(integral))).eq("`id`", member.getId()));
            // 插入抽奖记录
            LotteryRecord record = new LotteryRecord().setLotteryId(lotteryId).setPrizeId("===" + lotteryId).setPrizeName(integral + "颗安心").setMemberId(memberId).setMemberName(member.getNickname());
            record.insert();
            // 插入安心值记录
            new MemberCredit()
                    .setMemberId(member.getId())
                    .setTitle("抽奖获得")
                    .setType(1)
                    .setAmount(BigDecimal.ZERO)
                    .setPoint(new BigDecimal(integral))
                    .insert();
        }

        return BuildSuccessJson(0,"保存成功");
    }

    @ApiOperation("查询当前用户抽奖次数")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "lotteryId", value = "抽奖id", required = true)
    })
    @PostMapping("/queryLotteryCount")
    public JsonResults queryLotteryCount(String lotteryId) {
        String memberId = this.getToken();
        if (StringUtils.isNotBlank(lotteryId)){
            Integer count = recordService.count(new QueryWrapper<LotteryRecord>().eq("lottery_id", lotteryId).eq("member_id", memberId));
            return BuildSuccessJson(count,"保存成功");
        }

        return BuildSuccessJson(0,"保存成功");
    }

    @ApiOperation("根据商品id查询商品类型")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "cargoId", value = "商品id", required = true)
    })
    @PostMapping("/queryCargoType")
    public JsonResults queryCargoType(String cargoId) {

        return BuildSuccessJson(cargoService.getById(cargoId).getType(),"保存成功");
    }

}
