package com.lxkj.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DateTreeGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.SysResource;
import com.lxkj.service.SysResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Controller
@RequestMapping("/SysResource")
@Slf4j
public class SysResourceController extends BaseController {
    @Autowired
    private SysResourceService sysResourceService;

    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        return BuildModelView("/admin/SysResource/list");
    }

    @RequestMapping("/treeList")
    @ResponseBody
    public DateTreeGridModel<SysResource> treeList() {
        List<SysResource> res= sysResourceService.list(new QueryWrapper<SysResource>().orderByAsc("orders"));
        DateTreeGridModel<SysResource> model=new DateTreeGridModel(res,Long.valueOf(res.size()));
        return  model;
    }

    @RequestMapping("/getTreeData")
    @ResponseBody
    public List<Map<String,Object>> getTreeData() {
        List<Map<String,Object>> res= sysResourceService.getTreeList();
        return  res;
    }

    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id, ModelAndView model) {
        if(StringUtils.isNotBlank(id)){
            model.addObject("SysResource",sysResourceService.getById(id));
        }else{
            model.addObject("SysResource", new SysResource());
        }
        model.setViewName("/admin/SysResource/add");
        return model;
    }
    @RequestMapping("/sava")
    @ResponseBody
    public JsonResults sava(SysResource user) {
        if(StringUtils.isNotBlank(user.getId())){
            sysResourceService.updateById(user);
        }else{
            SysResource bean=sysResourceService.getById(user.getPid());
            if(bean==null){
                user.setLevels(1);
            }else{
                user.setLevels(bean.getLevels()+1);
            }
            sysResourceService.save(user);
        }
        return BuildSuccessJson("提交成功");
    }



    @RequestMapping("/doUpdate")
    @ResponseBody
    public JsonResults enableStatus(SysResource user) {
        sysResourceService.updateById(user);
        return BuildSuccessJson("修改成功");
    }

    @RequestMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResults delete(String id) {
        sysResourceService.remove(new QueryWrapper<SysResource>().eq("pid",id));
        sysResourceService.removeById(id);
        return BuildSuccessJson("删除成功");
    }

}
