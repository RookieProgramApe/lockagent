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

import java.util.*;

/**
 * <p>
 * 商品 前端控制器
 * </p>
 * 首页路由：/Credit/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Credit")
@Slf4j
public class CreditAdminController extends BaseController {
    @Autowired
    private CreditCargoService creditCargoService;
    @Autowired
    private CreditCargoAttachmentService creditCargoAttachmentService;
    @Autowired
    private CreditCargoSkuService creditCargoSkuService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RetailerRewardService retailerRewardService;
    /**
     * 首页RetailerReward
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/CreditCargo/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CreditCargo> pageList() {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<CreditCargo> page=creditCargoService.page(new Page<CreditCargo>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<CreditCargo>()
                        .eq("isdel",0)  //未删除
                        .like(StringUtils.isNotBlank(keyword),"name",keyword)
                        .orderByAsc("sort"));
        page.getRecords().stream().forEach(p->{
            List<CreditCargoAttachment> list =  creditCargoAttachmentService.list(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",p.getId()));
            if(list!=null&&list.size()>0){
                p.setAttachment(list);
            }

            p.setSaleNum(creditCargoService.getSaleNum(p.getId()));
        });
        DataGridModel<CreditCargo> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<CreditCargo> pageList1(String csid) {
        PageData params = this.getPageData();
        List<CreditCargo> page=creditCargoService.list(
                new QueryWrapper<CreditCargo>()
                        .eq("isdel",0)
                        .ne("id",csid)
                        .orderByAsc("sort"));
        DataGridModel<CreditCargo> grid=new DataGridModel(page,Long.valueOf(page.size()));
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
    public DataGridModel<CreditCargo> pageListReward(String retailer_id) {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<CreditCargo> page=creditCargoService.page(new Page<CreditCargo>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<CreditCargo>()
                        .eq("isdel",0)
                        .select("cargo.*"
                                ,"(select figure from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as figure"
                                ,"(select id from retailer_reward where cargo_id=cargo.id and retailer_id='"+retailer_id+"') as retailerRewardId "
                        )
                        .eq("status",1)
                        .like(StringUtils.isNotBlank(keyword),"name",keyword)
        );
        page.getRecords().stream().forEach(p->{
            List<CreditCargoAttachment> list =  creditCargoAttachmentService.list(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",p.getId()));
            if(list!=null&&list.size()>0){
                p.setAttachment(list);
            }
            p.setSaleNum(creditCargoService.getSaleNum(p.getId()));
        });
        DataGridModel<CreditCargo> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("CreditCargo",creditCargoService.getById(id));
            CreditCargoAttachment image = creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",id).eq("type",1));
            CreditCargoAttachment video = creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",id).eq("type",2));
            model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
            model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");
        }else{
            model.addObject("CreditCargo",new CreditCargo());
            model.addObject("image","");
            model.addObject("video","");
        }
        model.setViewName("/admin/CreditCargo/add");
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
            model.addObject("CreditCargo",creditCargoService.getById(id));
            CreditCargoAttachment image = creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",id).eq("type",1));
            CreditCargoAttachment video = creditCargoAttachmentService.getOne(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",id).eq("type",2));
            model.addObject("image",(image!=null&&StringUtils.isNotBlank(image.getUrl()))?image.getUrl():"");
            model.addObject("video",(video!=null&&StringUtils.isNotBlank(video.getUrl()))?video.getUrl():"");
        model.setViewName("/admin/CreditCargo/detail");
        return model;
    }


    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(CreditCargo bean,String url1,String url2,String stepArr,String delIds) {
        System.out.println(bean+"save fu");
        String id = "";
        if(StringUtils.isNotBlank(bean.getId())){
            id = bean.getId();
            creditCargoService.updateById(bean);
            creditCargoAttachmentService.remove(Wrappers.<CreditCargoAttachment>query().eq("cargo_id",bean.getId()));
        }else{
            bean.setCreateTime(new Date());
            bean.setSort(creditCargoService.count()+1);
            System.out.println("lai else  fu");
            creditCargoService.save(bean);
            id = bean.getId();
        }
        if(StringUtils.isNotBlank(url1)){
            creditCargoAttachmentService.save(new CreditCargoAttachment().setCargoId(bean.getId()).setType(1).setUrl(url1));
        }
        if(StringUtils.isNotBlank(url2)){
            creditCargoAttachmentService.save(new CreditCargoAttachment().setCargoId(bean.getId()).setType(2).setUrl(url2));
        }
        //---------------商品规格-添加/新增-------------------
        if(StringUtils.isNotBlank(stepArr)){
            List<CreditCargoSku> list=new ArrayList<CreditCargoSku>();
            JSONArray jsonArray = JSON.parseArray(stepArr);
            for (int i = 0; i < jsonArray.size()-1; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonObject2 = jsonArray.getJSONObject(i+1);
                CreditCargoSku addBean=new CreditCargoSku();

                addBean.setCargoId(bean.getId());
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setName(jsonObject.containsKey("content")?jsonObject.getString("content"):"");

                System.out.println("添加商品规格"+addBean.getId()+"ewe"+addBean.getName());
                //规格的价格
                addBean.setPrice(jsonObject2.containsKey("content2")?jsonObject2.getString("content2"):"");
            /*    addBean.setPrice("22");*/
               /* System.out.println("进来了"+addBean.getPrice()+"元"+addBean.getName());*/
                System.out.println("商品list"+"输出"+list);
                if(StringUtils.isBlank(addBean.getId())){
                    addBean.setCreateTime(new Date());
                }
                list.add(addBean);
                i=i+1;
            }
            //bukong zhu[CargoSku(id=, cargoId=40654e161916a62b7dd540874359e57e,
            // name=eq, price=23, inventory=null, status=null, createTime=Fri Oct 18 02:42:12 CST 2019, lockType=null)]

            //bukong fu[CreditCargoSku(id=, creditId=926df242f3718944a764b53b11fdfe0c, name=eq, price=23,
            // inventory=null, status=null, createTime=Fri Oct 18 02:38:58 CST 2019, lockType=null)]

            // Preparing: INSERT INTO cargo_sku ( id, cargo_id, `name`, `price`, create_time ) VALUES ( ?, ?, ?, ?, ? )
            // Parameters: e8facf31f36124ef4c6ed768d3bc40ff(String), 6744dacfdd4725abd53c93238d2bcb62(String), 321(String), 321(String), 2019-10-18 02:55:27.308(Timestamp)

            //Preparing: INSERT INTO 'creditcargo_sku' ( id, cargo_id, `name`, `price`, create_time ) VALUES ( ?, ?, ?, ?, ? )
            if(!list.isEmpty()){
                System.out.println("bukong zi"+list);
                creditCargoSkuService.saveOrUpdateBatch(list);
            }
            System.out.println("kong du"+list);
        }
        //------------商品规格删除-----------
        if(StringUtils.isNotBlank(delIds)){
            Arrays.asList(delIds.split(",")).stream().forEach(p->{
                creditCargoSkuService.removeById(p);
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
    public JsonResults update(CreditCargo bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        creditCargoService.updateById(bean);
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
        creditCargoService.update(new UpdateWrapper<CreditCargo>().set("isdel",1).eq("id",id));
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
        creditCargoSkuService.remove(Wrappers.<CreditCargoSku>query().in("cargo_id",ids));
        creditCargoAttachmentService.remove(Wrappers.<CreditCargoAttachment>query().in("cargo_id",ids));
        creditCargoService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }
    @RequestMapping("/select")
    @ResponseBody
    @Transactional
    public List<?> select() {
        System.out.println("lai select  creditAdmincontroller");
        List<?> list=new ArrayList<>();
         list = jdbcTemplate.queryForList("select `id` as value,`name` as `key` from credit where isdel=0 order by create_time ");
        System.out.println(list+"fu list");
        return list;
    }
}
