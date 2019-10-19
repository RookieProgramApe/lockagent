package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.BargainOrderFlow;
import com.lxkj.entity.Member;
import com.lxkj.service.BargainOrderFlowService;
import com.lxkj.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 砍价订单完成情况 前端控制器
 * </p>
 * 首页路由：/BargainOrderFlow/list
 * @author 一个烧包
 * @since 2019-08-02
 */
@Controller
@RequestMapping("/BargainOrderFlow")
@Slf4j
public class BargainOrderFlowController extends BaseController {
    @Autowired
    private BargainOrderFlowService bargainOrderFlowService;
    @Autowired
    private MemberService memberService;
    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/BargainOrderFlow/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<BargainOrderFlow> pageList() {
            PageData params=this.getPageData();
            IPage<BargainOrderFlow> page=bargainOrderFlowService.page(new Page<BargainOrderFlow>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<BargainOrderFlow>()
                    .orderByDesc("createTime"));
            DataGridModel<BargainOrderFlow> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<BargainOrderFlow> pageList1(String bargain_order_id) {
        PageData params=this.getPageData();
        IPage<BargainOrderFlow> page=bargainOrderFlowService.page(new Page<BargainOrderFlow>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<BargainOrderFlow>()
                        .eq("bargain_order_id",bargain_order_id)
                        .orderByAsc("sort"));
        page.getRecords().stream().forEach(p->{
            if(StringUtils.isNotBlank(p.getEndMemberId())){
                Member m = memberService.getById(p.getEndMemberId());
                if(m!=null){
                    p.setAvatar(m.getAvatar());
                    p.setNickname(m.getNickname());
                }
            }
        });
        DataGridModel<BargainOrderFlow> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("BargainOrderFlow",bargainOrderFlowService.getById(id));
        }else{
            model.addObject("BargainOrderFlow",new BargainOrderFlow());
        }
        model.setViewName("/admin/BargainOrderFlow/add");
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
    public JsonResults save(BargainOrderFlow bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            bargainOrderFlowService.updateById(bean);
        }else{
            bargainOrderFlowService.save(bean);
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
    public JsonResults update(BargainOrderFlow bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            bargainOrderFlowService.updateById(bean);
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
        bargainOrderFlowService.removeById(id);
        return BuildSuccessJson("删除成功");
    }


}
