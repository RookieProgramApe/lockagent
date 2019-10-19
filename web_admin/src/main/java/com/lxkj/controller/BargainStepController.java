package com.lxkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.BargainStep;
import com.lxkj.service.BargainStepService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 砍价过程配置 前端控制器
 * </p>
 * 首页路由：/BargainStep/list
 * @author 一个烧包
 * @since 2019-08-02
 */
@Controller
@RequestMapping("/BargainStep")
@Slf4j
public class BargainStepController extends BaseController {
    @Autowired
    private BargainStepService bargainStepService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/BargainStep/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<BargainStep> pageList() {
            PageData params=this.getPageData();
            IPage<BargainStep> page=bargainStepService.page(new Page<BargainStep>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<BargainStep>()
                    .orderByDesc("createTime"));
            DataGridModel<BargainStep> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    @RequestMapping("/select")
    @ResponseBody
    @Transactional
    public JsonResults select(String bargain_id) {
        List<BargainStep> list=new ArrayList<>();
        if(StringUtils.isNotBlank(bargain_id)){
            list=bargainStepService.list(new QueryWrapper<BargainStep>().eq("bargain_id",bargain_id).orderByAsc("sort"));
        }
        return BuildSuccessJson(list,"查询成功");
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
            model.addObject("BargainStep",bargainStepService.getById(id));
        }else{
            model.addObject("BargainStep",new BargainStep());
        }
        model.setViewName("/admin/BargainStep/add");
        return model;
    }

    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/save1")
    @ResponseBody
    @Transactional
    public JsonResults save(BargainStep bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            bargainStepService.updateById(bean);
        }else{
            bargainStepService.save(bean);
        }
        return BuildSuccessJson("提交成功");
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
    public JsonResults save(String id,String stepArr,String delIds) {
        if (StringUtils.isBlank(id)) {
           throw new RuntimeException("砍价订单主键不能为空");
        }
        //---------------问卷题选择项-添加/新增-------------------
        if(StringUtils.isNotBlank(stepArr)){
            List<BargainStep> list=new ArrayList<BargainStep>();
            JSONArray jsonArray = JSON.parseArray(stepArr);
            for (int i = 0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BargainStep addBean=new BargainStep();
                addBean.setBargainId(id);
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setPrice(jsonObject.getBigDecimal("content"));
                addBean.setSort(jsonObject.getInteger("sort"));
                addBean.setCreateTime(new Date());
                list.add(addBean);
            }
            if(!list.isEmpty()){
                bargainStepService.saveOrUpdateBatch(list);
            }
        }
        //------------问卷删除-----------
        if(StringUtils.isNotBlank(delIds)){
            bargainStepService.removeByIds(Arrays.asList(delIds.split(",")));
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
    public JsonResults update(BargainStep bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            bargainStepService.updateById(bean);
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
        bargainStepService.removeById(id);
        return BuildSuccessJson("删除成功");
    }


}
