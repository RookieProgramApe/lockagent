package com.lxkj.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.common.util.PageData;
import com.lxkj.common.util.encrypt.PasswordUtils;
import com.lxkj.entity.SysUser;
import com.lxkj.entity.SysUserRoleRef;
import com.lxkj.service.SysUserRoleRefService;
import com.lxkj.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-01
 */
@Controller
@RequestMapping("/SysUser")
@Slf4j
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleRefService sysUserRoleRefService;
    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView index(ModelAndView model) {
        return BuildModelView("/admin/SysUser/list");
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
            model.addObject("SysUser",sysUserService.getById(id));
        }else{
            model.addObject("SysUser",new SysUser());
        }
        model.setViewName("/admin/SysUser/add");
        return model;
    }

    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/sava")
    @ResponseBody
    public JsonResults sava(SysUser user, String roleId) {
         if(StringUtils.isNotBlank(user.getId())){
             sysUserService.updateById(user);
             //删除角色
             sysUserRoleRefService.remove(new QueryWrapper<SysUserRoleRef>().eq("userId",user.getId()));
         }else{
             int i=sysUserService.count(new QueryWrapper<SysUser>().eq("userName",user.getUserName()));
             if(i>0){
                return  BuildFailJson("账号已存在,换一个吧");
             }
             user.setCreateTime(new Date());
             user.setPassWord(user.getPassWord());
             sysUserService.save(user);
         }
         //添加到角色
        if(StringUtils.isNotBlank(roleId)){
            if(StringUtils.isNotBlank(roleId)) Arrays.stream(roleId.split(",")).forEach(p->{
                sysUserRoleRefService.save(new SysUserRoleRef().setRoleId(p).setUserId(user.getId()));
            });
        }
        return BuildSuccessJson("提交成功");
    }


    /**
     * 用户列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<SysUser> pageList() {
        PageData params=this.getPageData();
        IPage<SysUser> page=sysUserService.page(new Page<SysUser>(params.getInteger("page"),
                params.getInteger("limit")),
                new QueryWrapper<SysUser>()
                        .like(StringUtils.isNotBlank(params.getString("userName")),"userName",params.getString("userName"))
                        .like(StringUtils.isNotBlank(params.getString("realName")),"realName",params.getString("realName")));
        DataGridModel<SysUser> bena=new DataGridModel(page.getRecords(),page.getTotal());
        return  bena;
    }

    /**
     * 进入修改密码界面
     * @param model
     * @return
     */
    @RequestMapping("/toUpdatePassWord")
    public ModelAndView toUpdatePassWord(ModelAndView model) {
        model.addObject("SysUser", ShiroUtils.getSysUser());
        model.setViewName("/admin/SysUser/updatePassWord");
        return model;
    }

    /**
     * 修改密码
     * @param
     * @return
     */
    @RequestMapping("/updatePassWord")
    @ResponseBody
    public JsonResults toUpdatePassWord(SysUser user) {
        //user.setPassWord(PasswordUtils.generate(user.getPassWord()));
        user.setPassWord(user.getPassWord());
        sysUserService.updateById(user);
        return BuildSuccessJson("修改成功");
    }


    /**
     * 删除
     * @param
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public JsonResults delete(String id) {
        SysUser user=sysUserService.getById(id);
        if(user.getUserName().equals("admin")){
          return BuildFailJson("超级管理账号不能删除");
        }
        sysUserService.removeById(id);
        return BuildSuccessJson("删除成功");
    }
    /**
     * 修改状态
     * @return
     */
    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResults updateState(SysUser user){
        if(StringUtils.isBlank(user.getId()))return BuildFailJson("主键不能为空");
        sysUserService.updateById(user);
        return BuildSuccessJson("修改成功");
    }


    @RequestMapping("/getMyRoleIds")
    @ResponseBody
    public JsonResults getMyRoleIds(String id){
        List<String> selectRole=new ArrayList<String>();
        if(StringUtils.isNotBlank(id)){
            sysUserRoleRefService.list(new QueryWrapper<SysUserRoleRef>().eq("userId",id)).stream().forEach(p->{
                selectRole.add(p.getRoleId());
            });
        }
        return BuildSuccessJson(selectRole,"查询成功");
    }
}
