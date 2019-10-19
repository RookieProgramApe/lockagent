package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.CargoSku;
import com.lxkj.service.CargoSkuService;
import com.lxkj.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 商品规格库存 前端控制器
 * </p>
 * 首页路由：/CargoSku/list
 *
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/CargoSku")
@Slf4j
public class CargoSkuController extends BaseController {

  @Autowired
  private CargoSkuService cargoSkuService;
  @Autowired
  private OrderService orderService;

  /**
   * 首页
   */
  @RequestMapping("/list")
  public ModelAndView list(ModelAndView model) {
    model.setViewName("/admin/CargoSku/list");
    return model;
  }


  /**
   * 分页列表
   */
  @RequestMapping("/pageList")
  @ResponseBody
  public DataGridModel<CargoSku> pageList(String keyword) {
    PageData params = this.getPageData();
    String cargo_id = params.getString("cargo_id");
    IPage<CargoSku> page = cargoSkuService.page(new Page<CargoSku>(params.getInteger("page"), params.getInteger("limit")),
            new QueryWrapper<CargoSku>()
                    .eq(StringUtils.isNotBlank(cargo_id), "cargo_id", cargo_id)
                    .like(StringUtils.isNotBlank(keyword), "name", keyword)
                    .orderByDesc("create_time"));
    DataGridModel<CargoSku> grid = new DataGridModel(page.getRecords(), page.getTotal());
    return grid;
  }

  @RequestMapping("/pageList1")
  @ResponseBody
  public DataGridModel<CargoSku> pageList1(String keyword) {
    PageData params = this.getPageData();
    String cargo_id = params.getString("cargo_id");
    IPage<CargoSku> page = cargoSkuService.page(new Page<CargoSku>(params.getInteger("page"), params.getInteger("limit")),
            new QueryWrapper<CargoSku>()
                    .eq("cargo_id", cargo_id)
                    .orderByDesc("create_time"));

//        page.getRecords().stream().forEach(p->{
//              Integer num = jdbcTemplate.queryForObject("select ifnull(sum(count),0) from `order` where sku_id=? and status in (2,3,4)",Integer.class,p.getId());
//              p.setInventory((p.getInventory()==null?0:p.getInventory())-(num==null?0:num));
//        });
    DataGridModel<CargoSku> grid = new DataGridModel(page.getRecords(), page.getTotal());
    return grid;
  }

  /**
   * 跳转添加/编辑界面
   */
  @RequestMapping("/toAdd")
  public ModelAndView toAdd(String id, ModelAndView model) {
    if (StringUtils.isNotBlank(id)) {
      model.addObject("CargoSku", cargoSkuService.getById(id));
    } else {
      model.addObject("CargoSku", new CargoSku());
    }
    model.setViewName("/admin/CargoSku/add");
    return model;
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  @ResponseBody
  @Transactional
  public JsonResults save(CargoSku bean) {
    if (StringUtils.isNotBlank(bean.getId())) {
      cargoSkuService.updateById(bean);
    } else {
      cargoSkuService.save(bean);
    }
    return BuildSuccessJson("提交成功");
  }

  @RequestMapping("/save1")
  @ResponseBody
  @Transactional
  public JsonResults save1(String name, String stepArr, String delIds) {
    if (StringUtils.isNotBlank(name)) {
      return BuildFailJson("SKU名称不能为空");
    }

    return BuildSuccessJson("提交成功");
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  @ResponseBody
  @Transactional
  public JsonResults update(CargoSku bean) {
    if (StringUtils.isBlank(bean.getId())) {
      return BuildFailJson("主键不能为空");
    }
    cargoSkuService.updateById(bean);
    return BuildSuccessJson("修改成功");
  }

  /**
   * 单个删除
   */
  @RequestMapping("/delete")
  @ResponseBody
  @Transactional
  public JsonResults delete(String name) {
    cargoSkuService.remove(Wrappers.<CargoSku>query().eq("name", name));
    return BuildSuccessJson("删除成功");
  }

  /**
   * 批量删除
   */
  @RequestMapping("/deleteForList")
  @ResponseBody
  @Transactional
  public JsonResults deleteForList(@RequestParam(value = "ids[]") String[] ids) {
    cargoSkuService.remove(Wrappers.<CargoSku>query().in("name", ids));

    return BuildSuccessJson("删除成功");
  }

  @RequestMapping("/select")
  @ResponseBody
  public List<?> select(String id) {
    List<?> list = new ArrayList<>();
    list = jdbcTemplate.queryForList("select `id` as value,`name` as `key` from cargo_sku where cargo_id=? order by create_time ", id);
    return list;
  }

  @RequestMapping("/select1")
  @ResponseBody
  public JsonResults select1(String cargo_id) {
    List<?> list = new ArrayList<>();
    list = jdbcTemplate.queryForList("select * from cargo_sku where cargo_id=? order by create_time ", cargo_id);
    return BuildSuccessJson(list, "查询成功");
  }

  @RequestMapping("/select2")
  @ResponseBody
  public JsonResults select2(String cargo_id) {
    List<?> list = new ArrayList<>();
    list = jdbcTemplate.queryForList("select * from cargo_category where cargo_id=? order by create_time ", cargo_id);
    return BuildSuccessJson(list, "查询成功");
  }

}
