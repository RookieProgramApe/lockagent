package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.lxkj.entity.*;
import com.lxkj.facade.DeliveryLookupService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "订单")
@Slf4j
@RestController
@RequestMapping("/api/member/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DeliveryLookupService deliveryLookupService;
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    @Autowired
    private LianpayService lianpayService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private GiftcardService giftcardService;
    @Autowired
    private RetailerRewardService retailerRewardService;
    @Autowired
    private RetailerService rtailerService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;
    @Autowired
    private InstallerService installerService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private InstallerOrderService installerOrderService;
    @Autowired
    private WxPayService wxPayService;
    @ApiOperation("订单分页查询")
    @PostMapping("/queryOrder")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "status", value = "0=全部,2=已支付，3=已发货，4=已完成", required = true),
    })
    public JsonResults<List<Order>> queryOrder(Long page, Long limit, Integer status) {
        String memberId = this.getToken();
        String about_contact = this.configService.queryForString("about_contact");
        var data = this.orderService.page(new Page<Order>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Order>().eq("member_id", memberId)
                        .eq(status != 0, "status", status)
                        .in("status", 2, 3, 4)
                        .orderByDesc("create_time"));
        data.getRecords().stream().forEach(p -> {
            //封面
            var picture = this.cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id", p.getCargoId()).eq("type", 1));
            p.setCargoImage(picture == null ? "" : picture.getUrl());
            //客服电话
            p.setPhone(about_contact);
            //提货卡号
            if(StringUtils.isNotBlank(p.getGiftcardId())){
                p.setGiftcardNum(giftcardService.getById(p.getGiftcardId()).getSerial());
            }
        });
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("确认收货")
    @PostMapping("/endOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "id", dataType = "String", value = "订单ID", required = true)
    })
    @LoginRequired
    public JsonResults endOrder(@RequestParam String id) {
        var order = orderService.getById(id);
        if (order.getStatus() == 1) {
            return BuildFailJson("该订单未支付，请刷新当前界面");
        }
        if (order.getStatus() == 4) {
            return BuildFailJson("该订单已完成，请刷新当前界面");
        }
        orderService.updateById(new Order().setId(order.getId()).setStatus(4));
        return BuildSuccessJson("已完成订单");
    }


    @ApiOperation("申请安装-查看列表")
    @PostMapping("/orderByaz")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "id", dataType = "String", value = "订单ID", required = true)
    })
    @LoginRequired
    public JsonResults<List<Installer>> orderByaz(@RequestParam String id) {
        var order = orderService.getById(id);
        List<Installer> list=new ArrayList<>();
        var retailerGiftcard = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", order.getGiftcardId()));//卡的代理商
        if (retailerGiftcard != null) {
            var rtailer = rtailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailerGiftcard.getMemberId()));
            if (StringUtils.isNotBlank(rtailer.getInstallerMobile())) {
                var installer=new Installer();
                installer.setName("同城安装");
                installer.setContact(rtailer.getInstallerMobile());
                list.add(installer);
            }
        }
        var lisa=installerService.list(new QueryWrapper<Installer>().eq("enabled",1).orderByDesc("create_time"));
        if(!lisa.isEmpty()){
            list.addAll(lisa);
        }
        return BuildSuccessJson(list,"查询成功");
    }

    @ApiOperation(value = "查看物流跟踪",notes = "必须是已发货状态才可以看")
    @PostMapping("/findLogistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "订单ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "deliveryProvider:'快递公司',orderNum:物流运单号,process:[{Waybill_No:'运单号',Upload_Time:'时间',ProcessInfo:'跟踪内容'}]"),
    })
    public JsonResults<?> findLogistics(@RequestParam String id) {
        var order = orderService.getById(id);
        List<DeliveryLookupService.WaybillProcessInfo> list = new ArrayList<>();
        try {
            if(StringUtils.isNotBlank(order.getDeliveryTrack())){
                list = deliveryLookupService.lookup(order.getDeliveryTrack());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BuildSuccessJson(Map.of("process",list,"orderNum",order.getDeliveryTrack(),"deliveryProvider",order.getDeliveryProvider()), "查询成功");
    }




    @ApiOperation("安装下单")
    @LoginRequired
    @PostMapping("/addOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "orderId", dataType = "String", value = "订单ID", required = true),
            @ApiImplicitParam(name = "installerId", dataType = "String", value = "安装ID", required = true),
    })
    @Transactional
    public JsonResults addOrder(@RequestParam String orderId,@RequestParam String installerId) {
        String memberId = this.getToken();
        var order = orderService.getById(orderId);//订单
        var member = memberService.getById(memberId);
        var installer=installerService.getById(installerId);
        if (order ==null) {
            return BuildFailJson("订单不存在");
        }
        var sum=installerOrderService.count(new QueryWrapper<InstallerOrder>()
                .eq("order_id",order.getId())
                .eq("status",1));
        if(sum>1){
            return BuildFailJson("该订单，您已经申请过安装了，请勿重复操作");
        }
        InstallerOrder inOrder=new InstallerOrder();
        inOrder.setInstallerId(installer.getId());
        inOrder.setMemberId(memberId);
        inOrder.setOrderId(order.getId());
        inOrder.setTotalPrice(installer.getPrice());
        inOrder.setCreateTime(new Date());
        inOrder.setOrder(order);
        inOrder.setStatus(1);
        inOrder.setOrderNo(ID.nextGUID());
        inOrder.insert();
        /* ******************连连-第三方支付接口**************************/
//        var data = this.lianpayService.prepay_install(inOrder,order.getIpaddr());
//        if (data == null) {
//            return BuildFailJson("下单失败，可能支付有问题，请联系平台管理");
//        }
//        String ret_code = data.getOrDefault("ret_code", "999").toString();
//        if (ret_code.equals("0000")) {//成功
//            String payload = data.get("payload").toString();
//            JSONObject jsonObj = JSONObject.parseObject(payload);
//            return BuildSuccessJson(Map.of("payload", jsonObj, "ret_code", "0000"), "下单成功");
//        } else {
//            String ret_msg = data.getOrDefault("ret_msg", "下单失败").toString();
//            return BuildFailJson(ret_msg);
//        }
        /* *****************微信支付支付接口**************************/
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setAttach("installerOrder");
        request.setBody("安装服务");
        request.setOutTradeNo(inOrder.getOrderNo());
        request.setTotalFee(BaseWxPayRequest.yuanToFen(inOrder.getTotalPrice().toString()));
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
