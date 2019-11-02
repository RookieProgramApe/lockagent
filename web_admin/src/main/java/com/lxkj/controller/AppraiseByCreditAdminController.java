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
import com.lxkj.entity.*;
import com.lxkj.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 积分商品评论 前端控制器
 * </p>
 * 首页路由：/Appraise/list
 * @author zhanqian
 * @since 2019-10-28
 */
@Controller
@RequestMapping("/AppraiseByCredit")
@Slf4j
public class AppraiseByCreditAdminController extends BaseController {
    @Autowired
    private CargoService cargoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AppraiseService appraiseService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AppraiseAttachmentService appraiseAttachmentService;
    /**
     * 首页RetailerReward
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/AppraiseByCredit/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Appraise> pageList(@RequestParam("type") String type) {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<Appraise> page=appraiseService.page(new Page<Appraise>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Appraise>()
                        .eq("is_del",0)
                        .eq("status", 0)
                        .eq("`type`", StringUtils.isNotBlank(type)?type:1)
//                        .eq("type", 1)
                        .like(StringUtils.isNotBlank(keyword),"cargo_name", keyword)
                        .orderByDesc("is_top")
                        .orderByDesc("create_time"));
//        page.getRecords().stream().forEach(p->{
//            List<CargoAttachment> list =  cargoAttachmentService.list(Wrappers.<CargoAttachment>query().eq("cargo_id",p.getId()));
//            if(list!=null&&list.size()>0){
//                p.setAttachment(list);
//            }
//
//            p.setSaleNum(cargoService.getSaleNum(p.getId()));
//        });
        DataGridModel<Appraise> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

//    @RequestMapping("/pageList1")
//    @ResponseBody
//    public DataGridModel<Cargo> pageList1(String csid) {
//        PageData params = this.getPageData();
//        List<Cargo> page=cargoService.list(
//                new QueryWrapper<Cargo>()
//                        .eq("isdel",0)
//                        .eq("type", 1)
//                        .ne("id",csid)
//                        .orderByAsc("sort"));
//        DataGridModel<Cargo> grid=new DataGridModel(page,Long.valueOf(page.size()));
//        return  grid;
//    }
//
//    @RequestMapping("/pageListOrder")
//    @ResponseBody
//    public DataGridModel<Order> pageListOrder(String cargo_id) {
//        PageData params = this.getPageData();
//        List<Map<String ,Object>> list = jdbcTemplate.queryForList("select * from `order` where cargo_id=? order by create_time ",cargo_id);
////        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
////                new QueryWrapper<Order>()
////                        .select("count","sku_name","status","create_time")
////                        .eq("cargo_id",cargo_id)
////                        .orderByDesc("create_time"));
//        DataGridModel<Order> grid=new DataGridModel(list,Long.valueOf(list.size()));
//        return  grid;
//    }

//    /**
//     *
//     * @param retailer_id
//     * @return
//     */
//    @RequestMapping("/pageListReward")
//    @ResponseBody
//    public DataGridModel<Cargo> pageListReward(String retailer_id) {
//        PageData params = this.getPageData();
//        String keyword = params.getString("keyword");
//        IPage<Cargo> page=cargoService.page(new Page<Cargo>(params.getInteger("page"),params.getInteger("limit")),
//                new QueryWrapper<Cargo>()
//                        .eq("isdel",0)
//                        .eq("type", 1)
//                        .select("cargo.*"
//                                ,"(select figure from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as figure"
//                                ,"(select id from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as retailerRewardId "
//                        )
//                        .eq("status",1)
//                        .like(StringUtils.isNotBlank(keyword),"name",keyword)
//        );
//        page.getRecords().stream().forEach(p->{
//            List<CargoAttachment> list =  cargoAttachmentService.list(Wrappers.<CargoAttachment>query().eq("cargo_id",p.getId()));
//            if(list!=null&&list.size()>0){
//                p.setAttachment(list);
//            }
//            p.setSaleNum(cargoService.getSaleNum(p.getId()));
//        });
//        DataGridModel<Cargo> grid=new DataGridModel(page.getRecords(),page.getTotal());
//        return  grid;
//    }

    /**
     * 跳转添加/编辑界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id,ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("appraise", appraiseService.getById(id));
        }else{
            model.addObject("appraise",new Appraise());
        }
        model.setViewName("/admin/AppraiseByCredit/add");
        return model;
    }

    /**
     * 详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id,ModelAndView model) {
        if(StringUtils.isNotBlank(id)){
            Appraise appraise = appraiseService.getById(id);
            if(appraise != null){
                // 获取评价的评论人信息、订单信息、商品信息
                appraise.setMember(memberService.getById(appraise.getMemberId()));
                appraise.setOrder(orderService.getById(appraise.getOrderId()));
                appraise.setCargo(cargoService.getById(appraise.getCargoId()));

                // 查询图片
                appraise.setImgs(appraiseAttachmentService.list(new QueryWrapper<AppraiseAttachment>().eq("appraise_id", appraise.getId())));
            }
            model.addObject("appraise", appraise);
//        CargoAttachment image = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",1));
//        CargoAttachment video = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",2));
//        model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
//        model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");


        }else{

        }
        model.setViewName("/admin/AppraiseByCredit/detail");
        return model;
    }


    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(Appraise bean) {
        System.out.print(bean);
        // 获取评论人
        Member member = memberService.getById(bean.getMemberId());
        Cargo cargo = cargoService.getById(bean.getCargoId());
        bean.setMemberAvatar(member.getAvatar());
        bean.setMemberName(member.getNickname());
        bean.setCreateTime(new Date());
        bean.setCargoName(cargo.getName());
        //保存到数据库
        appraiseService.saveOrUpdate(bean);
        return BuildSuccessJson("提交成功");
    }

    /**
     * 修改
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(Appraise bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        appraiseService.updateById(bean);
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
        appraiseService.update(new UpdateWrapper<Appraise>().set("status",1).eq("id",id));
//        cargoService.removeById(id);
//        cargoAttachmentService.remove(Wrappers.<CargoAttachment>query().eq("cargo_id",id));
//        cargoSkuService.remove(Wrappers.<CargoSku>query().eq("cargo_id",id));
        return BuildSuccessJson("删除成功");
    }

//    /**
//     * 批量删除
//     * @param ids
//     * @return
//     */
//    @RequestMapping("/deleteForList")
//    @ResponseBody
//    @Transactional
//    public JsonResults deleteForList(@RequestParam(value = "ids[]") String[] ids) {
//        cargoSkuService.remove(Wrappers.<CargoSku>query().in("cargo_id",ids));
//        cargoAttachmentService.remove(Wrappers.<CargoAttachment>query().in("cargo_id",ids));
//        cargoService.removeByIds(Arrays.asList(ids));
//        return BuildSuccessJson("删除成功");
//    }

    /**
     * 查询可用用户
     * @return
     */
    @RequestMapping("/selectGoods")
    @ResponseBody
    @Transactional
    public JsonResults selectGoods() {
        var list= memberService.list(new QueryWrapper<Member>().orderByDesc("create_time"));
        return BuildSuccessJson(list,"查询成功");
    }
}
