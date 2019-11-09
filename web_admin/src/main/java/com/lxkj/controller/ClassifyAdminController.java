package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Classify;
import com.lxkj.service.ClassifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @author Zhanqian
 * @date 2019/11/7 15:44
 * 商品分类Controller
 */
@Controller
@RequestMapping("/Classify")
@Slf4j
public class ClassifyAdminController extends BaseController {

    @Autowired
    private ClassifyService classifyService;

    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Classify/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Classify> pageList() {
        PageData params=this.getPageData();
        IPage<Classify> page=classifyService.page(new Page<Classify>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Classify>()
                        .eq("is_del", 0)  //没有被删除的
                        .orderByAsc("sort")
                        .orderByDesc("create_time"));
        DataGridModel<Classify> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("Classify",classifyService.getById(id));
        }else{
            model.addObject("Classify",new Classify());
        }
        model.setViewName("/admin/Classify/add");
        return model;
    }

    /**
     * 保存商品分类
     * @param bean
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(Classify bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            bean.getId();
            bean.update(new QueryWrapper<Classify>().eq("id", bean.getId()));
        }else{
            bean.setCreateTime(new Date());
            bean.setSort(classifyService.count(new QueryWrapper<Classify>().eq("is_del", 0))+1);
            bean.insert();
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
    public JsonResults update(Classify bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        classifyService.updateById(bean);
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
        classifyService.update(new UpdateWrapper<Classify>().set("is_del",1).eq("id",id));
        return BuildSuccessJson("删除成功");
    }

    /**
     * 详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id,ModelAndView model) {
        model.addObject("Classify",classifyService.getById(id));
        model.setViewName("/admin/Classify/detail");
        return model;
    }

    /**
     * 查询可用商品分类
     * @return
     */
    @RequestMapping("/selectClassifys")
    @ResponseBody
    @Transactional
    public JsonResults selectClassifys() {
        var list= classifyService.list(new QueryWrapper<Classify>().eq("is_del", 0).orderByAsc("sort").orderByDesc("create_time"));
        return BuildSuccessJson(list,"查询成功");
    }
}
