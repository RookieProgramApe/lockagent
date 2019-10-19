package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.mapper.InstallerOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.lxkj.common.bean.DataGridModel;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.lxkj.entity.InstallerOrder;
import com.lxkj.service.InstallerOrderService;
import org.springframework.stereotype.Controller;
import com.lxkj.common.bean.BaseController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>
 * 安装订单 前端控制器
 * </p>
 * 首页路由：/InstallerOrder/list
 * @author 一个烧包
 * @since 2019-08-27
 */
@Controller
@RequestMapping("/InstallerOrder")
@Slf4j
public class InstallerOrderController extends BaseController {
    @Autowired
    private InstallerOrderService installerOrderService;
    @Resource
    private InstallerOrderMapper installerOrderMapper;
    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/InstallerOrder/list");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Map> pageList(String status) {
            PageData params=this.getPageData();
            IPage<Map> page=installerOrderMapper.queryPage(new Page<Map>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<InstallerOrder>()
                        .eq(StringUtils.isNotBlank(status),"a.status",status)
                    .orderByDesc("a.create_time"));
            DataGridModel<Map> grid=new DataGridModel(page.getRecords(),page.getTotal());
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
            model.addObject("InstallerOrder",installerOrderService.getById(id));
        }else{
            model.addObject("InstallerOrder",new InstallerOrder());
        }
        model.setViewName("/admin/InstallerOrder/add");
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
    public JsonResults save(InstallerOrder bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            installerOrderService.updateById(bean);
        }else{
            installerOrderService.save(bean);
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
    public JsonResults update(InstallerOrder bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            installerOrderService.updateById(bean);
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
        installerOrderService.removeById(id);
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
        installerOrderService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
