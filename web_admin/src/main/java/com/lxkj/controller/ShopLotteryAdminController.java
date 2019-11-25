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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商家抽奖后台管理
 * @author Zhanqian
 * @date 2019/11/9 16:32
 */
@Controller
@RequestMapping("/ShopLottery")
@Slf4j
public class ShopLotteryAdminController extends BaseController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private ShopLotteryService shopLotteryService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private LotteryPrizeService lotteryPrizeService;

    @Autowired
    private CargoAttachmentService cargoAttachmentService;

    @Autowired
    private LotteryRecordService recordService;

    /**
     * 首页RetailerReward
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/ShopLottery/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<ShopLottery> pageList() {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<ShopLottery> page = shopLotteryService.page(new Page<ShopLottery>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<ShopLottery>()
                        .eq("is_del",0)
                        .eq("status", 1)
                        .like(StringUtils.isNotBlank(keyword),"name", keyword)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            p.setShopName(shopService.getById(p.getShopId()).getName());
        });
        DataGridModel<ShopLottery> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("ShopLottery", shopLotteryService.getById(id));
//            model.addObject("cargoList", new ArrayList<>());
        }else{
            model.addObject("ShopLottery",new ShopLottery());
//            model.addObject("cargoList", new ArrayList<>());
        }
        model.setViewName("/admin/ShopLottery/add");
        return model;
    }

    /**
     * 查询可用商品列表
     * @return
     */
    @RequestMapping("/selectCargoById")
    @ResponseBody
    @Transactional
    public JsonResults selectCargoById(String id) {
        Cargo cargo = cargoService.getById(id);
        var picture = this.cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id", cargo.getId()).eq("type", 1));
        cargo.setPicture(picture == null ? "" : picture.getUrl());
        return BuildSuccessJson(cargo,"查询成功");
    }

    /**
     * 查询可用商品列表
     * @return
     */
    @RequestMapping("/selectCargos")
    @ResponseBody
    @Transactional
    public JsonResults selectCargos() {
        var list = cargoService.list(new QueryWrapper<Cargo>()
                .eq("isdel", 0)
                .orderByAsc("`sort`"));
        list.stream().forEach(p -> {
            var picture = this.cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id", p.getId()).eq("type", 1));
            p.setPicture(picture == null ? "" : picture.getUrl());
        });
        return BuildSuccessJson(list,"查询成功");
    }

    /**
     * 查询可用商品列表
     * @return
     */
    @RequestMapping("/selectCargosByLotteryId")
    @ResponseBody
    public JsonResults selectCargosByLotteryId(String lotteryId) {
        System.out.println(lotteryId);
        var list = lotteryPrizeService.list(new QueryWrapper<LotteryPrize>().eq("lottery_id", lotteryId).orderByAsc("`sort`"));
        return BuildSuccessJson(list,"查询成功");
    }

    /**
     * 查询商家列表
     * @return
     */
    @RequestMapping("/selectShops")
    @ResponseBody
    @Transactional
    public JsonResults selectShops() {
        var list = shopService.list(new QueryWrapper<Shop>()
                .eq("is_del", 0)
                .eq("status", 1)
                .orderByDesc("create_time"));
        return BuildSuccessJson(list,"查询成功");
    }

    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(ShopLottery bean,String stepArr,String delIds) {
        String id = "";
        if(StringUtils.isNotBlank(bean.getId())){
            shopLotteryService.updateById(bean);
            lotteryPrizeService.remove(Wrappers.<LotteryPrize>query().eq("lottery_id", bean.getId()));
        }else{
            bean.setCreateTime(new Date());
            shopLotteryService.save(bean);
        }
        id = bean.getId();
        //---------------抽奖礼品-添加/新增-------------------
        if(StringUtils.isNotBlank(stepArr)){
            List<LotteryPrize> list=new ArrayList<LotteryPrize>();
            JSONArray jsonArray = JSON.parseArray(stepArr);
            for (int i = 0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LotteryPrize addBean=new LotteryPrize();
                addBean.setLotteryId(id);
                addBean.setId(jsonObject.containsKey("id")?jsonObject.getString("id"):"");
                addBean.setCargoId(jsonObject.containsKey("cargoId")?jsonObject.getString("cargoId"):"");
                addBean.setCargoName(jsonObject.containsKey("cargoName")?jsonObject.getString("cargoName"):"");
                addBean.setCount(jsonObject.containsKey("count")?jsonObject.getInteger("count"):0);
                addBean.setNum(addBean.getCount());
                addBean.setImg(jsonObject.containsKey("img")?jsonObject.getString("img"):"");
                list.add(addBean);
            }
            if(!list.isEmpty()){
                lotteryPrizeService.saveOrUpdateBatch(list);
            }
        }
        //------------抽奖礼品删除-----------
        if(StringUtils.isNotBlank(delIds)){
            Arrays.asList(delIds.split(",")).stream().forEach(p -> {
                lotteryPrizeService.removeById(p);
            });
        }
        return BuildSuccessJson("提交成功");
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
        shopLotteryService.update(new UpdateWrapper<ShopLottery>().set("is_del",1).eq("id",id));
        return BuildSuccessJson("删除成功");
    }

    /**
     * 详情
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id, ModelAndView model) {
        ShopLottery lottery = shopLotteryService.getById(id);
        Shop shop = shopService.getById(lottery.getShopId());
        shop.setLotteryCount(shopLotteryService.count(new QueryWrapper<ShopLottery>().eq("shop_id", lottery.getShopId())));
        shop.setPartakeCount(this.jdbcTemplate.queryForObject("select sum(partake_count) from shop_lottery where shop_id=?", Integer.class, lottery.getShopId()));
        Date date = this.jdbcTemplate.queryForObject("select max(create_time) from shop_lottery where shop_id=?",
                new BeanPropertyRowMapper<Date>(Date.class), shop.getId());
        shop.setLastTime(date);
        model.addObject("Lottery", lottery);
        model.addObject("Shop", shop);
        model.setViewName("/admin/ShopLottery/detail");
        return model;
    }

    /**
     * 抽奖记录分页列表
     * @return
     */
    @RequestMapping("/lotteryRecords")
    @ResponseBody
    public DataGridModel<LotteryRecord> lotteryRecords(String lotteryId) {
        PageData params = this.getPageData();
        IPage<LotteryRecord> page = recordService.page(new Page<LotteryRecord>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<LotteryRecord>().eq("lottery_id", lotteryId)
                .orderByDesc("create_time")
        );
        DataGridModel<LotteryRecord> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return grid;
    }
}
