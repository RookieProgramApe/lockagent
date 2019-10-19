package com.lxkj.controller;


import com.lxkj.common.bean.BaseController;
import com.lxkj.service.SysUserRoleRefService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 用户ID-角色ID 前端控制器
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Controller
@RequestMapping("/SysUserRoleRef")
@Slf4j
public class SysUserRoleRefController extends BaseController {
    @Autowired
    private SysUserRoleRefService sysUserRoleRefService;

}
