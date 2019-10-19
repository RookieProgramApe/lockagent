package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Giftcard;
import com.lxkj.service.GiftcardService;
import com.lxkj.service.RetailerGiftcardService;
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

/**
 * <p>
 * 卡片 前端控制器
 * </p>
 * 首页路由：/Giftcard/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Giftcard")
@Slf4j
public class GiftcardController extends BaseController {
    @Autowired
    private GiftcardService giftcardService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.addObject("cardNum",giftcardService.count());
        model.addObject("cardNum1",giftcardService.count(Wrappers.<Giftcard>query().eq("status",1)));
        model.addObject("cardNum2",retailerGiftcardService.count(null));
        model.addObject("cardNum3",giftcardService.count(Wrappers.<Giftcard>query().eq("status",3)));
        model.setViewName("/admin/Giftcard/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Giftcard> pageList() {
            PageData params=this.getPageData();
            String keyword = params.getString("keyword");
            String selectId2 = params.getString("selectId2");
            IPage<Giftcard> page=giftcardService.page(new Page<Giftcard>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Giftcard>()
                    .eq("1",1)
                    .and(StringUtils.isNotBlank(keyword),i->i.like("serial",keyword).or().like("passcode",keyword))
                    .eq(StringUtils.isNotBlank(selectId2),"status",params.getInteger("selectId2"))
                    .orderByAsc("serial")
              );
            DataGridModel<Giftcard> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageListRetailer")
    @ResponseBody
    public DataGridModel<Giftcard> pageListRetailer(String member_id) {
        PageData params=this.getPageData();
        IPage<Giftcard> page=giftcardService.page(new Page<Giftcard>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Giftcard>()
                        .select("giftcard.*"
                                ,"(select rg.state from retailer_giftcard  rg where rg.giftcard_id=giftcard.id and rg.member_id='"+member_id+"') as state"
                        )
                        .inSql("id","select rg.giftcard_id from retailer_giftcard  rg where rg.member_id='"+member_id+"'")
                        .orderByAsc("serial")
                        //.orderByDesc("(select rg.create_time from retailer_giftcard  rg where rg.giftcard_id=giftcard.id and rg.member_id='"+member_id+"')")
        );
        DataGridModel<Giftcard> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    @RequestMapping("/pageListOrder")
    @ResponseBody
    public DataGridModel<Giftcard> pageListOrder(String order_id) {
        PageData params=this.getPageData();
        IPage<Giftcard> page=giftcardService.page(new Page<Giftcard>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Giftcard>()
                        .select("giftcard.*"
                                ,"(select rg.state from retailer_giftcard  rg where rg.giftcard_id=giftcard.id and rg.order_id='"+order_id+"') as state"
                        )
                        .inSql("id","select rg.giftcard_id from retailer_giftcard  rg where rg.order_id='"+order_id+"'")
                        .orderByAsc("serial")
                        //.orderByDesc("(select rg.create_time from retailer_giftcard  rg where rg.giftcard_id=giftcard.id and rg.member_id='"+order_id+"')")
        );
        DataGridModel<Giftcard> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("Giftcard",giftcardService.getById(id));
        }else{
            model.addObject("Giftcard",new Giftcard());
        }
        model.setViewName("/admin/Giftcard/add");
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
    public JsonResults save(Giftcard bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            giftcardService.updateById(bean);
        }else{
            giftcardService.save(bean);
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
    public JsonResults update(Giftcard bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            giftcardService.updateById(bean);
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
        giftcardService.removeById(id);
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
        giftcardService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
