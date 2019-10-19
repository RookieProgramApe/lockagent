package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Config;
import com.lxkj.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * <p>
 * 配置 前端控制器
 * </p>
 * 首页路由：/Config/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Config")
@Slf4j
public class ConfigController extends BaseController {
    @Autowired
    private ConfigService configService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Config/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Config> pageList() {
            PageData params=this.getPageData();
            IPage<Config> page=configService.page(new Page<Config>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Config>()
                    .orderByDesc("createTime"));
            DataGridModel<Config> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    /**
     * 关于我们首页
     * @param model
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(ModelAndView model) {
        model.addObject("about_contact",configService.getOne(Wrappers.<Config>query().eq("`key`","about_contact")));
        model.addObject("about_certificate",configService.getOne(Wrappers.<Config>query().eq("`key`","about_certificate")));
        model.addObject("about_culture",configService.getOne(Wrappers.<Config>query().eq("`key`","about_culture")));
        model.addObject("about_introduction",configService.getOne(Wrappers.<Config>query().eq("`key`","about_introduction")));
        model.setViewName("/admin/Config/add");
        return model;
    }

    /**
     * 积分设置首页
     * @param model
     * @return
     */
    @RequestMapping("/toAdd1")
    public ModelAndView toAdd1(ModelAndView model) {
        model.addObject("credit_reward_rate",configService.getOne(Wrappers.<Config>query().eq("`key`","credit_reward_rate")));
        model.addObject("credit_discount_rate",configService.getOne(Wrappers.<Config>query().eq("`key`","credit_discount_rate")));
        model.setViewName("/admin/Config/add1");
        return model;
    }

    /**
     * 会员人数虚拟值
     * @param model
     * @return
     */
    @RequestMapping("/toAdd2")
    public ModelAndView toAdd2(ModelAndView model) {
        model.addObject("base_member_count",configService.getOne(Wrappers.<Config>query().eq("`key`","base_member_count")));
        model.setViewName("/admin/Config/add2");
        return model;
    }

    /**
     * 提现
     * @param model
     * @return
     */
    @RequestMapping("/toAdd3")
    public ModelAndView toAdd3(ModelAndView model) {
        model.addObject("withdraw_note",configService.getOne(Wrappers.<Config>query().eq("`key`","withdraw_note")));
        model.addObject("withdraw_min",configService.getOne(Wrappers.<Config>query().eq("`key`","withdraw_min")));
        model.addObject("withdraw_day",configService.getOne(Wrappers.<Config>query().eq("`key`","withdraw_day")));

        model.addObject("withdraw_commission",configService.getOne(Wrappers.<Config>query().eq("`key`","withdraw_commission")));
        model.addObject("withdraw_commissionMin",configService.getOne(Wrappers.<Config>query().eq("`key`","withdraw_commissionMin")));
        model.setViewName("/admin/Config/add3");
        return model;
    }

    /**
     * 卡片
     * @param model
     * @return
     */
    @RequestMapping("/toAdd4")
    public ModelAndView toAdd4(ModelAndView model) {
        model.addObject("card_price",configService.getOne(Wrappers.<Config>query().eq("`key`","card_price")));
        model.addObject("card_note",configService.getOne(Wrappers.<Config>query().eq("`key`","card_note")));
        model.setViewName("/admin/Config/add4");
        return model;
    }
    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save() {
        HttpServletRequest request =  this.getRequest();
        Enumeration<String> en =  request.getParameterNames();
        String param = "";
        String value = "";
        while (en.hasMoreElements()){
            param = en.nextElement();
            if(param.startsWith("about_")||param.startsWith("credit_")||param.startsWith("base_member_count")||param.startsWith("withdraw_")||param.startsWith("card_")){
                value = request.getParameter(param);
                String[] arr = param.split("@");
                if(arr.length==2){
                    configService.updateById(new Config().setId(arr[1]).setValue(value));
                }else{
                    return BuildFailJson("提交失败");
                }
            }
        }
        return BuildSuccessJson("提交成功");
     }

    /**
    * 修改
    * @return
    */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(Config bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            configService.updateById(bean);
        return BuildSuccessJson("修改成功");
    }

    /**
    * 单个删除
    * @param
    * @return
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResults delete(String id) {
        configService.removeById(id);
        return BuildSuccessJson("删除成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/deleteForList")
    @ResponseBody
    @Transactional
    public JsonResults deleteForList(@RequestParam(value = "ids[]") String[] ids) {
        configService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
