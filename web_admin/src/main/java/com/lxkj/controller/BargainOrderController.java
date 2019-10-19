package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.BargainOrder;
import com.lxkj.entity.CargoAttachment;
import com.lxkj.mapper.BargainOrderMapper;
import com.lxkj.service.BargainOrderService;
import com.lxkj.service.CargoAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 砍价订单 前端控制器
 * </p>
 * 首页路由：/BargainOrder/list
 * @author 一个烧包
 * @since 2019-08-02
 */
@Controller
@RequestMapping("/BargainOrder")
@Slf4j
public class BargainOrderController extends BaseController {
    @Autowired
    private BargainOrderService bargainOrderService;
    @Resource
    private BargainOrderMapper bargainOrderMapper;
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/BargainOrder/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<BargainOrder> pageList() {
            PageData params=this.getPageData();
            IPage<BargainOrder> page=bargainOrderService.page(new Page<BargainOrder>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<BargainOrder>()
                    .orderByDesc("createTime"));
            DataGridModel<BargainOrder> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<BargainOrder> pageList1(String keyword) {
        PageData params=this.getPageData();
        IPage<Map> page=bargainOrderMapper.pageListBargainOrder(new Page<BargainOrder>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<BargainOrder>()
                        .and(StringUtils.isNotBlank(keyword),i->i.like("m.nickname",keyword).or().inSql("bo.cargo_id ","select id from cargo where name like '%"+keyword+"%'"))
                        .orderByDesc("bo.create_time"));
        page.getRecords().stream().forEach(p->{
            List<CargoAttachment> list =  cargoAttachmentService.list(Wrappers.<CargoAttachment>query().eq("cargo_id",p.get("cargo_id")));
            if(list!=null&&list.size()>0){
                p.put("attachment",list);
            }else{
                p.put("attachment",null);
            }
        });
        DataGridModel<BargainOrder> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("BargainOrder",bargainOrderService.getById(id));
        }else{
            model.addObject("BargainOrder",new BargainOrder());
        }
        model.setViewName("/admin/BargainOrder/add");
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
    public JsonResults save(BargainOrder bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            bargainOrderService.updateById(bean);
        }else{
            bargainOrderService.save(bean);
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
    public JsonResults update(BargainOrder bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            bargainOrderService.updateById(bean);
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
        bargainOrderService.removeById(id);
        return BuildSuccessJson("删除成功");
    }


}
