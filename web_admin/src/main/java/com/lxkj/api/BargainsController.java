package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.exception.BusinessException;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.*;
import com.lxkj.facade.LianpayService;
import com.lxkj.mapper.BargainMapper;
import com.lxkj.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Api(tags = "砍价相关接口")
@Slf4j
@RestController
@RequestMapping("/api/Bargain")
public class BargainsController extends BaseController {
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CargoService cargoService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private LianpayService lianpayService;
    @Autowired
    private RetailerService rtailerService;
    @Autowired
    private BargainService bargainService;
    @Resource
    private BargainMapper brgainMapper;
    @Autowired
    private BargainStepService bargainStepService;
    @Autowired
    private BargainOrderService bargainOrderService;
    @Autowired
    private BargainOrderFlowService bargainOrderFlowService;


    @ApiOperation("砍价活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
    })
    @PostMapping("/list")
    @LoginRequired
    public JsonResults<List<Bargain>> list() {
        List<Bargain> data = bargainService.list(
                new QueryWrapper<Bargain>()
                        .eq("status", 1)
                        .eq("isdel", 0)
                        .orderByAsc("sort"));
        data.stream().forEach(p -> {
            //商品
            Cargo bean = cargoService.getById(p.getCargoId());
            cargoService.getData(bean);
            p.setCargo(bean);
            //原价
            p.setOriginalPrice(bean.getSalePrice());
            //实际参与人数
            p.setDoIng(bargainOrderService.count() + p.getBaseCount());
        });
        return BuildSuccessJson(data, "查询成功");
    }


    @ApiOperation("我的砍价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
    })
    @PostMapping("/mylist")
    @LoginRequired
    public JsonResults<List<BargainOrder>> mylist() {
        List<BargainOrder> data = bargainOrderService.list(
                new QueryWrapper<BargainOrder>()
                        .eq("member_id",getToken())
                        .orderByDesc("create_time"));
        data.stream().forEach(p -> {
            //商品
            Cargo bean = cargoService.getById(p.getCargoId());
            cargoService.getData(bean);
            p.setCargo(bean);
            //原价
            p.setOriginalPrice(bean.getSalePrice());
            //已砍次数
            p.setYkCount(bargainService.get_YK(p.getId()));
            //剩余次数（还需要完成次数）
            p.setWkCount(bargainService.get_WK(p.getId()));

        });
        return BuildSuccessJson(data, "查询成功");
    }


    @ApiOperation("砍价活动详情(商品详情)")
    @PostMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "id", dataType = "String", value = "砍价活动ID", required = true)
    })
    @LoginRequired
    public JsonResults<Bargain> detail(@RequestParam String id) {
        var data = this.bargainService.getById(id);
        //商品
        Cargo bean = cargoService.getById(data.getCargoId());
        cargoService.getData(bean);
        data.setCargo(bean);
        //原价
        data.setOriginalPrice(bean.getSalePrice());
        //剩余库存
        data.setKz(bargainService.querySurplus(data.getId()));
        return BuildSuccessJson(data, "查询成功");
    }


    @ApiOperation(value = "发起砍价", notes = "注意：砍价成功后，返回一个砍价订单ID，直接进入砍价订单详情界面")
    @PostMapping("/add")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "id", dataType = "String", value = "砍价活动ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{data:'砍价订单ID'}"),
    })
    @LoginRequired
    public JsonResults<String> add(@RequestParam String id) {
        var data = this.bargainService.getById(id);
        String orderId = bargainService.add_Bargain(data.getId(), getToken());
        return BuildSuccessJson(orderId, "查询成功");
    }



    @ApiOperation("我的砍价订单详情)")
    @PostMapping("/orderDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "orderId", dataType = "String", value = "砍价订单ID", required = true)
    })
    @LoginRequired
    public JsonResults<BargainOrder> orderDetail(@RequestParam String orderId) {
        //订单信息
        var order = this.bargainOrderService.getById(orderId);
        Member member=memberService.getById(order.getMemberId());
        order.setMemberAvatar(member.getAvatar());
        order.setMemberName(member.getNickname());
        //商品
        Cargo goods = cargoService.getById(order.getCargoId());
        cargoService.getData(goods);
        order.setCargo(goods);
        order.setOriginalPrice(goods.getSalePrice());
        order.setPicture(goods.getPicture());
        order.setSaleNum(goods.getSaleNum());
        order.setDescription(goods.getDescription());
        //砍价活动
        var act = this.bargainService.getById(order.getBargainId());
        order.setShareContent(act.getShareContent());
        order.setSharePic(act.getSharePic());
        order.setCnts(act.getCnts());
        //已砍金额
        var ykPrice=bargainService.Yk_Amount(order.getId());
        order.setYkPrice(ykPrice);
        //当前成交价格
        order.setEndPrice(goods.getSalePrice().subtract(ykPrice));
        //已砍次数
        var yk=bargainService.get_YK(order.getId());
        order.setYkCount(yk);
        //剩余次数（还需要完成次数）
        var wk=bargainService.get_WK(order.getId());
        order.setWkCount(wk);
        //帮TA砍了多少金额(当前用户)
        var heplePrice=bargainService.member_Yk_Amount(order.getId(),getToken());
        order.setHeplePrice(heplePrice);

        //是否可以帮Ta砍(true=已砍过，false=未砍过)
        int i = bargainOrderFlowService.count(new QueryWrapper<BargainOrderFlow>().eq("end_member_id", getToken()).eq("bargain_order_id", order.getId()));
        if (i > 0) {
            order.setIsHelp(true);
        }else{
            order.setIsHelp(false);
        }
        return BuildSuccessJson(order, "查询成功");
    }



    @ApiOperation(value = "帮TA砍价", notes = "注意：砍价成功后，返回一个帮砍价具体金额，展示当前页，并刷新砍价榜")
    @PostMapping("/cut")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "orderId", dataType = "String", value = "砍价订单ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{data:'砍价金额'}"),
    })
    @LoginRequired
    public JsonResults cut(@RequestParam String orderId) {
        BigDecimal price = bargainService.Help_Bargain(orderId, getToken());
        return BuildSuccessJson(price, "砍价成功");
    }


    @ApiOperation(value = "砍价榜")
    @PostMapping("/BargainingList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(name = "orderId", dataType = "String", value = "砍价订单ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{data:[{avatar:'头像',nickname:'昵称',price:'金额'}]}"),
    })
    @LoginRequired
    public JsonResults BargainingList(@RequestParam String orderId) {
        var list=bargainService.BargainingList(orderId);
        return BuildSuccessJson(list, "砍价成功");
    }

}
