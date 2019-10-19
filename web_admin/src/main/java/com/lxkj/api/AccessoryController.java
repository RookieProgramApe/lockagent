package com.lxkj.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.FileMappingProperties;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.DateUtil;
import com.lxkj.common.util.billUtil;
import com.lxkj.entity.Banner;
import com.lxkj.entity.Bulletin;
import com.lxkj.entity.Counter;
import com.lxkj.entity.Retailer;
import com.lxkj.facade.SMSService;
import com.lxkj.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "基础接口", description = "滚动公告、轮播图、关于我们、获取验证码、获取加入代理商宣传图、查询我的授权书")
@Slf4j
@RestController
@RequestMapping("/api")
public class AccessoryController extends BaseController {

    @Autowired
    private BulletinService bulletinService;
    @Autowired
    private FileMappingProperties fileUtil;
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CounterService counterService;
    @Autowired
    private SMSService smsService;
    @Value("${picture.propurl}")
    private String propurl;
    @Value("${picture.authurl}")
    private String authurl;
    @Value("${picture.zhanghurl}")
    private String zhanghurl;


    @ApiOperation("计数器")
    @PostMapping("/count")
    public JsonResults count() {
        int i= counterService.count()+1;
        new Counter().setCreateTime(new Date()).insert();
        return BuildSuccessJson(i, "查询成功");
    }



    @ApiOperation("查询滚动公告")
    @PostMapping("/bulletin")
    public JsonResults<List<Bulletin>> queryBulletin() {
        List<Bulletin> data = this.bulletinService.list(Wrappers.<Bulletin>query()
                .eq("enabled", 1)
                .orderByAsc("sort"));
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation("查询轮播图")
    @PostMapping("/banner")
    public JsonResults<List<Banner>> queryBanner() {
        List<Banner> data = this.bannerService.list(Wrappers.<Banner>query()
                .eq("enabled", 1)
                .orderByAsc("sort"));
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation("查询平台客服电话")
    @PostMapping("/contact")
    public JsonResults<String> queryAboutContact() {
        String data = this.configService.queryForString("about_contact");
        return BuildSuccessJson(data, "查询成功");
    }

    @ApiOperation("关于我们")
    @PostMapping("/about")
    @ApiResponses({
            @ApiResponse(code = 200, message = "{data:{certificate:'荣誉证书-富文本',culture:'企业文化-富文本',introduction:'公司介绍-富文本'}}"),
    })
    public JsonResults about() {
        String certificate = this.configService.queryForString("about_certificate");
        String culture = this.configService.queryForString("about_culture");
        String introduction = this.configService.queryForString("about_introduction");
        return BuildSuccessJson(Map.of("certificate", certificate, "culture", culture, "introduction", introduction), "查询成功");
    }


    @ApiOperation("获取手机验证码")
    @PostMapping("/sendSms")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", dataType = "String", value = "手机号", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{data:真实验证码(需要客户端做正确校验)}"),
    })
    public JsonResults<String> sendSms(@RequestParam String mobile) {
        if (invalidMobile(mobile)) {
            return BuildFailJson("手机号码不正确");
        }
        String data = this.smsService.sendRandomDigit(mobile);
        return BuildSuccessJson(data, "操作成功");
    }


    @ApiOperation("获取代理商加入宣传图片")
    @PostMapping("/getBill")
    public JsonResults<String> getBill() {
        return BuildSuccessJson(propurl, "查询成功");
    }

    @ApiOperation("查询我的授权书")
    @PostMapping("/getAuth")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户token值", required = true),
    })
    @LoginRequired
    public JsonResults<String> getAuth() {
        String memberId = this.getToken();
        var user = memberService.getById(memberId);
        Retailer retailer = this.retailerService.getOne(Wrappers.<Retailer>query().eq("member_id", memberId));
        String auth = retailer.getAuthurl();
        if (StringUtils.isBlank(auth)) {
            try {
                String a = DateUtil.DateToString(retailer.getCreateTime(), "yyyy年MM月dd日");
                String b = DateUtil.DateToString(DateUtil.addYear(retailer.getCreateTime(), 1), "yyyy年MM月dd日");
                auth = billUtil.generateImg2(authurl, retailer.getName(), a+"至"+b, fileUtil.getPath(), fileUtil.getMapping(),zhanghurl);
            } catch (Exception e) {
                e.printStackTrace();
                return BuildFailJson("生成失败");
            }
            if (StringUtils.isNotBlank(auth)) {
                this.retailerService.update(Wrappers.<Retailer>update().set("authurl", auth).eq("id", retailer.getId()));
            } else {
                return BuildFailJson("生成失败");
            }
        }
        return BuildSuccessJson(auth, "查询成功");
    }
}
