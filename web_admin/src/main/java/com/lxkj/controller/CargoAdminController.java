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
 * 商品 前端控制器
 * </p>
 * 首页路由：/Cargo/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Cargo")
@Slf4j
public class CargoAdminController extends BaseController {
    @Autowired
    private CargoService cargoService;
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    @Autowired
    private CargoSkuService cargoSkuService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RetailerRewardService retailerRewardService;
    @Autowired
    private CargoCategoryService cargoCategoryService;
    /**
     * 首页RetailerReward
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Cargo/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Cargo> pageList() {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<Cargo> page=cargoService.page(new Page<Cargo>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Cargo>()
                        .eq("isdel",0)
                        .eq("type", 1)
                        .like(StringUtils.isNotBlank(keyword),"name",keyword)
                        .orderByAsc("sort"));
        page.getRecords().stream().forEach(p->{
            List<CargoAttachment> list =  cargoAttachmentService.list(Wrappers.<CargoAttachment>query().eq("cargo_id",p.getId()));
            if(list!=null&&list.size()>0){
                p.setAttachment(list);
            }

            p.setSaleNum(cargoService.getSaleNum(p.getId()));
        });
        DataGridModel<Cargo> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Cargo> pageList1(String csid) {
        PageData params = this.getPageData();
        List<Cargo> page=cargoService.list(
                new QueryWrapper<Cargo>()
                        .eq("isdel",0)
                        .eq("type", 1)
                        .ne("id",csid)
                        .orderByAsc("sort"));
        DataGridModel<Cargo> grid=new DataGridModel(page,Long.valueOf(page.size()));
        return  grid;
    }

    @RequestMapping("/pageListOrder")
    @ResponseBody
    public DataGridModel<Order> pageListOrder(String cargo_id) {
        PageData params = this.getPageData();
        List<Map<String ,Object>> list = jdbcTemplate.queryForList("select * from `order` where cargo_id=? order by create_time ",cargo_id);
//        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
//                new QueryWrapper<Order>()
//                        .select("count","sku_name","status","create_time")
//                        .eq("cargo_id",cargo_id)
//                        .orderByDesc("create_time"));
        DataGridModel<Order> grid=new DataGridModel(list,Long.valueOf(list.size()));
        return  grid;
    }

    /**
     *
     * @param retailer_id
     * @return
     */
    @RequestMapping("/pageListReward")
    @ResponseBody
    public DataGridModel<Cargo> pageListReward(String retailer_id) {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<Cargo> page=cargoService.page(new Page<Cargo>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Cargo>()
                        .eq("isdel",0)
                        .eq("type", 1)
                        .select("cargo.*"
                                ,"(select figure from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as figure"
                                ,"(select id from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as retailerRewardId "
                        )
                        .eq("status",1)
                        .like(StringUtils.isNotBlank(keyword),"name",keyword)
        );
        page.getRecords().stream().forEach(p->{
            List<CargoAttachment> list =  cargoAttachmentService.list(Wrappers.<CargoAttachment>query().eq("cargo_id",p.getId()));
            if(list!=null&&list.size()>0){
                p.setAttachment(list);
            }
            p.setSaleNum(cargoService.getSaleNum(p.getId()));
        });
        DataGridModel<Cargo> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("Cargo",cargoService.getById(id));
            CargoAttachment image = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",1));
            CargoAttachment video = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",2));
            model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
            model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");
        }else{
            model.addObject("Cargo",new Cargo());
            model.addObject("image","");
            model.addObject("video","");
        }
        model.setViewName("/admin/Cargo/add");
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
        model.addObject("Cargo",cargoService.getById(id));
        CargoAttachment image = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",1));
        CargoAttachment video = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",id).eq("type",2));
        model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
        model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");
        model.setViewName("/admin/Cargo/detail");
        return model;
    }


    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(Cargo bean,String url1,String url2,String stepArr,String delIds, String cateArr, String delIds1) {
        String id = "";
        if(StringUtils.isNotBlank(bean.getId())){
            id = bean.getId();
            cargoService.updateById(bean);
            cargoAttachmentService.remove(Wrappers.<CargoAttachment>query().eq("cargo_id",bean.getId()));
        }else{
            bean.setCreateTime(new Date());
            bean.setSort(cargoService.count()+1);
            cargoService.save(bean);
            id = bean.getId();
        }
        if(StringUtils.isNotBlank(url1)){
            cargoAttachmentService.save(new CargoAttachment().setCargoId(bean.getId()).setType(1).setUrl(url1));
        }
        if(StringUtils.isNotBlank(url2)){
            cargoAttachmentService.save(new CargoAttachment().setCargoId(bean.getId()).setType(2).setUrl(url2));
        }
        //---------------商品规格-添加/新增-------------------
        if(StringUtils.isNotBlank(stepArr)){
            List<CargoSku> list=new ArrayList<CargoSku>();
            JSONArray jsonArray = JSON.parseArray(stepArr);
            for (int i = 0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CargoSku addBean=new CargoSku();
                addBean.setCargoId(bean.getId());
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setName(jsonObject.containsKey("content")?jsonObject.getString("content"):"");
                if(StringUtils.isBlank(addBean.getId())){
                    addBean.setCreateTime(new Date());
                }
                list.add(addBean);
            }
            if(!list.isEmpty()){
                cargoSkuService.saveOrUpdateBatch(list);
            }
        }
        //------------商品规格删除-----------
        if(StringUtils.isNotBlank(delIds)){
            Arrays.asList(delIds.split(",")).stream().forEach(p->{
                cargoSkuService.removeById(p);
            });
        }

        //---------------商品套餐-添加/新增-------------------
        if(StringUtils.isNotBlank(cateArr)){
            List<CargoCategory> list=new ArrayList<CargoCategory>();
            JSONArray jsonArray = JSON.parseArray(cateArr);
            for (int i = 0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CargoCategory addBean=new CargoCategory();
                addBean.setCargoId(bean.getId());
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setName(jsonObject.containsKey("content")?jsonObject.getString("content"):"");
                addBean.setPrice(jsonObject.containsKey("content1")?new BigDecimal((String) jsonObject.getString("content1")):new BigDecimal(""));
                if(StringUtils.isBlank(addBean.getId())){
                    addBean.setCreateTime(new Date());
                }
                list.add(addBean);
            }
            if(!list.isEmpty()){
                cargoCategoryService.saveOrUpdateBatch(list);
            }
        }
        //------------商品套餐删除-----------
        if(StringUtils.isNotBlank(delIds1)){
            Arrays.asList(delIds1.split(",")).stream().forEach(p->{
                cargoCategoryService.removeById(p);
            });
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
    public JsonResults update(Cargo bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        cargoService.updateById(bean);
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
        cargoService.update(new UpdateWrapper<Cargo>().set("isdel",1).eq("id",id));
//        cargoService.removeById(id);
//        cargoAttachmentService.remove(Wrappers.<CargoAttachment>query().eq("cargo_id",id));
//        cargoSkuService.remove(Wrappers.<CargoSku>query().eq("cargo_id",id));
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
        cargoSkuService.remove(Wrappers.<CargoSku>query().in("cargo_id",ids));
        cargoAttachmentService.remove(Wrappers.<CargoAttachment>query().in("cargo_id",ids));
        cargoService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }
    @RequestMapping("/select")
    @ResponseBody
    @Transactional
    public List<?> select() {
        List<?> list=new ArrayList<>();
        list = jdbcTemplate.queryForList("select `id` as value,`name` as `key` from cargo where isdel=0 order by create_time ");
        return list;
    }
}
