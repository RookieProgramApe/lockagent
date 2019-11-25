package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Cargo;
import com.lxkj.entity.Retailer;
import com.lxkj.entity.Shop;
import com.lxkj.entity.ShopLottery;
import com.lxkj.service.RetailerService;
import com.lxkj.service.ShopLotteryService;
import com.lxkj.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * 抽奖商家Controller
 * @author Zhanqian
 * @date 2019/11/11 9:56
 */
@Controller
@RequestMapping("/Shop")
@Slf4j
public class ShopAdminController  extends BaseController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private ShopLotteryService lotteryService;

    /**
     * 抽奖商家首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Shop/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Shop> pageList() {
        PageData params = this.getPageData();
        String keyword = params.getString("keyword");
        IPage<Shop> page = shopService.page(new Page<Shop>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Shop>()
                        .eq("is_del",0)
                        .eq("status", 1)
                        .like(StringUtils.isNotBlank(keyword),"name", keyword)
                        .or()
                        .like(StringUtils.isNotBlank(keyword),"phone", keyword)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            p.setLotteryCount(lotteryService.count(new QueryWrapper<ShopLottery>().eq("shop_id", p.getId())));
            p.setPartakeCount(this.jdbcTemplate.queryForObject("select sum(partake_count) from shop_lottery where shop_id=?", Integer.class, p.getId()));
            p.setLastTime(this.jdbcTemplate.queryForObject("select max(create_time) from shop_lottery where shop_id=?",
                    new BeanPropertyRowMapper<Date>(Date.class), p.getId()));
        });
        DataGridModel<Shop> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("Shop", shopService.getById(id));
        }else{
            model.addObject("Shop",new Shop());
        }
        model.setViewName("/admin/Shop/add");
        return model;
    }

    /**
     * 查询可用微股东列表
     * @return
     */
    @RequestMapping("/selectRetailers")
    @ResponseBody
    @Transactional
    public JsonResults selectRetailers(String flag) {
        var list= retailerService.list(new QueryWrapper<Retailer>()
                .eq("status", 1)
                .eq("`type`", 3)
                .notIn("`id`", this.jdbcTemplate.queryForList("select retailer_id from shop where is_del = 0 and retailer_id<>?", String.class, flag))
                .orderByDesc("create_time"));
        return BuildSuccessJson(list,"查询成功");
    }

    /**
     * 保存
     * @param bean
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(Shop bean) {
        // 获取memberId
        var retailer = retailerService.getById(bean.getRetailerId());
        bean.setMemberId(retailer.getMemberId());
        if(StringUtils.isNotBlank(bean.getId())){
            shopService.updateById(bean);
        }else{
            bean.setCreateTime(new Date());
            shopService.save(bean);
            // 新建商家默认分配100张
            shopService.buycard(retailer.getId(), 100);
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
        shopService.update(new UpdateWrapper<Shop>().set("is_del",1).eq("id",id));
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
        Shop shop = shopService.getById(id);
        shop.setLotteryCount(lotteryService.count(new QueryWrapper<ShopLottery>().eq("shop_id", shop.getId())));
        shop.setPartakeCount(this.jdbcTemplate.queryForObject("select sum(partake_count) from shop_lottery where shop_id=?", Integer.class, shop.getId()));
        Date date = this.jdbcTemplate.queryForObject("select max(create_time) from shop_lottery where shop_id=?",
                new BeanPropertyRowMapper<Date>(Date.class), shop.getId());
        shop.setLastTime(date);
        // 商家信息
        model.addObject("Shop", shop);
        // 微股东信息
        Retailer retailer = retailerService.getById(shop.getRetailerId());
        // 微股东信息
        model.addObject("Retailer", retailer);
        model.setViewName("/admin/Shop/detail");
        return model;
    }

    /**
     * 抽奖分页列表
     * @return
     */
    @RequestMapping("/shopLotterys")
    @ResponseBody
    public DataGridModel<ShopLottery> shopLotterys(String shopId) {
        PageData params = this.getPageData();
        IPage<ShopLottery> page = lotteryService.page(new Page<ShopLottery>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<ShopLottery>().eq("shop_id", shopId)
                        .orderByDesc("create_time")
        );
        DataGridModel<ShopLottery> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return grid;
    }
}
