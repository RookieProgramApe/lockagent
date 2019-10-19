package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.CreditCargo;
import com.lxkj.entity.CreditWarehousing;
import com.lxkj.entity.Warehousing;
import com.lxkj.service.CargoService;
import com.lxkj.service.CreditCargoService;
import com.lxkj.service.CreditWarehousingService;
import com.lxkj.service.WarehousingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 * 积分商城货物入库记录 前端控制器
 * </p>
 * 首页路由：/Warehousing/list
 * @author lih
 * @since 2019-10-17
 */
@Controller
@RequestMapping("/CreditWarehousing")
@Slf4j
public class CreditWarehousingController extends BaseController {
    @Autowired
    private CreditWarehousingService creditWarehousingService;

    @Autowired
    private CreditCargoService creditCargoService;
    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/CreditWarehousing/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CreditWarehousing> pageList() {
            PageData params=this.getPageData();
            IPage<CreditWarehousing> page=creditWarehousingService.page(new Page<CreditWarehousing>(params.getInteger("page"),params.getInteger("limit")),
                            new QueryWrapper<CreditWarehousing>()
                    .select("id","inventory","create_time","remk","(select name from credit where id= creditwarehousing.cargo_id ) as cargo_id","(select name from creditcargo_sku where id=creditwarehousing.sku_id) as sku_id ")
                    .orderByDesc("create_time"));
            DataGridModel<CreditWarehousing> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("CreditWarehousing",creditWarehousingService.getById(id));
        }else{
            model.addObject("CreditWarehousing",new CreditWarehousing());
        }
        model.setViewName("/admin/CreditWarehousing/add");
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
    public JsonResults save(CreditWarehousing bean) {
        System.out.println(bean+"测试拿到bean没   save");
        if(StringUtils.isNotBlank(bean.getId())){
            creditWarehousingService.updateById(bean);
        }else{
            bean.setCreateBy(ShiroUtils.getUserId());
            bean.setCreateTime(new Date());
            creditWarehousingService.save(bean);
        }

        creditCargoService.updateInventory(bean.getSkuId());
        return BuildSuccessJson("提交成功");
     }

    /**
    * 修改
    * @return
    */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(CreditWarehousing bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            creditWarehousingService.updateById(bean);
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
        var uid=creditWarehousingService.getById(id).getSkuId();
       creditWarehousingService.removeById(id);
        creditCargoService.updateInventory(uid);
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
        creditWarehousingService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
