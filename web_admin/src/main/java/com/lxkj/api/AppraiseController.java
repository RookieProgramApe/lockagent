package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.Appraise;
import com.lxkj.entity.Order;
import com.lxkj.service.AppraiseService;
import com.lxkj.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Zhanqian
 * @date 2019/10/24 9:56
 */
@Api(tags = "评价接口")
@Slf4j
@RestController
@RequestMapping("/api/appraise")
public class AppraiseController extends BaseController {

    @Autowired
    private AppraiseService appraiseService;

    @Autowired
    private OrderService orderService;

//    @ApiOperation("评价新增功能")
//    @PostMapping("/save")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "star", dataType = "String", value = "星级", required = true),
//            @ApiImplicitParam(name = "describe", dataType = "String", value = "描述", required = true)
//    })
//    public JsonResults queryCargoDetail(@RequestParam String star, @RequestParam String describe) {
////        Cargo data = this.cargoService.getById(id);
////        cargoService.getData(data);
//        return BuildSuccessJson("查询成功");
//    }

    @ApiOperation("评价新增功能")
    @PostMapping("/save")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "star", dataType = "String", value = "星级", required = true),
//            @ApiImplicitParam(name = "describe", dataType = "String", value = "描述", required = true)
//    })
    public JsonResults saveAppraise(Appraise appraise) {
        System.out.println(this.getToken());
        appraise.setCreateTime(new Date());
        //保存评价
        appraiseService.saveOrUpdate(appraise);

        //更改订单表的评价状态
        orderService.update(Wrappers.<Order>update().set("appraise_id", appraise.getId()).eq("`id`", appraise.getOrderId()));

        return BuildSuccessJson("保存成功");
    }

    @ApiOperation("根据订单id获取评价")
    @PostMapping("/getByOrderId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "订单ID", required = true)
    })
    public JsonResults<Appraise> getByOrderId(@RequestParam String id) {
        System.out.println("id=======" + id);
        Appraise appraise = appraiseService.getOne(new QueryWrapper<Appraise>().eq("order_id", id));
        return BuildSuccessJson(appraise,"保存成功");
    }

    @ApiOperation("根据id获取评价")
    @PostMapping("/getAppraiseById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "评价ID", required = true)
    })
    public JsonResults<Appraise> getAppraiseById(@RequestParam String id) {
        System.out.println("id==============" + id);
        Appraise appraise = appraiseService.getById(id);
        return BuildSuccessJson(appraise,"保存成功");
    }

    @ApiOperation("评价查询")
    @PostMapping("/queryAppraise")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true)
    })
    public JsonResults<List<Appraise>> queryAppraise(Long page, Long limit) {
        String memberId = this.getToken();
        var data = this.appraiseService.page(new Page<Appraise>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Appraise>().eq("member_id", memberId)
                        .in("is_del", 0)
                        .orderByDesc("create_time"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("根据商品查询评价")
    @PostMapping("/queryAppraiseByCargo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "id", value = "商品id", required = true),
    })
    public JsonResults<List<Appraise>> queryAppraise(Long page, Long limit, String id) {
        var data = this.appraiseService.page(new Page<Appraise>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Appraise>()
                        .eq("is_del", 0)
                        .eq("cargo_id", id)
                        .eq("status", 0)
                        .orderByDesc("create_time"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("根据商品查询热门评价")
    @PostMapping("/queryTopAppraiseByCargo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "id", value = "商品id", required = true),
    })
    public JsonResults<List<Appraise>> queryTopAppraiseByCargo(Long page, Long limit, String id) {
        var data = this.appraiseService.page(new Page<Appraise>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Appraise>()
                        .eq("is_del", 0)
                        .eq("is_top", 1)
                        .eq("cargo_id", id)
                        .eq("status", 0)
                        .orderByDesc("create_time"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

}
