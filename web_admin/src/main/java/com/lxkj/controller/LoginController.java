package com.lxkj.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.entity.CardOrder;
import com.lxkj.entity.Order;
import com.lxkj.entity.Retailer;
import com.lxkj.entity.SysUser;
import com.lxkj.service.CardOrderService;
import com.lxkj.service.MemberService;
import com.lxkj.service.OrderService;
import com.lxkj.service.RetailerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class LoginController extends BaseController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CardOrderService cardOrderService;


    /**
     * 登录界面
     * @return
     */
    @RequestMapping({"/", "login"})
    public String login() {
        SysUser user = ShiroUtils.getSysUser();
        if(user==null){
            return "login";
        }else{
            return "redirect:/index";
        }
    }

    /**
     * 后台首页
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(ModelAndView model) {
        model.addObject("user", ShiroUtils.getSysUser());
        model.setViewName("/index");
        return model;
    }


    /**
     * 首页
     * @param model
     * @return
     */
    @RequestMapping("/home")
    public ModelAndView home(ModelAndView model) {
          //会员数
       Integer countMember = memberService.count();
            model.addObject("countMember",countMember);
        //代理商
        Integer contRetailer = retailerService.count(Wrappers.<Retailer>query().eq("status",1));
            model.addObject("contRetailer",contRetailer);
        // 总营收入
        List<Map<String,Object>> listSum  = jdbcTemplate.queryForList("select sum(total_price) as sumPrice from `order` where status in (2,3,4)");
        if(listSum!=null&&listSum.size()>0&&listSum.get(0)!=null&&listSum.get(0).get("sumPrice")!=null){
            model.addObject("sumPrice",(BigDecimal)listSum.get(0).get("sumPrice"));
        }else{
            model.addObject("sumPrice",new BigDecimal(0.00));
        }
        //卡片订单
        Integer countCardOrder = cardOrderService.count(Wrappers.<CardOrder>query().eq("status",2));
        model.addObject("countCardOrder",countCardOrder);
        //待发货
        Integer countOrder2 = orderService.count(Wrappers.<Order>query().eq("status",2));
        model.addObject("countOrder2",countOrder2);
        //已发货
        Integer countOrder3 = orderService.count(Wrappers.<Order>query().eq("status",3));
        model.addObject("countOrder3",countOrder3);
        //已完成
        Integer countOrder4 = orderService.count(Wrappers.<Order>query().eq("status",4));
        model.addObject("countOrder4",countOrder4);
        model.setViewName("/includes/home");
        return model;
    }


    /**
     * 登录接口
     * @param httpServletRequest
     * @param userName
     * @param passWord
     * @return
     */
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public JsonResults doLogin(HttpServletRequest httpServletRequest,
                               @RequestParam String userName,
                               @RequestParam String passWord) {
        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(userName, passWord);
        // 执行认证登陆
        subject.login(token);
        return BuildSuccessJson("登录成功");
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }


}
