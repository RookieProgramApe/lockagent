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
import com.lxkj.entity.CardOrderFlowFile;
import com.lxkj.service.CardOrderFlowFileService;
import org.springframework.stereotype.Controller;
import com.lxkj.common.bean.BaseController;

import java.util.Arrays;

/**
 * <p>
 * 卡片订单流程图片 前端控制器
 * </p>
 * 首页路由：/CardOrderFlowFile/list
 * @author 一个烧包
 * @since 2019-07-31
 */
@Controller
@RequestMapping("/CardOrderFlowFile")
@Slf4j
public class CardOrderFlowFileController extends BaseController {
    @Autowired
    private CardOrderFlowFileService cardOrderFlowFileService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/CardOrderFlowFile/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CardOrderFlowFile> pageList() {
            PageData params=this.getPageData();
            IPage<CardOrderFlowFile> page=cardOrderFlowFileService.page(new Page<CardOrderFlowFile>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<CardOrderFlowFile>()
                    .orderByDesc("createTime"));
            DataGridModel<CardOrderFlowFile> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("CardOrderFlowFile",cardOrderFlowFileService.getById(id));
        }else{
            model.addObject("CardOrderFlowFile",new CardOrderFlowFile());
        }
        model.setViewName("/admin/CardOrderFlowFile/add");
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
    public JsonResults save(CardOrderFlowFile bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            cardOrderFlowFileService.updateById(bean);
        }else{
            cardOrderFlowFileService.save(bean);
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
    public JsonResults update(CardOrderFlowFile bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            cardOrderFlowFileService.updateById(bean);
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
        cardOrderFlowFileService.removeById(id);
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
        cardOrderFlowFileService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
