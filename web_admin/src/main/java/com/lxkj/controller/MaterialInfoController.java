package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.MaterialInfo;
import com.lxkj.service.MaterialInfoService;
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
 * 素材内容 前端控制器
 * </p>
 * 首页路由：/MaterialInfo/list
 * @author 一个烧包
 * @since 2019-10-06
 */
@Controller
@RequestMapping("/MaterialInfo")
@Slf4j
public class MaterialInfoController extends BaseController {
    @Autowired
    private MaterialInfoService materialInfoService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/MaterialInfo/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<MaterialInfo> pageList() {
            PageData params=this.getPageData();
            IPage<MaterialInfo> page=materialInfoService.page(new Page<MaterialInfo>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<MaterialInfo>()
                    .orderByDesc("createTime"));
            DataGridModel<MaterialInfo> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("MaterialInfo",materialInfoService.getById(id));
        }else{
            model.addObject("MaterialInfo",new MaterialInfo());
        }
        model.setViewName("/admin/MaterialInfo/add");
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
    public JsonResults save(MaterialInfo bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            materialInfoService.updateById(bean);
        }else{
            materialInfoService.save(bean);
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
    public JsonResults update(MaterialInfo bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            materialInfoService.updateById(bean);
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
        materialInfoService.removeById(id);
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
        materialInfoService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
