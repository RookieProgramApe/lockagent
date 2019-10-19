package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Giftcard;
import com.lxkj.entity.GiftcardBatch;
import com.lxkj.service.GiftcardBatchService;
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

import java.net.URL;
import java.util.Arrays;

/**
 * <p>
 * 卡片批量导入 前端控制器
 * </p>
 * 首页路由：/GiftcardBatch/list
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/GiftcardBatch")
@Slf4j
public class GiftcardBatchController extends BaseController {
    @Autowired
    private GiftcardBatchService giftcardBatchService;
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
        model.setViewName("/admin/GiftcardBatch/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<GiftcardBatch> pageList() {
            PageData params=this.getPageData();
            IPage<GiftcardBatch> page=giftcardBatchService.page(new Page<GiftcardBatch>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<GiftcardBatch>()
                    .groupBy("sequence")
                    .orderByDesc("create_time"));
            DataGridModel<GiftcardBatch> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<GiftcardBatch> pageList1() {
        PageData params=this.getPageData();
        String sequence  = params.getString("sequence");
        IPage<GiftcardBatch> page=giftcardBatchService.page(new Page<GiftcardBatch>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<GiftcardBatch>()
                        .eq("sequence",sequence)
                        .orderByDesc("create_time")
                        .orderByAsc("serial"));
        DataGridModel<GiftcardBatch> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("GiftcardBatch",giftcardBatchService.getById(id));
        }else{
            model.addObject("GiftcardBatch",new GiftcardBatch());
        }
        model.setViewName("/admin/GiftcardBatch/add");
        return model;
    }


    @RequestMapping("/detail")
    public ModelAndView detail(String sequence,ModelAndView model) {
        model.addObject("sequence",sequence);
        model.setViewName("/admin/GiftcardBatch/detail");
        return model;
    }

    @RequestMapping("/batchImport")
    @ResponseBody
    public JsonResults batchImport( String fileName) throws Exception {
        URL url = new URL(fileName);
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return BuildFailJson("上传文件格式不正确");
        }
        String squ = giftcardBatchService.batchImport(url.openStream(),ShiroUtils.getUserId());
        if(squ==null){
            return BuildFailJson("导入失败，请联系管理员");
        }
        return BuildSuccessJson("导入完成");
    }
    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(GiftcardBatch bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            giftcardBatchService.updateById(bean);
        }else{
            bean.setCreateBy(ShiroUtils.getUserId());
            giftcardBatchService.save(bean);
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
    public JsonResults update(GiftcardBatch bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            giftcardBatchService.updateById(bean);
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
        giftcardBatchService.removeById(id);
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
        giftcardBatchService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
