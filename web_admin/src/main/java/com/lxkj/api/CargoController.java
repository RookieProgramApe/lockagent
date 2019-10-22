package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.DateUtil;
import com.lxkj.common.util.ID;
import com.lxkj.entity.Cargo;
import com.lxkj.entity.Giftcard;
import com.lxkj.entity.Order;
import com.lxkj.facade.LianpayService;
import com.lxkj.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Api(tags = "商城接口", description = "查询商品、商品详情、下单支付")
@Slf4j
@RestController
@RequestMapping("/api/cargo")
public class CargoController extends BaseController {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private CargoSkuService cargoSkuService;
    @Autowired
    private BargainService bargainService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private GiftcardService giftcardService;

    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private CargoCategoryService cargoCategoryService;

    @Autowired
    private LianpayService lianpayService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private WxPayService wxPayService;
    @ApiOperation("商品分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "模糊搜索[关键字]"),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
    })
    @PostMapping("/all")
    public JsonResults<List<Cargo>> queryCargoList(Long page, Long limit, String keyword) {
        IPage<Cargo> data = this.cargoService.page(
                new Page<Cargo>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Cargo>()
                        .eq("status", 1)
                        .eq("type", 1)
                        .eq("isdel", 0)
                        .like(StringUtils.isNotBlank(keyword), "name", keyword)
                        .orderByAsc("sort"));
        data.getRecords().forEach(p -> cargoService.getData(p));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("商品详情")
    @PostMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "商品ID", required = true)
    })
    public JsonResults<Cargo> queryCargoDetail(@RequestParam String id) {
        Cargo data = this.cargoService.getById(id);
        cargoService.getData(data);
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation("查询有多少积分能抵扣多少钱")
    @LoginRequired
    @PostMapping("/credit")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{credit:积分,discount:可抵扣的金额}"),
    })
    public JsonResults<Map<String, Number>> queryCredit() {
        String memberId = this.getToken();
        BigDecimal credit_discount_rate = this.configService.queryForDecimal("credit_discount_rate");
        BigDecimal credit = this.jdbcTemplate.queryForObject("select ifnull(sum(point * type), 0) from member_credit where member_id = ?", BigDecimal.class, memberId);
        BigDecimal discount = BigDecimal.ZERO;
        if (credit != null && credit.compareTo(BigDecimal.ZERO) > 0) {
            discount = credit.divide(credit_discount_rate, 2, RoundingMode.HALF_DOWN);
        }
        return BuildSuccessJson(Map.of("credit", credit, "discount", discount), "操作成功");
    }

    @ApiOperation("商品下单(支付)")
    @LoginRequired
    @PostMapping("/pay")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "cargoId", dataType = "String", value = "商品ID", required = true),
            @ApiImplicitParam(name = "skuId", dataType = "String", value = "商品规格ID", required = true),
            @ApiImplicitParam(name = "cateId", dataType = "String", value = "商品套餐ID", required = true),
            @ApiImplicitParam(name = "addressId", dataType = "String", value = "收货地址ID", required = true),
            @ApiImplicitParam(name = "isDeduct", dataType = "int", value = "是否需要抵扣 0=否 1=是", required = true),
            @ApiImplicitParam(name = "cardNum", dataType = "String", value = "卡号（开启卡片验证是必填）"),
            @ApiImplicitParam(name = "cardPwd", dataType = "String", value = "卡密（开启卡片验证是必填）"),
            @ApiImplicitParam(name = "remark", dataType = "String", value = "备注"),
    })
    @Transactional
    public JsonResults pay(@RequestParam String cargoId,
                           @RequestParam String skuId,
                           @RequestParam String cateId,
                           @RequestParam String addressId,
                           @RequestParam Integer isDeduct,
                           String cardNum,
                           String cardPwd,
                           String remark) {
        String memberId = this.getToken();
        var cargo = cargoService.getById(cargoId);//商品
        var cargoSku = cargoSkuService.getById(skuId);//规格

        var adddr = memberAddressService.getById(addressId);//收货地址
        var member = memberService.getById(memberId);
        /* 校验规则**/
        if (cargoSku.getInventory() <= 0) {//库存不足
            return BuildFailJson("该规格得商品库存不足");
        }
        /* 创建业务提货订单****/
        Order order = new Order();
        if(cargo.getIscard()==1){//开启卡片验证
            if(StringUtils.isBlank(cardNum)){
                return BuildFailJson("请输入卡号");
            }
            if(StringUtils.isBlank(cardPwd)){
                return BuildFailJson("请输入密码");
            }
            var card = giftcardService.getOne(new QueryWrapper<Giftcard>().eq("serial", cardNum.trim()).eq("passcode", cardPwd.trim()));
            if (card == null) {
                return BuildFailJson("您输入的卡号或卡密不正确");
            } else {
                if (card.getStatus() == 3) {
                    return BuildFailJson("该卡片已被使用，请换一个吧~");
                }
                order.setGiftcardId(card.getId());
            }
        }

        order.setCargoId(cargo.getId());
        order.setCargoName(cargo.getName());
        order.setSkuId(cargoSku.getId());
        order.setSkuName(cargoSku.getName());
        order.setType(1);
        order.setMemberId(memberId);
        order.setRemark(remark);
        order.setCreateTime(new Date());
        var credit = isDeduct == 1 ? member.getIntegral() : BigDecimal.ZERO;//抵扣积分
        order.setCredit(credit);
        var credit_discount = isDeduct == 1 ? memberService.integralTransformYuan(memberId) : BigDecimal.ZERO;//抵扣金额
        order.setCreditDiscount(credit_discount);
        order.setCount(1);//数量
        //将套餐id和名称放入订单中
        if(StringUtils.isNotBlank(cateId)){
            var category = cargoCategoryService.getById(cateId);
            order.setCateId(category.getId());
            order.setCateName(category.getName());
            order.setUnitPrice(category.getPrice());
            order.setTotalPrice(category.getPrice().subtract(credit_discount));
        }else{
            order.setUnitPrice(cargo.getSalePrice());//原价
            order.setTotalPrice(cargo.getSalePrice().subtract(credit_discount));//实际支付价格
        }
        order.setStatus(1);
        order.setRecipient(adddr.getRecipient());
        order.setMobile(adddr.getMobile());
        order.setCity(adddr.getCity());
        order.setProvince(adddr.getProvince());
        order.setCounty(adddr.getCounty());
        order.setAddress(adddr.getAddress());
        order.setOrdernum(ID.nextGUID());
        String nextSequence = this.jdbcTemplate.queryForObject("select next_sequence_text('delivery') from dual", String.class);
        order.setSequence(nextSequence);
        order.setIpaddr(getIpAddr(getRequest()));
        /* ******************连连-第三方支付接口**************************/
//        var data = this.lianpayService.prepay(order,order.getIpaddr());
//            if (data == null) {
//                return BuildFailJson("下单失败，可能支付有问题，请联系平台管理");
//            }
//            String ret_code = data.getOrDefault("ret_code", "999").toString();
//            if (ret_code.equals("0000")) {//成功
//                String payload = data.get("payload").toString();
//                JSONObject jsonObj = JSONObject.parseObject(payload);
//                return BuildSuccessJson(Map.of("payload", jsonObj, "ret_code", "0000"), "下单成功");
//            } else {
//                String ret_msg = data.getOrDefault("ret_msg", "下单失败").toString();
//                return BuildFailJson(ret_msg);
//            }
        /* *****************微信支付支付接口**************************/
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setBody("商品支付");
        request.setAttach("order");
        request.setOutTradeNo(order.getOrdernum());
        request.setTotalFee(BaseWxPayRequest.yuanToFen(order.getTotalPrice().toString()));
        request.setOpenid(member.getOpenId());
        request.setSpbillCreateIp("127.0.0.1");
        request.setTimeStart(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
        try {
            Object response = this.wxPayService.createOrder(request);
            order.insert();
            return BuildSuccessJson(Map.of("payload", response, "ret_code", "0000"), "下单成功");
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return BuildFailJson("下单失败");

    }



    @ApiOperation("砍价商品下单(支付)")
    @LoginRequired
    @PostMapping("/payByBargain")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "cargoId", dataType = "String", value = "商品ID", required = true),
            @ApiImplicitParam(name = "skuId", dataType = "String", value = "商品规格ID", required = true),
            @ApiImplicitParam(name = "addressId", dataType = "String", value = "收货地址ID", required = true),
            @ApiImplicitParam(name = "bargainOrderId", dataType = "String", value = "砍价订单ID", required = true),
            @ApiImplicitParam(name = "remark", dataType = "String", value = "备注"),
    })
    @Transactional
    public JsonResults payByBargain(@RequestParam String cargoId,
                                    @RequestParam String skuId,
                                    @RequestParam String addressId,
                                    @RequestParam String bargainOrderId,
                                    String remark) {
        String memberId = this.getToken();
        var cargo = cargoService.getById(cargoId);//商品
        var cargoSku = cargoSkuService.getById(skuId);//规格
        var adddr = memberAddressService.getById(addressId);//收货地址
        var member = memberService.getById(memberId);
        /* 校验规则**/
        if (cargoSku.getInventory() <= 0) {//库存不足
            return BuildFailJson("该规格得商品库存不足");
        }
        /* 创建业务提货订单****/
        Order order = new Order();
        order.setCargoId(cargo.getId());
        order.setCargoName(cargo.getName());
        order.setSkuId(cargoSku.getId());
        order.setSkuName(cargoSku.getName());
        order.setType(2);
        order.setMemberId(memberId);
        order.setRemark(remark);
        order.setCreateTime(new Date());
        order.setCredit(BigDecimal.ZERO);
        order.setCreditDiscount(BigDecimal.ZERO);
        order.setCount(1);//数量
        order.setUnitPrice(cargo.getSalePrice());//原价
        //已砍金额
        var ykPrice=bargainService.Yk_Amount(bargainOrderId);
        order.setBargainOrderId(bargainOrderId);
        order.setTotalPrice(cargo.getSalePrice().subtract(ykPrice));//实际成交价格
        order.setStatus(1);
        order.setRecipient(adddr.getRecipient());
        order.setMobile(adddr.getMobile());
        order.setCity(adddr.getCity());
        order.setProvince(adddr.getProvince());
        order.setCounty(adddr.getCounty());
        order.setAddress(adddr.getAddress());
        order.setOrdernum(ID.nextGUID());
        String nextSequence = this.jdbcTemplate.queryForObject("select next_sequence_text('delivery') from dual", String.class);
        order.setSequence(nextSequence);
        order.setIpaddr(getIpAddr(getRequest()));
        order.insert();
        /* ******************连连-第三方支付接口**************************/
//            var data = this.lianpayService.prepay(order,order.getIpaddr());
//            if (data == null) {
//                return BuildFailJson("下单失败，可能支付有问题，请联系平台管理");
//            }
//            String ret_code = data.getOrDefault("ret_code", "999").toString();
//            if (ret_code.equals("0000")) {//成功
//                String payload = data.get("payload").toString();
//                JSONObject jsonObj = JSONObject.parseObject(payload);
//                return BuildSuccessJson(Map.of("payload", jsonObj, "ret_code", "0000"), "下单成功");
//            } else {
//                String ret_msg = data.getOrDefault("ret_msg", "下单失败").toString();
//                return BuildFailJson(ret_msg);
//            }
        /* *****************微信支付支付接口**************************/
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setAttach("order");
        request.setBody("商品支付");
        request.setOutTradeNo(order.getOrdernum());
        request.setTotalFee(BaseWxPayRequest.yuanToFen(order.getTotalPrice().toString()));
        request.setOpenid(member.getOpenId());
        request.setSpbillCreateIp("127.0.0.1");
        request.setTimeStart(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
        try {
            Object response = this.wxPayService.createOrder(request);
            return BuildSuccessJson(Map.of("payload", response, "ret_code", "0000"), "下单成功");
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return BuildFailJson("下单失败");

    }
}
