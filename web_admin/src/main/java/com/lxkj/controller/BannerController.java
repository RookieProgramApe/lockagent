package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Banner;
import com.lxkj.service.BannerService;
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
import java.util.Date;

/**
 * <p>
 * 轮播图 前端控制器
 * </p>
 * 首页路由：/Banner/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Banner")
@Slf4j
public class BannerController extends BaseController {
    @Autowired
    private BannerService bannerService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Banner/list");
        return model;
    }

    /**
     * 积分商城轮播图首页
     */
    @RequestMapping("/list1")
    public ModelAndView list2(ModelAndView model) {
        model.setViewName("/admin/Banner/list1");
        return model;
    }

    /**
     * 商城首页轮播图分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Banner> pageList() {
            PageData params=this.getPageData();
            IPage<Banner> page=bannerService.page(new Page<Banner>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Banner>()
                    .eq("`type`", 1)
                    .orderByAsc("sort")
                    .orderByDesc("created_date")
            );
            DataGridModel<Banner> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    /**
     * 积分商城首页轮播图分页列表
     * @return
     */
    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Banner> pageList1() {
        PageData params=this.getPageData();
        IPage<Banner> page=bannerService.page(new Page<Banner>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Banner>()
                        .eq("`type`", 2)
                        .orderByAsc("sort")
                        .orderByDesc("created_date")
        );
        DataGridModel<Banner> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("Banner",bannerService.getById(id));
        }else{
            model.addObject("Banner",new Banner());
        }
        model.setViewName("/admin/Banner/add");
        return model;
    }

    /**
     * 跳转添加/编辑界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toAdd1")
    public ModelAndView toAdd1(String id,ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("Banner",bannerService.getById(id));
        }else{
            model.addObject("Banner",new Banner());
        }
        model.setViewName("/admin/Banner/add1");
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
    public JsonResults save(Banner bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            bannerService.updateById(bean);
        }else{
            bean.setSort(bannerService.count()+1);
            bean.setCreatedBy(ShiroUtils.getUserId());
            bean.setCreatedDate(new Date());
            bannerService.save(bean);
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
    public JsonResults update(Banner bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            bannerService.updateById(bean);
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
        bannerService.removeById(id);
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
        bannerService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
