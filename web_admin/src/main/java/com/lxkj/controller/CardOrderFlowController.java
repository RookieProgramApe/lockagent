package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.entity.CardOrderFlowFile;
import com.lxkj.entity.SysUser;
import com.lxkj.service.CardOrderFlowFileService;
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
import com.lxkj.entity.CardOrderFlow;
import com.lxkj.service.CardOrderFlowService;
import org.springframework.stereotype.Controller;
import com.lxkj.common.bean.BaseController;

import java.util.Arrays;

/**
 * <p>
 * 卡片订单审批流程 前端控制器
 * </p>
 * 首页路由：/CardOrderFlow/list
 *
 * @author 一个烧包
 * @since 2019-07-31
 */
@Controller
@RequestMapping("/CardOrderFlow")
@Slf4j
public class CardOrderFlowController extends BaseController {
    @Autowired
    private CardOrderFlowService cardOrderFlowService;
    @Autowired
    private CardOrderFlowFileService cardOrderFlowFileService;

    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/CardOrderFlow/list");
        return model;
    }

    /**
     * 分页列表
     *
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CardOrderFlow> pageList(String cardOrderId, String lb) {
        PageData params = this.getPageData();
        IPage<CardOrderFlow> page = cardOrderFlowService.page(new Page<CardOrderFlow>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<CardOrderFlow>()
                        .eq(StringUtils.isNotBlank(lb), "lb", lb)
                        .eq("card_order_id", cardOrderId)
                        .orderByAsc("create_time"));
        page.getRecords().stream().forEach(cardOrderFlow -> {
            var user = new SysUser().selectById(cardOrderFlow.getCreateBy());
            cardOrderFlow.setRealName(user.getRealName() + "(" + user.getUserName() + ")");
            cardOrderFlow.setMobile(user.getMobile());
            //cardOrderFlow.setFileList(cardOrderFlowFileService.list(new QueryWrapper<CardOrderFlowFile>().eq("card_order_flow_id",cardOrderFlow.getId())));
        });
        DataGridModel<CardOrderFlow> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 跳转添加/编辑界面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id, ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("CardOrderFlow", cardOrderFlowService.getById(id));
        } else {
            model.addObject("CardOrderFlow", new CardOrderFlow());
        }
        model.setViewName("/admin/CardOrderFlow/add");
        return model;
    }

    /**
     * 保存
     *
     * @param
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(CardOrderFlow bean) {
        if (StringUtils.isNotBlank(bean.getId())) {
            cardOrderFlowService.updateById(bean);
        } else {
            cardOrderFlowService.save(bean);
        }
        return BuildSuccessJson("提交成功");
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(CardOrderFlow bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        cardOrderFlowService.updateById(bean);
        return BuildSuccessJson("修改成功");
    }

    /**
     * 单个删除
     *
     * @param
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResults delete(String id) {
        cardOrderFlowService.removeById(id);
        return BuildSuccessJson("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteForList")
    @ResponseBody
    @Transactional
    public JsonResults deleteForList(@RequestParam(value = "ids[]") String[] ids) {
        cardOrderFlowService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }
}
