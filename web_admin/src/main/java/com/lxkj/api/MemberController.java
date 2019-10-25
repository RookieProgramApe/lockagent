package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.Member;
import com.lxkj.entity.MemberCredit;
import com.lxkj.service.ConfigService;
import com.lxkj.service.MemberCreditService;
import com.lxkj.service.MemberService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "会员", description = "查看我的信息")
@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MemberCreditService memberCreditService;

    @ApiOperation(value = "查看我的信息", notes = "包含内容：昵称、头像、是否是代理商、积分余额等")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true)
    })
    @PostMapping("/myInfo")
    @LoginRequired
    public JsonResults<Member> myInfo() {
        String memberId = this.getToken();
        var member = memberService.getById(memberId);
        return BuildSuccessJson(member, "查询成功");
    }

    @ApiOperation(value = "根据id获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "String", value = "用户id", required = true)
    })
    @PostMapping("/getMemberById")
    @LoginRequired
    public JsonResults<Member> getMemberById(@RequestParam String id) {
        var member = memberService.getById(id);
        return BuildSuccessJson(member, "查询成功");
    }



    @ApiOperation(value = "积分支出/收入列表", notes = "支出是-，收入是+")
    @PostMapping("/myCredit")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data=[{name:'姓名',phone:'电话',bought:'卡片数量',create_time:'加入时间'}...]"),
    })
    public JsonResults<List<MemberCredit>> myCredit(Long page, Long limit) {
        String memberId = this.getToken();
        var data = this.memberCreditService.page(new Page<MemberCredit>(page != null ? page : 1, limit != null ? limit : 10)
                , new QueryWrapper<MemberCredit>().eq("member_id", memberId).orderByDesc("create_time"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }


}
