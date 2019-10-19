package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.MemberAddress;
import com.lxkj.service.MemberAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "收货地址管理")
@Slf4j
@RestController
@RequestMapping("/api/address")
public class AddressController extends BaseController {

    @Autowired
    private MemberAddressService memberAddressService;

    @ApiOperation("【添加/修改】收货地址")
    @LoginRequired
    @PostMapping("/save")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    @Transactional
    public JsonResults<?> save(MemberAddress address) {
        String memberId = this.getToken();
        address.setMemberId(memberId);
        if (address.getDefaultAddress()) {
            this.memberAddressService.update(Wrappers.<MemberAddress>update().set("`default`", 0).eq("member_id", memberId));
        }
        this.memberAddressService.saveOrUpdate(address);
        return BuildSuccessJson("操作成功");
    }


    @ApiOperation("删除收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "主键", required = true)
    })
    @PostMapping("/delete")
    @Transactional
    public JsonResults<?> deleteAddress(String id) {
        if (id == null || id.isBlank()) {
            return BuildFailJson("主键不能为空");
        }
        this.memberAddressService.removeById(id);
        return BuildSuccessJson("操作成功");
    }

    @ApiOperation(value = "我的默认收货地址", notes = "注意：用户下订单的时候默认带入")
    @LoginRequired
    @PostMapping("/myAddress")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    public JsonResults<MemberAddress> myAddress() {
        var addr = this.memberAddressService.getOne(new QueryWrapper<MemberAddress>().eq("member_id", getToken()).eq("`default`", 1));
        return BuildSuccessJson(addr, "查询成功");
    }

    @ApiOperation("我的收货地址列表")
    @LoginRequired
    @PostMapping("/addressList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    public JsonResults<List<MemberAddress>> addressList() {
        var list = this.memberAddressService.list(new QueryWrapper<MemberAddress>().eq("member_id", getToken()).orderByDesc("`default`"));
        return BuildSuccessJson(list, "查询成功");
    }

    @ApiOperation("收货地址详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "地址ID", required = true)
    })
    @PostMapping("/detail")
    public JsonResults<MemberAddress> detail(String id) {
        if (id == null || id.isBlank()) {
            return BuildFailJson("主键不能为空");
        }
        var bean = this.memberAddressService.getById(id);
        return BuildSuccessJson(bean, "查询成功");
    }

}
