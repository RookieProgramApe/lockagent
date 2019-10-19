package com.lxkj.common.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxkj.entity.SysResource;
import com.lxkj.entity.SysRole;
import com.lxkj.entity.SysUser;
import com.lxkj.service.SysResourceService;
import com.lxkj.service.SysRoleService;
import com.lxkj.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author apple
 * @Date 2018/3/18 23:45
 * @Description:
 * @Modified by:
 */
//实现AuthorizingRealm接口用户用户认证
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysResourceService sysResourceService;
    @Autowired
    private SysRoleService sysRoleService;

    //用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户账号
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        SysUser userBean = sysUserService.getOne(new QueryWrapper<SysUser>().eq("userName", token.getUsername()));
        if (userBean != null) {
            String pwd = String.valueOf(token.getPassword());//登录密码
            if (!pwd.equals(userBean.getPassWord())) {
                throw new AuthenticationException("用户名或者密码错误");
            }
            if (userBean.getEnableStatus() == 1) {
                throw new DisabledAccountException("该用户已被禁用");
            }
            //菜单查询
            List<SysResource> menu_1 = new ArrayList<SysResource>();
            List<SysResource> menu_2 = new ArrayList<SysResource>();
            List<SysResource> menu_3 = new ArrayList<SysResource>();
            List<SysResource> sysResourceList = sysResourceService.list(new QueryWrapper<SysResource>()
                    .orderByAsc("orders")
                    .eq("resType", 1)
                    .eq("enableStatus", 1)
                    .exists(!userBean.getUserName().equals("admin"), "select 1 from sys_role_resource_ref b where sys_resource.id=b.resourceId and b.roleId in (select roleId from sys_user_role_ref where userId='" + userBean.getId() + "') "));
            sysResourceList.stream().forEach(p -> {
                switch (p.getLevels()) {
                    case 1:
                        menu_1.add(p);
                        break;
                    case 2:
                        menu_2.add(p);
                        break;
                    case 3:
                        menu_3.add(p);
                        break;
                }
            });
            menu_2.stream().forEach(p -> {
                List<SysResource> cl = new ArrayList<SysResource>();
                menu_3.forEach(b -> {

                    //gai
                    System.out.println(p.getId()+"测试有没有获得我们定义的ID");


                    if (p.getId().equals(b.getPid())) {
                        cl.add(b);
                    }
                });
                p.setChildList(cl);
            });
            menu_1.stream().forEach(p -> {
                List<SysResource> cl = new ArrayList<SysResource>();
                menu_2.forEach(b -> {
                    if (p.getId().equals(b.getPid())) {
                        cl.add(b);
                    }
                });
                p.setChildList(cl);
            });
            userBean.setSysResourceList(menu_1);
            //角色查询
            List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>().exists("select 1 from sys_user_role_ref b where sys_role.id=b.roleId and b.userId='" + userBean.getId() + "'"));
            userBean.setRoleList(roleList);
            return new SimpleAuthenticationInfo(userBean, new String(token.getPassword()), getName());
        } else {
            throw new AuthenticationException("用户名或者密码错误");
        }
    }

    //角色权限和对应权限添加
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        SysUser sysUser = (SysUser) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //角色授权
        List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>().exists("select 1 from sys_user_role_ref b where sys_role.id=b.roleId and b.userId='" + sysUser.getId() + "'"));
        roleList.stream().forEach(p -> {
            info.addRole(p.getRoleCode());
        });
        //菜单/功能授权
        List<SysResource> sysResourceList = sysResourceService.list(new QueryWrapper<SysResource>()
                .orderByAsc("orders")
                .eq("enableStatus", 1)
                .exists(!sysUser.getUserName().equals("admin"), "select 1 from sys_role_resource_ref b where sys_resource.id=b.resourceId and b.roleId in (select roleId from sys_user_role_ref where userId='" + sysUser.getId() + "') "));
        sysResourceList.stream().forEach(p -> {
            if (StringUtils.isNotBlank(p.getResPermission())) {
                info.addStringPermission(p.getResPermission());
            }
        });
        return info;
    }
}