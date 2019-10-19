package com.lxkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.DataGridModel;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.shiro.ShiroUtils;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.*;
import com.lxkj.mapper.CardOrderMapper;
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
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 卡片订单 前端控制器
 * </p>
 * 首页路由：/CardOrder/list
 *
 * @author 一个烧包
 * @since 2019-07-18
 */
@Controller
@RequestMapping("/CardOrder")
@Slf4j
public class CardOrderController extends BaseController {
    @Autowired
    private CardOrderService cardOrderService;
    @Resource
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;
    @Autowired
    private CardOrderFlowFileService cardOrderFlowFileService;
    @Autowired
    private CardOrderFlowService cardOrderFlowService;
    @Autowired
    private GiftcardService giftcardService;
    @Autowired
    private ConfigService configService;

    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        List<Map<String, Object>> listCount = jdbcTemplate.queryForList("select sum(`count`) as sumCount from card_order where status=2 ");
        //设置卡片总数
        BigDecimal sumCount = new BigDecimal(0);
        if (listCount != null && listCount.size() > 0 && listCount.get(0) != null && listCount.get(0).get("sumCount") != null) {
            sumCount = ((BigDecimal) (listCount.get(0).get("sumCount"))).multiply(new BigDecimal(50));
        }
        model.addObject("cardNum", sumCount);

        Integer num = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query());
        Integer num0 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("state", 0).inSql("order_id", "select id from card_order where status=2 "));
        Integer num1 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("state", 1).inSql("order_id", "select id from card_order where status=2 "));
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select sum(total_price) as sumPrice from card_order where status=2 and retailstate=1");
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).get("sumPrice") != null) {
            model.addObject("sumPrice", (BigDecimal) list.get(0).get("sumPrice"));
        } else {
            model.addObject("sumPrice", new BigDecimal(0.00));
        }
        //未分配
        model.addObject("cardNum3", sumCount.subtract(new BigDecimal(num)));
        //已分配
        model.addObject("cardNum4", num);
        //已分配未使用
        model.addObject("cardNum0", num0);
        //已使用
        model.addObject("cardNum1", num1);
        model.setViewName("/admin/CardOrder/list");
        return model;
    }


    /**
     * 首页
     */
    @RequestMapping("/list2")
    public ModelAndView list2(ModelAndView model) {
        List<Map<String, Object>> listCount = jdbcTemplate.queryForList("select sum(`count`) as sumCount from card_order where status=2 ");
        //设置卡片总数
        BigDecimal sumCount = new BigDecimal(0);
        if (listCount != null && listCount.size() > 0 && listCount.get(0) != null && listCount.get(0).get("sumCount") != null) {
            sumCount = ((BigDecimal) (listCount.get(0).get("sumCount"))).multiply(new BigDecimal(50));
        }
        model.addObject("cardNum", sumCount);

        Integer num = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query());
        Integer num0 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("state", 0).inSql("order_id", "select id from card_order where status=2 "));
        Integer num1 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("state", 1).inSql("order_id", "select id from card_order where status=2 "));
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select sum(total_price) as sumPrice from card_order where status=2 and retailstate=1");
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).get("sumPrice") != null) {
            model.addObject("sumPrice", (BigDecimal) list.get(0).get("sumPrice"));
        } else {
            model.addObject("sumPrice", new BigDecimal(0.00));
        }
        //未分配
        model.addObject("cardNum3", sumCount.subtract(new BigDecimal(num)));
        //已分配
        model.addObject("cardNum4", num);
        //已分配未使用
        model.addObject("cardNum0", num0);
        //已使用
        model.addObject("cardNum1", num1);
        model.setViewName("/admin/CardOrder/list2");
        return model;
    }


    @RequestMapping("/toCheck")
    public ModelAndView toCheck(String id, ModelAndView model) {
        model.addObject("id", id);
        model.setViewName("/admin/CardOrder/check");
        return model;
    }

    @RequestMapping("/toCheck2")
    public ModelAndView toCheck2(String id, ModelAndView model) {
        var cardOrderFlowlist = cardOrderFlowService.list(new QueryWrapper<CardOrderFlow>().eq("card_order_id", id).eq("lb",1).eq("state", "初审通过").orderByDesc("create_time"));
        if (cardOrderFlowlist.isEmpty()) {
            model.addObject("cardOrderFlow", null);
        } else {
            var cardOrderFlow = cardOrderFlowlist.get(0);
            var user = new SysUser().selectById(cardOrderFlow.getCreateBy());
            cardOrderFlow.setRealName(user.getRealName() + "(" + user.getUserName() + ")");
            cardOrderFlow.setMobile(user.getMobile());
            cardOrderFlow.setFileList(cardOrderFlowFileService.list(new QueryWrapper<CardOrderFlowFile>().eq("card_order_flow_id", cardOrderFlow.getId())));
            model.addObject("cardOrderFlow", cardOrderFlow);
        }
        model.addObject("id", id);
        model.setViewName("/admin/CardOrder/check2");
        return model;
    }

    /**
     * 分页列表
     *
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<CardOrder> pageList() {
        PageData params = this.getPageData();
        IPage<CardOrder> page = cardOrderService.page(new Page<CardOrder>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<CardOrder>()
                        .orderByDesc("create_time"));
        DataGridModel<CardOrder> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 分页列表带会员信息和代理商信息
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Map> pageList1(String keyword) {
        PageData params = this.getPageData();
        String status = params.getString("status");
        IPage<Map> page = cardOrderMapper.cardOrderPage(new Page<CardOrder>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<CardOrder>()
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("r.name", keyword).or().like("r.phone", keyword))
                        .eq(StringUtils.isNotBlank(status), "co.status", params.getInteger("status"))
                        .orderByDesc("co.create_time"));
        DataGridModel<Map> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    @RequestMapping("/pageList2")
    @ResponseBody
    public DataGridModel<Map> pageList2(String keyword) {
        PageData params = this.getPageData();
        IPage<Map> page = cardOrderMapper.cardOrderPage(new Page<CardOrder>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<CardOrder>()
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("r.name", keyword).or().like("r.phone", keyword))
                        .in("co.status", 4, 2)
                        .orderByAsc("co.status")
                        .orderByDesc("co.create_time"));
        DataGridModel<Map> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 跳转添加/编辑界面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(String id, ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("CardOrder", cardOrderService.getById(id));
        } else {
            model.addObject("CardOrder", new CardOrder());
        }
        model.setViewName("/admin/CardOrder/add");
        return model;
    }

    @RequestMapping("/detail")
    public ModelAndView detail(String id, ModelAndView model) {
        CardOrder co = cardOrderService.getById(id);
        model.addObject("CardOrder", null);
        model.addObject("Member", null);
        model.addObject("Retailer", null);
        if (co != null) {
            co.setMobile(ShiroUtils.getPhone(co.getMobile()));
            model.addObject("CardOrder", co);
            if (StringUtils.isNotBlank(co.getMemberId())) {
                Member m = memberService.getById(co.getMemberId());
                if (m != null) {
                    model.addObject("Member", m);
                }
            }
            if (StringUtils.isNotBlank(co.getRetailerId())) {
                Retailer r = retailerService.getById(co.getRetailerId());
                if (r != null) {
                    r.setPhone(ShiroUtils.getPhone(r.getPhone()));
                    r.setIdentity(ShiroUtils.getIdCard(r.getIdentity()));
                    model.addObject("Retailer", r);
                }
            }
        }
        Integer num = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("order_id", id));
        Integer num0 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("order_id", id).eq("state", 0));
        Integer num1 = retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("order_id", id).eq("state", 1));
//        List<Map<String,Object>> list = jdbcTemplate.queryForList("select sum(total_price) as sumPrice from card_order where status=2 ");
//        if(list!=null&&list.size()>0&&list.get(0)!=null&&list.get(0).get("sumPrice")!=null){
//            model.addObject("sumPrice",(BigDecimal)list.get(0).get("sumPrice"));
//        }else{
//            model.addObject("sumPrice",new BigDecimal(0.00));
//        }

        //卡片总数
        var count = giftcardService.count(
                new QueryWrapper<Giftcard>()
                        .inSql("id", "select rg.giftcard_id from retailer_giftcard  rg where rg.order_id='" + co.getId() + "'")
        );

        model.addObject("cardNum", count);
        //卡片总数未分配
        model.addObject("cardNum3", count - num);
        //卡片总数已分配
        model.addObject("cardNum4", num);
        //卡片总数未使用
        model.addObject("cardNum0", num0);
        //卡片总数已使用
        model.addObject("cardNum1", num1);

        model.addObject("id", id);
        model.setViewName("/admin/CardOrder/detail");
        return model;
    }

    /**
     * 保存
     *
     * @param
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(CardOrder bean) {
        if (StringUtils.isNotBlank(bean.getId())) {
            cardOrderService.updateById(bean);
        } else {
            cardOrderService.save(bean);
        }
        return BuildSuccessJson("提交成功");
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(CardOrder bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        cardOrderService.updateById(bean);
        return BuildSuccessJson("修改成功");
    }

    /**
     * 初审
     *
     * @param bean
     * @return
     */
    @RequestMapping("/check")
    @ResponseBody
    @Transactional
    public JsonResults check(CardOrder bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        CardOrder bean1 = cardOrderService.getById(bean.getId());
        if (bean1 == null) {
            return BuildFailJson("主键查询结果不能为空");
        } else {
            bean1.setStatus(bean.getStatus());
            bean1.setReson(bean.getReson());
        }
        cardOrderService.update(new UpdateWrapper<CardOrder>().set("status", bean.getStatus()).eq("id", bean1.getId()));
        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        flow.setCardOrderId(bean.getId());
        flow.setState(bean.getStatus() == 4 ? "初审通过" : "拒绝");
        flow.setRemark(bean.getStatus() == 4 ? bean.getRemark() : bean.getReson());
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.insert();
        String files = bean.getFileUrl();
        if (StringUtils.isNotBlank(files)) {
            String[] strs = files.split(",");
            Arrays.stream(strs).forEach(p -> {
                CardOrderFlowFile file = new CardOrderFlowFile();
                cardOrderFlowFileService.save(
                        file.setCardOrderFlowId(flow.getId()).setImageurl(p));
            });
        }
        return BuildSuccessJson("审核成功");
    }

    /**
     * 终审
     *
     * @param bean
     * @return
     */
    @RequestMapping("/endCheck")
    @ResponseBody
    public JsonResults endCheck(CardOrder bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        CardOrder bean1 = cardOrderService.getById(bean.getId());
        if (bean1 == null) {
            return BuildFailJson("主键查询结果不能为空");
        } else {
            bean1.setStatus(bean.getStatus());
            bean1.setReson(bean.getReson());
        }
        if (bean.getStatus() == 2) {//通过
            cardOrderService.finshCardOrders(bean1.getId(), bean.getRetailstate());
        } else {
            cardOrderService.update(new UpdateWrapper<CardOrder>()
                    .set(StringUtils.isNotBlank(bean.getReson()), "reson", bean.getReson())
                    .set("status", bean.getStatus())
                    .eq("id", bean1.getId()));
        }
        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        if (bean.getStatus() == 2) {
            flow.setState("终审通过");
            flow.setRemark(bean.getRemark());
        } else if (bean.getStatus() == 3) {
            flow.setState("拒绝");
            flow.setRemark(bean.getReson());
        } else if (bean.getStatus() == 1) {
            flow.setState("打回重审");
            flow.setRemark(bean.getRemark());
        }
        flow.setCardOrderId(bean.getId());
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.insert();
        return BuildSuccessJson("审核成功");
    }

    /**
     * 单个删除
     *
     * @param
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResults delete(String id) {
        cardOrderService.removeById(id);
        return BuildSuccessJson("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteForList")
    @ResponseBody
    @Transactional
    public JsonResults deleteForList(@RequestParam(value = "ids[]") String[] ids) {
        cardOrderService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }


    //添加卡片订单
    @RequestMapping("/buyAdminCard")
    @ResponseBody
    @Transactional
    public JsonResults<?> buyAdminCard(CardOrder co) {
        Retailer retailer = this.retailerService.getById(co.getRetailerId());
        Integer totalCount = cardOrderService.queryCount();//剩余库存
        if (co.getCount() > totalCount) {
            return BuildFailJson("卡片库存不足，请联系平台工作人员");
        }
        co.setMemberId(retailer.getMemberId());
        co.setRetailerId(retailer.getId());
        BigDecimal unitPrice = this.configService.queryForDecimal("card_price");
        co.setTotalPrice(unitPrice.multiply(new BigDecimal(co.getCount())));
        co.setStatus(1);
        String nextSequence = this.jdbcTemplate.queryForObject("select next_sequence_text('delivery') from dual", String.class);
        co.setSequence(nextSequence);
        co.setLb(1);
        this.cardOrderService.save(co);
        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        flow.setState("新增订单");
        flow.setRemark("平台管理人员代下单");
        flow.setCardOrderId(co.getId());
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.insert();
        return BuildSuccessJson("提交成功，赶紧去审核吧");
    }



    @RequestMapping("/toSelectUser")
    public ModelAndView toSelectUser(ModelAndView model) {
        model.setViewName("/admin/CardOrder/selectUser");
        return model;
    }
}
