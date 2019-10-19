package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.SysUser;
import com.lxkj.entity.SysUserOrgRef;
import com.lxkj.entity.SysUserRoleRef;
import com.lxkj.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-05-13
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

 @Autowired
 private SysUserRoleRefService sysUserRoleRefService;
 @Autowired
 private SysUserOrgRefService sysUserOrgRefService;

 @Transactional
 public boolean delete(String id){
  SysUser user = this.getById(id);
  if (user.getUserName().equals("admin")) {
   return  false;//("超级管理账号不能删除");
  }
  this.removeById(id);
  sysUserRoleRefService.remove(new QueryWrapper<SysUserRoleRef>().eq("userId",user.getId()));
  sysUserOrgRefService.remove(new QueryWrapper<SysUserOrgRef>().eq("userId",user.getId()));
  return true;//("删除成功");
 }
 }