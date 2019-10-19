package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Retailer;
import com.lxkj.entity.RetailerReward;
import com.lxkj.service.RetailerRewardService;
import com.lxkj.service.RetailerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

/**
 * <p>
 * 代理商商品提货奖励设置 前端控制器
 * </p>
 * 首页路由：/RetailerReward/list
 * @author 一个烧包
 * @since 2019-07-19
 */
@Controller
@RequestMapping("/RetailerReward")
@Slf4j
public class RetailerRewardController extends BaseController {
    @Autowired
    private RetailerRewardService retailerRewardService;
    @Autowired
    private RetailerService retailerService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/RetailerReward/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<RetailerReward> pageList() {
            PageData params=this.getPageData();
            IPage<RetailerReward> page=retailerRewardService.page(new Page<RetailerReward>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<RetailerReward>()
                    .orderByDesc("createTime"));
            DataGridModel<RetailerReward> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    /**
    * 跳转添加/编辑界面
    * @param id
    * @param model
    * @return
    */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id,ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("RetailerReward",retailerRewardService.getById(id));
        }else{
            model.addObject("RetailerReward",new RetailerReward());
        }
        model.setViewName("/admin/RetailerReward/add");
        return model;
    }

    @RequestMapping("/setCargoReward")
    public ModelAndView setCargoReward(String id,String cargo_id,String retailer_id,ModelAndView model) {
        RetailerReward rr =retailerRewardService.getById(id);
        model.addObject("id",id);
        model.addObject("retailer_id",retailer_id);
        model.addObject("cargo_id",cargo_id);
        if(rr!=null&&rr.getFigure()!=null){
            model.addObject("figure",rr.getFigure());
        }else{
            model.addObject("figure",0.00);
        }
        model.setViewName("/admin/Retailer/setCargoReward");
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
    public JsonResults save(RetailerReward bean) {
        System.out.println(bean.getId());
        System.out.println(StringUtils.isNotBlank(bean.getId()));
        Retailer r = retailerService.getById(bean.getRetailerId());
        if(r!=null&&StringUtils.isNotBlank(r.getMemberId())){
            bean.setMemberId(r.getMemberId());
        }
        if(StringUtils.isNotBlank(bean.getId())&&!("null").equals(bean.getId())){
            retailerRewardService.updateById(bean);
        }else{
            bean.setId("");
            retailerRewardService.save(bean);
        }
        return BuildSuccessJson("设置成功");
     }

    /**
    * 修改
    * @return
    */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(RetailerReward bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            retailerRewardService.updateById(bean);
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
        retailerRewardService.removeById(id);
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
        retailerRewardService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
