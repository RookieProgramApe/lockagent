package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.Member;
import com.lxkj.entity.Order;
import com.lxkj.entity.Retailer;
import com.lxkj.entity.Transaction;
import com.lxkj.mapper.TransactionMapper;
import com.lxkj.service.*;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支付 前端控制器
 * </p>
 * 首页路由：/Transaction/list
 * @author 一个烧包
 * @since 2019-07-26
 */
@Controller
@RequestMapping("/Transaction")
@Slf4j
public class TransactionController extends BaseController {
    @Autowired
    private TransactionService transactionService;
    @Resource
    private TransactionMapper transactionMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private RetailerService retailerService;

    /**
    * 首页
    */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        List<Map<String,Object>> listAmount = jdbcTemplate.queryForList("select sum(amount) as sumAmount from transaction where type=99 and status=1");
        List<Map<String,Object>> listCommission = jdbcTemplate.queryForList("select sum(commission) as sumCommission from transaction where type=99 and status=1");
       if(listAmount!=null&&listAmount.size()>0&&listAmount.get(0)!=null){
           model.addObject("sumAmount",(BigDecimal)listAmount.get(0).get("sumAmount"));
       }else{
           model.addObject("sumAmount",new BigDecimal(0.00));
       }

        if(listCommission!=null&&listCommission.size()>0&&listCommission.get(0)!=null){
            model.addObject("sumCommission",(BigDecimal)listCommission.get(0).get("sumCommission"));
        }else{
            model.addObject("sumCommission",new BigDecimal(0.00));
        }
        model.setViewName("/admin/Transaction/list");
        return model;
    }

    /**
     * 分页列表收益
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Transaction> pageList(String member_id) {
            PageData params=this.getPageData();
            IPage<Transaction> page=transactionService.page(new Page<Transaction>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Transaction>()
                   .eq("member_id",member_id)
                   .eq("status",1)
                   .in("type",80,81)
                    .orderByDesc("create_time"));
            DataGridModel<Transaction> grid=new DataGridModel(page.getRecords(),page.getTotal());
            return  grid;
     }

    /**
     * 分页列表体现
     * @return
     */
    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Transaction> pageList1(String member_id) {
        PageData params=this.getPageData();
        IPage<Transaction> page=transactionService.page(new Page<Transaction>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Transaction>()
                        .eq("member_id",member_id)
                        .eq("status",1)
                        .eq("type",99)
                        .orderByDesc("create_time"));
        DataGridModel<Transaction> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    @RequestMapping("/pageList2")
    @ResponseBody
    public DataGridModel<Map> pageList2(String keyword) {
        PageData params=this.getPageData();
        IPage<Map> page=transactionMapper.pagWithMessage(new Page<Transaction>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Map>()
                        .eq("t.type",99)
                        .like(StringUtils.isNotBlank(keyword),"m.nickname",keyword)
                        .orderByDesc("t.create_time"));
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
            model.addObject("Transaction",transactionService.getById(id));
        }else{
            model.addObject("Transaction",new Transaction());
        }
        model.setViewName("/admin/Transaction/add");
        return model;
    }

    @RequestMapping("/toCheck")
    public ModelAndView toCheck(String id,ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            Transaction t = transactionService.getById(id);
            Member m = memberService.getById(t.getMemberId());
            Order o = orderService.getById(t.getOrderId());
            Retailer r = retailerService.getOne(Wrappers.<Retailer>query().eq("member_id",t.getMemberId()));

            model.addObject("member",m);
            model.addObject("Retailer",r);
            model.addObject("Order",o);
            model.addObject("Transaction",t);
        }else{
            throw new RuntimeException("主数据丢失，请联系管理员");
        }
        model.setViewName("/admin/Transaction/check");
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
    public JsonResults save(Transaction bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            transactionService.updateById(bean);
        }else{
            transactionService.save(bean);
        }
        return BuildSuccessJson("提交成功");
     }

    @RequestMapping("/check")
    @ResponseBody
    @Transactional
    public JsonResults check(Transaction bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            Transaction t=transactionService.getById(bean.getId());
            if(t.getType()==99){
                cardOrderService.finshiTransaction(bean.getId(),bean.getStatus(),bean.getComment());
            }
        }else{
            throw new RuntimeException("主键数据丢失");
        }
        return BuildSuccessJson("审核成功");
    }

    /**
    * 修改
    * @return
    */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(Transaction bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
            transactionService.updateById(bean);
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
        transactionService.removeById(id);
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
        transactionService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
     }
}
