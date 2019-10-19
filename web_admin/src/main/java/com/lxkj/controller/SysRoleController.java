package com.lxkj.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.SysRole;
import com.lxkj.entity.SysRoleResourceRef;
import com.lxkj.entity.SysUserRoleRef;
import com.lxkj.service.SysResourceService;
import com.lxkj.service.SysRoleResourceRefService;
import com.lxkj.service.SysRoleService;
import com.lxkj.service.SysUserRoleRefService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Controller
@RequestMapping("/SysRole")
@Slf4j
public class SysRoleController extends BaseController {
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private SysRoleResourceRefService sysRoleResourceRefService;
    @Resource
    private SysUserRoleRefService sysUserRoleRefService;
    @Resource
    private SysResourceService sysResourceService;
    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        return BuildModelView("/admin/SysRole/list");
    }

    /**
     * 跳转添加/编辑界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id, ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("SysRole",sysRoleService.getById(id));
        }else{
            model.addObject("SysRole",new SysRole());
        }
        model.setViewName("/admin/SysRole/add");
        return model;
    }

    /**
     * 列表数据
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<SysRole> pageList(Integer page, Integer limit) {
        PageData params=this.getPageData();
        IPage<SysRole> bean=sysRoleService.page(new Page<SysRole>(page,limit),new QueryWrapper<SysRole>());
        DataGridModel<SysRole> list=new DataGridModel(bean.getRecords(),bean.getTotal());
        return  list;
    }

    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/sava")
    @ResponseBody
    @Transactional
    public JsonResults sava(SysRole user, @RequestParam(required =false ,value = "ids[]") String[] ids) {
        if(StringUtils.isNotBlank(user.getId())){
            sysRoleService.updateById(user);
            //删除所有关联
            sysRoleResourceRefService.remove(new QueryWrapper<SysRoleResourceRef>().eq("roleId",user.getId()));
        }else{
            user.setCreateTime(new Date());
            sysRoleService.save(user);
        }
        //插入关联表
        if(ids!=null) Arrays.stream(ids).forEach(p->{
            sysRoleResourceRefService.save(new SysRoleResourceRef().setResourceId(p).setRoleId(user.getId()));
        });
        return BuildSuccessJson("提交成功");
    }

    /**
     * 删除
     * @param
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResults delete(String id) {
        sysRoleService.removeById(id);
        sysRoleResourceRefService.remove(new QueryWrapper<SysRoleResourceRef>().eq("roleId",id));
        sysUserRoleRefService.remove(new QueryWrapper<SysUserRoleRef>().eq("roleId",id));
        return BuildSuccessJson("删除成功");
    }

    @RequestMapping("/getTreeDataByRole")
    @ResponseBody
    public JsonResults getTreeDataByRole(String roleId) {
        List<Map<String,Object>> treeList= sysResourceService.getTreeList();
        if(StringUtils.isNotBlank(roleId)){
            treeList.stream().forEach(p->{
               int i=sysRoleResourceRefService.count(new QueryWrapper<SysRoleResourceRef>().eq("roleId",roleId).eq("resourceId",(String)p.get("id")));
               if(i>0){
                    p.put("checked",true);
                }
            });
        }
        return BuildSuccessJson(treeList,"查询成功");
    }

    /**
     * 查询所有的角色
     * @return
     */
    @RequestMapping("/selectList")
    @ResponseBody
    public List<SysRole> selectList() {
        List<SysRole> treeList= sysRoleService.list(null);
        return treeList;
    }
}
