package com.lxkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Bargain;
import com.lxkj.entity.BargainStep;
import com.lxkj.entity.Cargo;
import com.lxkj.entity.CargoAttachment;
import com.lxkj.mapper.BargainMapper;
import com.lxkj.service.BargainService;
import com.lxkj.service.BargainStepService;
import com.lxkj.service.CargoAttachmentService;
import com.lxkj.service.CargoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 砍价活动 前端控制器
 * </p>
 * 首页路由：/Bargain/list
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Controller
@RequestMapping("/Bargain")
@Slf4j
public class BargainController extends BaseController {
    @Autowired
    private BargainService bargainService;
    @Resource
    private BargainMapper brgainMapper;
    @Autowired
    private CargoService cargoService;
    @Autowired
    private BargainStepService bargainStepService;
    @Autowired
    private CargoAttachmentService  cargoAttachmentService;
    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Bargain/list");
        return model;
    }
    @RequestMapping("/list1")
    public ModelAndView list1(ModelAndView model) {
        model.setViewName("/admin/Bargain/list1");
        return model;
    }

    /**
     * 分页列表
     *
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Bargain> pageList() {
        PageData params = this.getPageData();
        IPage<Bargain> page = bargainService.page(new Page<Bargain>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Bargain>()
                        .eq("isdel",0)
                        .orderByAsc("sort"));
        page.getRecords().forEach(p -> {
            Cargo bean=cargoService.getById(p.getCargoId());
            cargoService.getData(bean);
            p.setCargo(bean);
        });
        DataGridModel<Bargain> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Bargain> pageList1() {
        PageData params=this.getPageData();
        String keyword = params.getString("keyword");
        IPage<Bargain> page=bargainService.page(new Page<Bargain>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Bargain>()
                        .eq("isdel",0)
                        .like(StringUtils.isNotBlank(keyword),"share_content",keyword)
                        .orderByAsc("sort")
                        .orderByDesc("create_time"));
        DataGridModel<Bargain> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
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
            model.addObject("Bargain", bargainService.getById(id));
        } else {
            model.addObject("Bargain", new Bargain());
        }
        model.setViewName("/admin/Bargain/add");
        return model;
    }

    @RequestMapping("/toSet")
    public ModelAndView toSet(String id, ModelAndView model) {
        model.addObject("id",id);
        model.setViewName("/admin/Bargain/set");
        return model;
    }

    /**
     * 砍价活动详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id,ModelAndView model) {
        model.addObject("Bargain",bargainService.getById(id));
        model.setViewName("/admin/Bargain/detail");
        return model;
    }

    /**
     * 砍价活动详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail1")
    public ModelAndView detail1(String id,ModelAndView model) {
        Bargain b=bargainService.getById(id);
        model.addObject("Bargain",b);
        if(StringUtils.isNotBlank(b.getCargoId())){
            model.addObject("Cargo",cargoService.getById(b.getCargoId()));
            CargoAttachment image = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",b.getCargoId()).eq("type",1));
            CargoAttachment video = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",b.getCargoId()).eq("type",2));
            model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
            model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");
        }else{
            model.addObject("Cargo",null);
        }
        model.setViewName("/admin/Bargain/detail1");
        return model;
    }

    @RequestMapping("/detailFlow")
    public ModelAndView detailFlow(String id,ModelAndView model) {
        model.addObject("id",id);
        model.setViewName("/admin/Bargain/detailFlow");
        return model;
    }


    /**
     * 订单列表
     * @param model
     * @return
     */
    @RequestMapping("/orderList")
    public ModelAndView orderList(ModelAndView model) {
        model.setViewName("/admin/Bargain/orderList");
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
    public JsonResults save(Bargain bean,String stepArr,String delIds) {
        if(StringUtils.isBlank(bean.getSharePic())){
            return BuildFailJson("请添加一个分享图片");
        }
        if (StringUtils.isNotBlank(bean.getId())) {
            bargainService.updateById(bean);
        } else {
            bean.setSort(bargainService.count()+1);
            bargainService.save(bean);
        }
        //---------------配置-添加/新增-------------------
        if(StringUtils.isNotBlank(stepArr)){
            List<BargainStep> list=new ArrayList<BargainStep>();
            JSONArray jsonArray = JSON.parseArray(stepArr);
            for (int i = 0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BargainStep addBean=new BargainStep();
                addBean.setBargainId(bean.getId());
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setPrice(jsonObject.getBigDecimal("content"));
                addBean.setSort(jsonObject.getInteger("sort"));
                list.add(addBean);
            }
            if(!list.isEmpty()){
                bargainStepService.saveOrUpdateBatch(list);
            }
        }
        //------------配置删除-----------
        if(StringUtils.isNotBlank(delIds)){
            Arrays.asList(delIds.split(",")).stream().forEach(p->{
                bargainStepService.removeById(p);
            });
        }
        return BuildSuccessJson("提交成功");
    }

    @RequestMapping("/select")
    @ResponseBody
    @Transactional
    public JsonResults select(String bargainId) {
        List<BargainStep> list=new ArrayList<>();
        if(StringUtils.isNotBlank(bargainId)){
            list=bargainStepService.list(new QueryWrapper<BargainStep>().eq("bargain_id",bargainId).orderByAsc("sort"));
        }
        return BuildSuccessJson(list,"查询成功");
    }
    /**
     * 修改
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(Bargain bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        bargainService.updateById(bean);
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
        bargainService.update(new UpdateWrapper<Bargain>().set("isdel",1).eq("id",id));
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
        bargainService.update(new UpdateWrapper<Bargain>().set("isdel",1).in("id",ids));
        return BuildSuccessJson("删除成功");
    }
    @RequestMapping("/selectGoods")
    @ResponseBody
    @Transactional
    public JsonResults selectGoods() {
       var list= cargoService.list(new QueryWrapper<Cargo>().eq("isdel",0).orderByAsc("sort"));
        return BuildSuccessJson(list,"查询成功");
    }
}
