package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.FileMappingProperties;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.ID;
import com.lxkj.common.util.QrCodeUtil;
import com.lxkj.common.util.Strings;
import com.lxkj.common.util.billUtil;
import com.lxkj.common.util.collection.Maps;
import com.lxkj.entity.*;
import com.lxkj.facade.WxService;
import com.lxkj.mapper.MemberSubordinateMapper;
import com.lxkj.mapper.RetailerMapper;
import com.lxkj.service.*;
import io.swagger.annotations.*;
import jodd.util.Util;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Api(tags = "代理商中心接口", description = "查询商品、商品详情、下单支付")
@Slf4j
@RestController
@RequestMapping("/api/retailer")
public class RetailerController extends BaseController {
    @Autowired
    private FileMappingProperties fileUtil;
    @Autowired
    private WxService wxService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private ConfigService configService;

    @Autowired
    private MemberSubordinateService memberSubordinateService;

    @Autowired
    private RetailerGiftcardService retailerGiftcardService;

    @Autowired
    private CardOrderService cardOrderService;
    @Resource
    private MemberSubordinateMapper memberSubordinateMapper;
    @Resource
    private RetailerMapper retailerMapper;
    @Autowired
    private GiftcardService giftcardService;
    @Autowired
    private CardOrdersService cardOrdersService;

    @Value("${picture.baillUrl}")
    private String baillUrl;

    @ApiOperation(value = "查询我申请代理状态", notes = "提交申请后，查询申请状态，审核通过才进入代理商中心界面")
    @PostMapping("/status")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    public JsonResults<Retailer> queryJoinStatus() {
        String memberId = this.getToken();
        Retailer data = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        if(data!=null&&data.getStatus()==2){
            data.setStatus(0);
        }
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation(value = "代理商中心", notes = "已审核通过,调用该接口")
    @PostMapping("/center")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    public JsonResults<Retailer> center() {
        String memberId = this.getToken();
        // 会员信息
        Member member = this.memberService.getById(memberId);
        // 代理商信息
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        retailer.setAvatar(member.getAvatar());
        if(retailer.getStatus()==2){
            retailer.setStatus(0);
        }
        // 累计收益
        BigDecimal accruedIncome = this.transactionService.queryAccruedIncome(memberId);
        retailer.setAccruedIncome(accruedIncome);
        // 我的卡片数量
        int giftcardCount = this.retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", memberId).eq("state", 0));
        retailer.setGiftcardCount(giftcardCount);
        // 直属团队数量
        int subordinateCount = this.retailerService.count(Wrappers.<Retailer>query().eq("parent_member_id", memberId).eq("`type`", 1));
        retailer.setSubordinateCount(subordinateCount);
        //次属团队数量
        int dinateCount = this.jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id in (select member_id from retailer where  parent_member_id=? and `type`=1)",Integer.class,memberId);
        retailer.setDinateCount(dinateCount);
        return BuildSuccessJson(retailer, "查询成功");
    }

    @ApiOperation(value = "申请加入代理", notes = "二维码邀请界面接口、申请加入代理接口、重新提交资料")
    @PostMapping("/join")
    @LoginRequired
    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "parentMemberId", value = " 邀请人ID(可填)"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "手机号(首次必填，下一次不需要填)"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = " 姓名", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "gender", value = "性别", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "identity", value = "身份证号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "city", value = "所在的地区", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "city", value = "所在的地区", required = true),
    })
    public JsonResults<?> join(
            String parentMemberId,
            String phone,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String identity,
            @RequestParam String city) {
        String memberId = this.getToken();
        Retailer retailer = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", memberId));
        if (retailer == null) {
            if (StringUtils.isBlank(phone)) {
                return BuildFailJson("手机号不能为空");
            }
            retailer = new Retailer();
            retailer.setPhone(phone);
        }
        retailer.setName(name);
        retailer.setGender(gender);
        retailer.setIdentity(identity);
        retailer.setCity(city);
        retailer.setMemberId(memberId);
        retailer.setStatus(0);
        //有邀请人
        if (StringUtils.isNotBlank(parentMemberId)) {
            retailer.setParentMemberId(parentMemberId);
//            MemberSubordinate ms1 = new MemberSubordinate();
//            ms1.setLevel(1);
//            ms1.setMemberId(parentMemberId);
//            ms1.setSubordinateMemberId(memberId);
//            this.memberSubordinateService.save(ms1);
//            MemberSubordinate grandParent = this.memberSubordinateService.getOne(Wrappers.<MemberSubordinate>query()
//                    .eq("subordinate_member_id", parentMemberId)
//                    .eq("`level`", 1));
//            if (grandParent != null) {
//                MemberSubordinate ms2 = new MemberSubordinate();
//                ms2.setLevel(2);
//                ms2.setMemberId(grandParent.getMemberId());
//                ms2.setSubordinateMemberId(memberId);
//                this.memberSubordinateService.save(ms2);
//            }
        }
        this.retailerService.saveOrUpdate(retailer);
        this.memberService.update(Wrappers.<Member>update().set("retailer_id", retailer.getId()).eq("id", memberId));
        return BuildSuccessJson("操作成功");
    }

    @ApiOperation(value = "申请加入商家", notes = "商家二维码邀请界面接口、申请加入商家接口、重新提交资料")
    @PostMapping("/joinStore")
    @LoginRequired
    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "parentMemberId", value = " 邀请人ID(可填)", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "手机号(首次必填，下一次不需要填)"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = " 姓名", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "gender", value = "性别", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "identity", value = "身份证号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "city", value = "所在的地区", required = true)
    })
    public JsonResults<?> joinStore(
            @RequestParam String parentMemberId,
            String phone,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String identity,
            @RequestParam String city) {
        String memberId = this.getToken();
        Retailer retailer = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", memberId));
        if (retailer == null) {
            if (StringUtils.isBlank(phone)) {
                return BuildFailJson("手机号不能为空");
            }
            retailer = new Retailer();
            retailer.setPhone(phone);
        }
        retailer.setType(3);
        retailer.setName(name);
        retailer.setGender(gender);
        retailer.setIdentity(identity);
        retailer.setCity(city);
        retailer.setMemberId(memberId);

        // 商家不需要审核
        retailer.setStatus(1);

        //有邀请人
        if (StringUtils.isNotBlank(parentMemberId)) {
            retailer.setParentMemberId(parentMemberId);
//            MemberSubordinate ms1 = new MemberSubordinate();
//            ms1.setLevel(1);
//            ms1.setMemberId(parentMemberId);
//            ms1.setSubordinateMemberId(memberId);
//            this.memberSubordinateService.save(ms1);
//            MemberSubordinate grandParent = this.memberSubordinateService.getOne(Wrappers.<MemberSubordinate>query()
//                    .eq("subordinate_member_id", parentMemberId)
//                    .eq("`level`", 1));
//            if (grandParent != null) {
//                MemberSubordinate ms2 = new MemberSubordinate();
//                ms2.setLevel(2);
//                ms2.setMemberId(grandParent.getMemberId());
//                ms2.setSubordinateMemberId(memberId);
//                this.memberSubordinateService.save(ms2);
//            }
        }
        this.retailerService.saveOrUpdate(retailer);

        // 会员表插入商家id并改变会员身份为商家
        this.memberService.update(Wrappers.<Member>update().set("retailer_id", retailer.getId()).set("isretailer", 3).eq("id", memberId));
        return BuildSuccessJson("操作成功");
    }


    @ApiOperation("代理商完善资料")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "installerMobile", value = "安装电话"),
            @ApiImplicitParam(name = "bankHolder", value = "开户人"),
            @ApiImplicitParam(name = "bank", value = "开户行"),
            @ApiImplicitParam(name = "bankAccountNumber", value = "银行卡号")
    })
    @PostMapping("/updateRetailerInfo")
    @LoginRequired
    public JsonResults<?> updateRetailerInfo(String installerMobile, String bankHolder, String bank, String bankAccountNumber) {
        String memberId = this.getToken();
        this.retailerService.update(Wrappers.<Retailer>update()
                .set("installer_mobile", installerMobile)
                .set("bank_holder", bankHolder)
                .set("bank", bank)
                .set("bank_account_number", bankAccountNumber)
                .eq("member_id", memberId));
        return BuildSuccessJson("操作成功");
    }

    @ApiOperation("提现中心")
    @PostMapping("/withdraw")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{withdrawable:'可提现金额',withdraw_note:'提现说明',withdraw_min:'最低提现金额'}"),
    })
    public JsonResults<Map<String, ?>> withdrawCentre() {
        String memberId = this.getToken();
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        // 最低提现金额
        BigDecimal withdraw_min = this.configService.queryForDecimal("withdraw_min");
        // 提现说明
        String withdraw_note = this.configService.queryForString("withdraw_note");
        Map<String, Object> data = Maps.<String, Object>builder()
                .put("withdrawable", retailer.getBalance()) // 可提现
                .put("withdraw_min", withdraw_min) // 最低提现金额
                .put("withdraw_note", withdraw_note) // 提现说明
                .build();
        return BuildSuccessJson(data, "查询成功");
    }


    @ApiOperation("提现记录")
    @PostMapping("/withdrawRecord")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    public JsonResults<List<Transaction>> withdrawRecord() {
        String memberId = this.getToken();
        // 提现记录
        List<Transaction> withdraw_record = this.transactionService.list(Wrappers.<Transaction>query()
                .eq("member_id", memberId)
                .eq("type", 99)
                .orderByDesc("create_time"));
        return BuildSuccessJson(withdraw_record, "查询成功");
    }


    @ApiOperation(value = "申请提现", notes = "该接口做了判断限制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额")
    })
    @PostMapping("/withdrawPropose")
    @LoginRequired
    @Transactional
    public JsonResults<?> proposeWithdraw(@RequestParam String amount) {
        BigDecimal price = new BigDecimal(amount);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return BuildFailJson("提现金额不能为0");
        }
        String memberId = this.getToken();
        //提现金额设置
        BigDecimal max = this.transactionService.queryWithdrawable(memberId);
        if (max.compareTo(price) < 0) {
            return BuildFailJson("您可提现金额不足");
        }
        //提现次数限制
        Integer withdraw_day = this.configService.queryForInt("withdraw_day");
        Integer count = this.transactionService.count(Wrappers.<Transaction>query()
                .eq("member_id", memberId)
                .eq("type", 99)
                .eq("date_format(create_time, '%Y%m%d')", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
        if (count >= withdraw_day) {
            return BuildFailJson("提现次数已超当日最大次数");
        }
        // 最低提现金额
        BigDecimal withdraw_min = this.configService.queryForDecimal("withdraw_min");
        if (price.compareTo(withdraw_min) < 0) {
            return BuildFailJson("单笔提现金额必须大于最小提现金额，请重新输入");
        }

        //提现手续费最低金额
        BigDecimal withdraw_commissionMin = this.configService.queryForDecimal("withdraw_commissionMin");
        //提现手续费率(%)
        BigDecimal withdraw_commission = this.configService.queryForDecimal("withdraw_commission");
        var commission = BigDecimal.ZERO;
        if (withdraw_commission.compareTo(BigDecimal.ZERO) > 0) {
            commission = price.multiply(withdraw_commission).divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN);
            if (commission.compareTo(withdraw_commissionMin) < 0) {
                commission = withdraw_commissionMin;
            }
        }
        //提现申请
        Transaction withdraw = new Transaction();
        withdraw.setCommission(commission);
        withdraw.setType(99);
        withdraw.setStatus(0);
        withdraw.setAmount(price.subtract(commission));
        withdraw.setMemberId(memberId);
        this.transactionService.save(withdraw);
        //余额
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        retailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer.getBalance().subtract(price)).eq("id", retailer.getId()));
        return BuildSuccessJson("操作成功");
    }

    @ApiOperation("我的团队")
    @PostMapping("/subordinate")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data=[{memberId: '成员ID',name:'姓名',teamcount:'直属团队人数',phone:'电话',bought:'卡片数量',create_time:'加入时间',dinateCount:'次属团队'}...]"),
    })
    public JsonResults queryMySubordinate(Long page, Long limit) {
        String memberId = this.getToken();
        var data = this.memberSubordinateMapper.querySubordinate(new Page<Map>(page != null ? page : 1, limit != null ? limit : 10), memberId);
        data.getRecords().stream().forEach(p->{
            int dinateCount = this.jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id in (select member_id from retailer where  parent_member_id=? and `type`=1)",Integer.class,p.get("memberId").toString());
            p.put("dinateCount",dinateCount);
        });
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("Ta的团队")
    @PostMapping("/queryOtherSubordinate")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "memberId", value = " 成员ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data=[{name:'姓名',teamcount:'直属团队人数',phone:'电话',bought:'卡片数量',create_time:'加入时间',dinateCount:'次属团队'}...]"),
    })
    public JsonResults queryOtherSubordinate(@RequestParam String memberId,Long page, Long limit) {
        var data = this.memberSubordinateMapper.querySubordinate(new Page<Map>(page != null ? page : 1, limit != null ? limit : 10), memberId);
        data.getRecords().stream().forEach(p->{
            int dinateCount = this.jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id in (select member_id from retailer where  parent_member_id=? and `type`=1)",Integer.class,p.get("memberId").toString());
            p.put("dinateCount",dinateCount);
        });
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }



    @ApiOperation("资金流水-流水列表")
    @PostMapping("/finance")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = " 1=卡片奖励 2=提货佣金", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码(type=1,需要传)"),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数(type=1,需要传)", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "type=2,data=[{cargoName:'商品名称',saleCount:'销量',totalPrice:'销售额', drpAward:'获得奖励金额'}...];" +
                    "type=1,data=[{phone:'代理商电话',name:'姓名',orderNum:'数量(套)', orderPrice:'销售额', amount:'获得奖励金额'}...]"),
    })
    public JsonResults queryFinance(Integer type,
                                    Long page,
                                    Long limit) {
        String memberId = this.getToken();
        if (type == 2) {  // 汇聚查询提货佣金
            List<Map<String, Object>> commission = this.retailerMapper.queryCommission(memberId);
            return BuildSuccessJson(commission, "查询成功");
        } else if (type == 1) {//卡片奖励
            var data = this.retailerMapper.queryCardAward(new Page<Map>(page != null ? page : 1, limit != null ? limit : 10), memberId);
            return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
        }
        return BuildSuccessJson(null, "查询成功");
    }


    @ApiOperation(value = "我的卡片-卡片情况", notes = "总量，已用，剩余")
    @PostMapping("/giftcard")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data={leftCount:'剩余可用',totalCount:'总量',usedCount:'已用'}"),
    })
    public JsonResults<Map<String, Object>> queryGiftcard() {
        String memberId = this.getToken();
        // 查询总量
        Integer totalCount = this.jdbcTemplate.queryForObject("select count(1) from retailer_giftcard where member_id = ?", Integer.class, memberId);
        // 查询已用
        Integer usedCount = this.jdbcTemplate.queryForObject("select count(1) from retailer_giftcard where member_id = ? and state=1", Integer.class, memberId);
        Map<String, Object> data = Maps.<String, Object>builder()
                .put("leftCount", totalCount - usedCount) // 剩余可用
                .put("totalCount", totalCount) // 总量
                .put("usedCount", usedCount) // 已用
                .build();
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation(value = "我的卡片-购买记录", notes = "购买订单列表")
    @PostMapping("/cardOrderList")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码"),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data={leftCount:'剩余可用',totalCount:'总量',usedCount:'已用'}"),
    })
    public JsonResults<List<CardOrder>> cardOrderList(Long page, Long limit) {
        String memberId = this.getToken();
        // 查询购买记录
        IPage<CardOrder> data = this.cardOrderService.page(
                new Page<CardOrder>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<CardOrder>()
                        .eq("member_id", memberId)
                        .orderByDesc("create_time"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation(value = "我的卡片-详情界面数据", notes = "卡片单价、卡片说明、卡片库存")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "type", value = "卡片类型(1轻奢卡 2贵族卡 3至尊卡)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data={cardPrice:'卡片单价格/套',totalCount:'库存(套)',cardNote:'卡片说明'}"),
    })
    @PostMapping("/cardInfo")
    @LoginRequired
    @Transactional
    public JsonResults cardInfo(@RequestParam Integer type) {
        if(type == null || type.equals(0)){
            return BuildSuccessJson("卡片类型有误");
        }else{
            BigDecimal cardPrice = new BigDecimal(0);
            String cardNote = "";
            switch (type){
                case 1:
                    cardPrice = this.configService.queryForDecimal("card_price");//单价
                    cardNote = this.configService.queryForString("card_note");//卡片说明
                    break;
                case 2:
                    cardPrice = this.configService.queryForDecimal("card_price_b");//单价
                    cardNote = this.configService.queryForString("card_note_b");//卡片说明
                    break;
                case 3:
                    cardPrice = this.configService.queryForDecimal("card_price_c");//单价
                    cardNote = this.configService.queryForString("card_note_c");//卡片说明
                    break;
                default: break;
            }
            Integer count = cardOrderService.queryCount(type);//剩余库存
            return BuildSuccessJson(Map.of("cardPrice", cardPrice, "cardNote", cardNote, "totalCount", count), "操作成功，请等待审核");

        }

    }

    @ApiOperation(value = "我的卡片-商家购买卡片获取卡片信息", notes = "卡片单价、卡片说明、卡片库存")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "type", value = "卡片类型(1轻奢卡 2贵族卡 3至尊卡)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data={cardPrice:'卡片单价/张',totalCount:'库存(张)',cardNote:'卡片说明'}"),
    })
    @PostMapping("/cardInfos")
    @LoginRequired
    @Transactional
    public JsonResults cardInfos(@RequestParam Integer type) {
        String memberId = this.getToken();
        // 获取商家信息
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        // 获取商家的事业合伙人信息
        Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));

        if(type == null || type.equals(0)){
            return BuildSuccessJson("卡片类型有误");
        }else{
            BigDecimal cardPrice = new BigDecimal(0);
            String cardNote = "";
            switch (type){
                case 1:
                    cardPrice = this.configService.queryForDecimal("card_price");//单价
                    cardNote = this.configService.queryForString("card_note");//卡片说明
                    break;
                case 2:
                    cardPrice = this.configService.queryForDecimal("card_price_b");//单价
                    cardNote = this.configService.queryForString("card_note_b");//卡片说明
                    break;
                case 3:
                    cardPrice = this.configService.queryForDecimal("card_price_c");//单价
                    cardNote = this.configService.queryForString("card_note_c");//卡片说明
                    break;
                default: break;
            }
            // 获取事业合伙人身上的剩余卡片数量
            Integer count = retailerGiftcardService.count(new QueryWrapper<RetailerGiftcard>()
                                                                .eq("member_id", retailer1.getMemberId())
                                                                .eq("`type`", type)
                                                                .eq("`status`", 0)
                                                                .eq("`state`", 0));
            return BuildSuccessJson(Map.of("cardPrice", cardPrice.divide(new BigDecimal("50"), 0, RoundingMode.HALF_DOWN), "cardNote", cardNote, "totalCount", count), "操作成功，请等待审核");

        }

    }


    @ApiOperation("购买卡片(下订单)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "count", value = "数量(套)", required = true),
            @ApiImplicitParam(name = "addressId", dataType = "String", value = "收货地址ID", required = true),
    })
    @PostMapping("/buyCard")
    @LoginRequired
    @Transactional
    public JsonResults<?> buyCard(@RequestParam Integer count, @RequestParam String addressId) {
        String memberId = this.getToken();
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        Integer totalCount = cardOrderService.queryCount();//剩余库存
        if (count > totalCount) {
            return BuildFailJson("卡片库存不足，请联系平台工作人员");
        }
        var adddr = memberAddressService.getById(addressId);//收货地址
        CardOrder co = new CardOrder();
        co.setMemberId(memberId);
        co.setRetailerId(retailer.getId());
        co.setCount(count);
        BigDecimal unitPrice = this.configService.queryForDecimal("card_price");
        co.setTotalPrice(unitPrice.multiply(new BigDecimal(count)));
        co.setStatus(1);
        co.setRecipient(adddr.getRecipient());
        co.setMobile(adddr.getMobile());
        co.setCity(adddr.getProvince() + adddr.getCity() + adddr.getCounty());
        co.setAddress(adddr.getAddress());
        String nextSequence = this.jdbcTemplate.queryForObject("select next_sequence_text('delivery') from dual", String.class);
        co.setSequence(nextSequence);
        this.cardOrderService.save(co);
        return BuildSuccessJson("操作成功，请等待审核");
    }

    @ApiOperation("商家购买卡片(下订单)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "count", dataType = "Integer", value = "数量（张）", required = true),
            @ApiImplicitParam(name = "type", dataType = "Integer", value = "卡片类型(1轻奢卡 2贵族卡 3至尊卡)", required = true),
            @ApiImplicitParam(name = "remark", dataType = "String", value = "备注（非必选）"),
            @ApiImplicitParam(name = "addressId", dataType = "String", value = "收货地址ID", required = true),
    })
    @PostMapping("/buyCards")
    @LoginRequired
    @Transactional
    public JsonResults<?> buyCards(@RequestParam Integer count, @RequestParam Integer type, @RequestParam String addressId, String remark) {
        String memberId = this.getToken();
        // 获取商家信息
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        // 获取商家的事业合伙人信息
        Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));


        Integer totalCount = 0;
        if (count == null || count.equals(0)){
            return BuildFailJson("亲，您输入的数量有误，请重试");
        }else{
            // 获取事业合伙人身上的剩余卡片数量
            totalCount = retailerGiftcardService.count(new QueryWrapper<RetailerGiftcard>()
                    .eq("member_id", retailer1.getMemberId())
                    .eq("`type`", type)
                    .eq("`status`", 0)
                    .eq("`state`", 0));
            if (count > totalCount) {
                return BuildFailJson("亲，卡片库存不足，请联系平台工作人员");
            }else{
                // 新增一个商家卡片订单
                var adddr = memberAddressService.getById(addressId);//收货地址

                CardOrder cos = new CardOrder();
                cos.setMemberId(memberId);
                cos.setRetailerId(retailer.getId());
                cos.setCount(count);
                cos.setCardType(type);
                cos.setStatus(1);
                cos.setCreateTime(new Date());
                cos.setRemark(remark);
                String nextSequence = this.jdbcTemplate.queryForObject("select next_sequence_text('delivery') from dual", String.class);
                cos.setSequence(nextSequence);
                cos.setOrderType(3);

                cos.setRecipient(adddr.getRecipient());
                cos.setMobile(adddr.getMobile());
                cos.setCity(adddr.getProvince() + adddr.getCity() + adddr.getCounty());
                cos.setAddress(adddr.getAddress());
                cos.insert();
                return BuildSuccessJson("购买成功，请联系事业合伙人审核");
            }
        }
    }

    @ApiOperation("我的二维码-邀请事业合伙人")
    @PostMapping("/qr")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "url", value = "链接地址", required = true),
    })
    public JsonResults<String> queryQr(String url) {
        String memberId = this.getToken();
        var user = memberService.getById(memberId);
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        String baill = retailer.getQr();
        if (StringUtils.isBlank(baill)) {
            String qrcode = QrCodeUtil.createQrCode(url, fileUtil.getPath(), fileUtil.getMapping());
            try {
                baill = billUtil.generateImg(user.getAvatar(), baillUrl, qrcode, retailer.getName(), fileUtil.getPath(), fileUtil.getMapping());
            } catch (Exception e) {
                e.printStackTrace();
                return BuildFailJson("生成失败");
            }
            if (StringUtils.isNotBlank(baill)) {
                this.retailerService.update(Wrappers.<Retailer>update().set("qr", baill).eq("id", retailer.getId()));
            } else {
                return BuildFailJson("生成失败");
            }
        }
        return BuildSuccessJson(baill, "查询成功");
    }

    /**
     * 事业合伙人审批商家卡片订单
     *
     * @param
     * @return
     */
    @ApiOperation("事业合伙人审批商家卡片订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "cardOrderId", dataType = "Integer", value = "商家卡片订单id", required = true),
            @ApiImplicitParam(name = "status", dataType = "Integer", value = "事业合伙人意见（2 同意， 3拒绝）", required = true),
            @ApiImplicitParam(name = "lb", dataType = "Integer", value = "0自发 1代发", required = true)
    })
    @PostMapping("/checkCardOrders")
    @LoginRequired
    @Transactional
    public JsonResults endCheck(@RequestParam String cardOrderId, @RequestParam String status, @RequestParam Integer lb) {
        String memberId = this.getToken();
        // 选择是否代发
        cardOrderService.update(new UpdateWrapper<CardOrder>().set("`lb`", lb).eq("`id`", cardOrderId));
        CardOrder cardOrder = cardOrderService.getById(cardOrderId);

        if (status.equals(2)){
            // 审核通过 分配卡片
            cardOrderService.finshCardOrders(cardOrder.getId(), cardOrder.getRetailstate());
        }else if (status.equals(3)){
            // 拒绝
            cardOrder.update(new UpdateWrapper<CardOrder>().set("`status`", 3).eq("`id`", cardOrderId));
        }else{
            return BuildSuccessJson("异常");
        }

        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        if (cardOrder.getStatus() == 2) {
            flow.setState("终审通过");
            flow.setRemark(cardOrder.getRemark());
        } else if (cardOrder.getStatus() == 3) {
            flow.setState("拒绝");
            flow.setRemark(cardOrder.getReson());
        } else if (cardOrder.getStatus() == 1) {
            flow.setState("打回重审");
            flow.setRemark(cardOrder.getRemark());
        }
        flow.setCardOrderId(cardOrder.getId());
        flow.setCreateBy("123456");
        flow.insert();
        return BuildSuccessJson("修改成功");
    }

    @ApiOperation(value = "事业合伙人-查看商家购卡订单信息", notes = "购买订单列表")
    @PostMapping("/storeCardOrderList")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "limit", value = "每页记录数", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data=[{status:'1 待审核，2 已通过，3 已拒绝'}]"),
    })
    public JsonResults<List<CardOrder>> storeCardOrderList(@RequestParam Integer page, @RequestParam Integer limit) {
        String memberId = this.getToken();
        List<String> memberIds = this.jdbcTemplate.queryForList("select member_id from retailer where parent_member_id=? and `type`=3", String.class, memberId);
        if(memberIds.size()>0) {
            // 查询购买记录
            IPage<CardOrder> data = this.cardOrderService.page(
                    new Page<CardOrder>(page != null ? page : 1, limit != null ? limit : 10),
                    new QueryWrapper<CardOrder>()
                            .in("member_id", memberIds)
                            .eq("order_type", 3)
                            .orderByDesc("create_time"));
            return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
        }else{
            return BuildSuccessJson(new ArrayList<CardOrder>(), 0L, "查询成功");
        }
    }


}
