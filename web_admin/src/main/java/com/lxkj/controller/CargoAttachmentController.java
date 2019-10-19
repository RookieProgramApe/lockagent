package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.lxkj.common.bean.DataGridModel;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.lxkj.entity.CargoAttachment;
import com.lxkj.service.CargoAttachmentService;
import org.springframework.stereotype.Controller;
import com.lxkj.common.bean.BaseController;

import java.util.Arrays;

/**
 * <p>
 * 商品附件 前端控制器
 * </p>
 * 首页路由：/CargoAttachment/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/CargoAttachment")
@Slf4j
public class CargoAttachmentController extends BaseController {
    @Autowired
    private CargoAttachmentService cargoAttachmentService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/CargoAttachment/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CargoAttachment> pageList() {
            PageData params=this.getPageData();
            IPage<CargoAttachment> page=cargoAttachmentService.page(new Page<CargoAttachment>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<CargoAttachment>()
                    .orderByDesc("createTime"));
            DataGridModel<CargoAttachment> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("CargoAttachment",cargoAttachmentService.getById(id));
        }else{
            model.addObject("CargoAttachment",new CargoAttachment());
        }
        model.setViewName("/admin/CargoAttachment/add");
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
    public JsonResults save(CargoAttachment bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            cargoAttachmentService.updateById(bean);
        }else{
            cargoAttachmentService.save(bean);
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
    public JsonResults update(CargoAttachment bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            cargoAttachmentService.updateById(bean);
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
        cargoAttachmentService.removeById(id);
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
        cargoAttachmentService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
