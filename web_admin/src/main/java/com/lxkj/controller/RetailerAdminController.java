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
import com.lxkj.service.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理商信息 前端控制器
 * </p>
 * 首页路由：/Retailer/list
 *
 * @author 一个烧包
 * @since 2019-07-16
 */
@Controller
@RequestMapping("/Retailer")
@Slf4j
public class RetailerAdminController extends BaseController {
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberSubordinateService memberSubordinateService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;
    @Autowired
    private CardOrderFlowFileService cardOrderFlowFileService;
    @Autowired
    private CardOrderFlowService cardOrderFlowService;
    @Autowired
    private WXMessageService wxMessageService;

    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Retailer/list");
        return model;
    }

    @RequestMapping("/list1")
    public ModelAndView list1(ModelAndView model) {
        model.setViewName("/admin/Retailer/list1");
        return model;
    }

    /**
     * 合伙人管理首页
     * @param model
     * @return
     */
    @RequestMapping("/list2")
    public ModelAndView list2(ModelAndView model) {
        model.setViewName("/admin/Retailer/storeList");
        return model;
    }

    /**
     * 商家管理首页
     * @param model
     * @return
     */
    @RequestMapping("/list3")
    public ModelAndView list3(ModelAndView model) {
        model.setViewName("/admin/Retailer/partnerList");
        return model;
    }

    /**
     * 合伙人审核首页
     * @param model
     * @return
     */
    @RequestMapping("/list4")
    public ModelAndView list4(ModelAndView model) {
        model.setViewName("/admin/Retailer/partnerCheckList");
        return model;
    }



    /**
     * 跳转审核页面
     *
     * @param id
     * @param memberId
     * @param model
     * @return
     */
    @RequestMapping("/toCheck")
    public ModelAndView toCheck(String id, String memberId, ModelAndView model) {
        model.addObject("id", id);
        model.addObject("memberId", memberId);
        var cardOrderFlowlist = cardOrderFlowService.list(new QueryWrapper<CardOrderFlow>().eq("card_order_id", id).eq("lb",2).eq("state", "初审通过").orderByDesc("create_time"));
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
        model.setViewName("/admin/Retailer/check");
        return model;
    }

    /**
     * 合伙人跳转审核页面
     *
     * @param id
     * @param memberId
     * @param model
     * @return
     */
    @RequestMapping("/toCheck2")
    public ModelAndView toCheck2(String id, String memberId, ModelAndView model) {
        model.addObject("id", id);
        model.addObject("memberId", memberId);
        var cardOrderFlowlist = cardOrderFlowService.list(new QueryWrapper<CardOrderFlow>().eq("card_order_id", id).eq("lb",2).eq("state", "初审通过").orderByDesc("create_time"));
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
        model.setViewName("/admin/Retailer/check2");
        return model;
    }

    @RequestMapping("/toCheck1")
    public ModelAndView toCheck1(String id, String memberId, ModelAndView model) {
        model.addObject("id", id);
        model.addObject("memberId", memberId);
        model.setViewName("/admin/Retailer/check1");
        return model;
    }

    @RequestMapping("/toSetGiftcard")
    public ModelAndView toSet(String id, ModelAndView model) {
        model.addObject("Retailer", retailerService.getById(id));
        model.setViewName("/admin/Retailer/setGiftcardReward");
        return model;
    }

    @RequestMapping("/toCargoList")
    public ModelAndView toCargoList(String id, ModelAndView model) {
        model.addObject("id", id);
        model.setViewName("/admin/Retailer/cargoList");
        return model;
    }

    /**
     * 根据id获取代理商信息
     *
     * @return
     */
    @RequestMapping("/getRetailerById")
    @ResponseBody
    @Transactional
    public JsonResults getRetailerById(String retailerId) {
        if (StringUtils.isBlank(retailerId)) return BuildFailJson("主键不能为空");
        return BuildSuccessJson(retailerService.getById(retailerId), "查询成功");
    }

    /**
     * 商家分页列表
     *
     * @return
     */
    @RequestMapping("/storeList")
    @ResponseBody
    public DataGridModel<Retailer> storeList(String keyword) {
        PageData params = this.getPageData();
        IPage<Retailer> page = retailerService.page(new Page<Retailer>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Retailer>()
                        .eq("`type`", 3)
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("name", keyword).or().like("identity", keyword).or().like("phone", keyword))
                        .in("status", 1, 2)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            int i = jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id=?", Integer.class, p.getMemberId());
            p.setSubordinateCount(i);
        });
        DataGridModel<Retailer> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 合伙人分页列表
     *
     * @return
     */
    @RequestMapping("/partnerList")
    @ResponseBody
    public DataGridModel<Retailer> partnerList(String keyword) {
        PageData params = this.getPageData();
        String status = params.getString("status");
        IPage<Retailer> page = retailerService.page(new Page<Retailer>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Retailer>()
                        .eq("`type`", 2)
                        .eq("status", 1)
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("name", keyword).or().like("identity", keyword).or().like("phone", keyword))
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            int i = jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id=?", Integer.class, p.getMemberId());
            p.setSubordinateCount(i);
        });
        DataGridModel<Retailer> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 合伙人审核列表
     *
     * @return
     */
    @RequestMapping("/partnerCheckList")
    @ResponseBody
    public DataGridModel<Retailer> partnerCheckList(String keyword) {
        PageData params = this.getPageData();
        String status = params.getString("status");
        IPage<Retailer> page = retailerService.page(new Page<Retailer>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Retailer>()
                        .eq("`type`", 2)
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("name", keyword).or().like("identity", keyword).or().like("phone", keyword))
                        .in("status", 0, 1, 2, 99)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            int i = jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id=?", Integer.class, p.getMemberId());
            p.setSubordinateCount(i);
        });
        DataGridModel<Retailer> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 终审-分页列表
     *
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Retailer> pageList(String keyword) {
        PageData params = this.getPageData();
        String status = params.getString("status");
        IPage<Retailer> page = retailerService.page(new Page<Retailer>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Retailer>()
                        .eq("`type`", 1)
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("name", keyword).or().like("identity", keyword).or().like("phone", keyword))
                        .in("status", 1, 2)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            int i = jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id=?", Integer.class, p.getMemberId());
            p.setSubordinateCount(i);
        });
        DataGridModel<Retailer> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 初审-分页列表
     *
     * @return
     */
    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Retailer> pageList1(String keyword) {
        PageData params = this.getPageData();
        String status = params.getString("status");
        IPage<Retailer> page = retailerService.page(new Page<Retailer>(params.getInteger("page"), params.getInteger("limit")),
                new QueryWrapper<Retailer>()
                        .eq("`type`", 1)
                        .nested(StringUtils.isNotBlank(keyword), i -> i.like("name", keyword).or().like("identity", keyword).or().like("phone", keyword))
                        .eq(StringUtils.isNotBlank(status), "status", params.getInteger("status"))
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p -> {
            int i = jdbcTemplate.queryForObject("select count(1) from retailer where parent_member_id=?", Integer.class, p.getMemberId());
            p.setSubordinateCount(i);
        });
        DataGridModel<Retailer> grid = new DataGridModel(page.getRecords(), page.getTotal());
        return grid;
    }

    /**
     * 代理商下级列表
     *
     * @return
     */
    @RequestMapping("/subordinateList")
    @ResponseBody
    public DataGridModel<Retailer> subordinateList(String memberId) {
        List<Map<String, Object>> list = memberSubordinateService.querySubordinateByLevel(memberId);
        DataGridModel<Retailer> grid = new DataGridModel(list, Long.valueOf(list.size()));
        return grid;
    }

    /**
     * 代理商下级列表
     *
     * @return
     */
    @RequestMapping("/subordinateList1")
    @ResponseBody
    public DataGridModel<Retailer> subordinateList1(String memberId, Integer isretailer) {
        List<Map<String, Object>> list = memberSubordinateService.querySubordinateByLevel1(memberId, isretailer);
        DataGridModel<Retailer> grid = new DataGridModel(list, Long.valueOf(list.size()));
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
            model.addObject("Retailer", retailerService.getById(id));
        } else {
            model.addObject("Retailer", new Retailer());
        }
        model.setViewName("/admin/Retailer/add");
        return model;
    }

    /**
     * 详情
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id, ModelAndView model) {
        Retailer r = retailerService.getById(id);
        r.setPhone(ShiroUtils.getPhone(r.getPhone()));
        r.setIdentity(ShiroUtils.getIdCard(r.getIdentity()));
        model.addObject("Retailer", r);
        if (StringUtils.isNotBlank(r.getMemberId())) {
            var m = memberService.getById(r.getMemberId());
            model.addObject("member", m);
            model.addObject("memberId", r.getMemberId());
        } else {
            model.addObject("member", null);
            model.addObject("memberId", "");
        }
        if (StringUtils.isNotBlank(r.getParentMemberId())) {
            var m = memberService.getById(r.getParentMemberId());
            var rmember = retailerService.getById(m.getRetailerId());
            model.addObject("Parent", m);

            rmember.setPhone(ShiroUtils.getPhone(rmember.getPhone()));
            rmember.setIdentity(ShiroUtils.getIdCard(rmember.getIdentity()));
            model.addObject("rmember", rmember);
        } else {
            model.addObject("Parent", null);
            model.addObject("rmember", null);
        }
        //代理商卡片总数
        model.addObject("cardNum", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId())));
        //代理商未使用卡片总数
        model.addObject("cardNum0", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("state", 0)));
        //代理商卡已使用片总数
        model.addObject("cardNum1", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("state", 1)));
        //代理商轻奢卡数量
        model.addObject("cardNum2", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("`type`", 1)));
        //代理商贵族卡数量
        model.addObject("cardNum3", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("`type`", 2)));
        //代理商至尊卡数量
        model.addObject("cardNum4", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("`type`", 3)));
        //代理商已分配数量
        model.addObject("cardNum5", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("`status`", 1)));
        //代理商未分配数量
        model.addObject("cardNum6", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("`status`", 0)));
        //代理商card_order购买总金额
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select ifnull(sum(total_price),0) as sumPrice from card_order where member_id=? and status=2", r.getMemberId());
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).get("sumPrice") != null) {
            model.addObject("sumPrice", list.get(0).get("sumPrice"));
        } else {
            model.addObject("sumPrice", new BigDecimal(0.00));
        }
        //支付transaction收益总金额
        List<Map<String, Object>> list1 = jdbcTemplate.queryForList("select ifnull(sum(amount),0) as sum80 from transaction where status=1 and type in (80,81) and member_id=?", r.getMemberId());
        if (list1 != null && list1.size() > 0 && list1.get(0) != null && list1.get(0).get("sum80") != null) {
            model.addObject("sum80", list1.get(0).get("sum80"));
        } else {
            model.addObject("sum80", new BigDecimal(0.00));
        }
        //支付transaction收益总金额
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList("select sum(amount) as sum99 from transaction where status=1 and type=99 and member_id=?", r.getMemberId());
        if (list2 != null && list2.size() > 0 && list2.get(0) != null && list2.get(0).get("sum99") != null) {
            model.addObject("sum99", list2.get(0).get("sum99"));
        } else {
            model.addObject("sum99", new BigDecimal(0.00));
        }
        model.setViewName("/admin/Retailer/detail");
        return model;
    }

    /**
     * 详情
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/storeDetail")
    public ModelAndView storeDetail(String id, ModelAndView model) {
        Retailer r = retailerService.getById(id);
        r.setPhone(ShiroUtils.getPhone(r.getPhone()));
        r.setIdentity(ShiroUtils.getIdCard(r.getIdentity()));
        model.addObject("Retailer", r);
        if (StringUtils.isNotBlank(r.getMemberId())) {
            var m = memberService.getById(r.getMemberId());
            model.addObject("member", m);
            model.addObject("memberId", r.getMemberId());
        } else {
            model.addObject("member", null);
            model.addObject("memberId", "");
        }
        if (StringUtils.isNotBlank(r.getParentMemberId())) {
            var m = memberService.getById(r.getParentMemberId());
            var rmember = retailerService.getById(m.getRetailerId());
            model.addObject("Parent", m);

            rmember.setPhone(ShiroUtils.getPhone(rmember.getPhone()));
            rmember.setIdentity(ShiroUtils.getIdCard(rmember.getIdentity()));
            model.addObject("rmember", rmember);
        } else {
            model.addObject("Parent", null);
            model.addObject("rmember", null);
        }
        //代理商卡片总数
        model.addObject("cardNum", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId())));
        //代理商未使用卡片总数
        model.addObject("cardNum0", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("state", 0)));
        //代理商卡已使用片总数
        model.addObject("cardNum1", retailerGiftcardService.count(Wrappers.<RetailerGiftcard>query().eq("member_id", r.getMemberId()).eq("state", 1)));
        //代理商card_order购买总金额
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select ifnull(sum(total_price),0) as sumPrice from card_order where member_id=? and status=2", r.getMemberId());
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).get("sumPrice") != null) {
            model.addObject("sumPrice", list.get(0).get("sumPrice"));
        } else {
            model.addObject("sumPrice", new BigDecimal(0.00));
        }
        //支付transaction收益总金额
        List<Map<String, Object>> list1 = jdbcTemplate.queryForList("select ifnull(sum(amount),0) as sum80 from transaction where status=1 and type in (80,81) and member_id=?", r.getMemberId());
        if (list1 != null && list1.size() > 0 && list1.get(0) != null && list1.get(0).get("sum80") != null) {
            model.addObject("sum80", list1.get(0).get("sum80"));
        } else {
            model.addObject("sum80", new BigDecimal(0.00));
        }
        //支付transaction收益总金额
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList("select sum(amount) as sum99 from transaction where status=1 and type=99 and member_id=?", r.getMemberId());
        if (list2 != null && list2.size() > 0 && list2.get(0) != null && list2.get(0).get("sum99") != null) {
            model.addObject("sum99", list2.get(0).get("sum99"));
        } else {
            model.addObject("sum99", new BigDecimal(0.00));
        }
        model.setViewName("/admin/Retailer/storeDetail");
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
    public JsonResults save(Retailer bean) {
        if (StringUtils.isNotBlank(bean.getId())) {
            retailerService.updateById(bean);
        } else {
            retailerService.save(bean);
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
    public JsonResults update(Retailer bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        retailerService.updateById(bean);
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
    public JsonResults check(Retailer bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        Retailer bean1 = retailerService.getById(bean.getId());
        retailerService.update(new UpdateWrapper<Retailer>().set("status", bean.getStatus()).eq("id", bean1.getId()));
        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        flow.setCardOrderId(bean.getId());
        flow.setState(bean.getStatus() == 2 ? "初审通过" : "拒绝");
        flow.setRemark(bean.getStatus() == 2 ? bean.getRemark() : bean.getApprovalComment());
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.setLb(2);
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
    @Transactional
    public JsonResults endCheck(Retailer bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        var retailer = retailerService.getById(bean.getId());
        if (bean.getStatus() == 1) {//通过
            //添加公告
            String gender = retailer.getGender().equals("男") ? "先生" : "女士";
            String cnts = "恭喜来自" + retailer.getCity().split(" ")[0] + retailer.getCity().split(" ")[1] + "的" + retailer.getName().substring(0, 1) + gender + "成功加入代理";
            new Bulletin().setSort(new Bulletin().selectCount(null) + 1).setContent(cnts).insert();
            //审核通过
            memberService.updateById(memberService.getById(retailer.getMemberId()).setIsretailer(1).setRetailerId(bean.getId()));
        }
        retailerService.update(new UpdateWrapper<Retailer>()
                .set(StringUtils.isNotBlank(bean.getApprovalComment()), "approval_comment", bean.getApprovalComment())
                .set("status", bean.getStatus())
                .eq("id", retailer.getId()));

        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        if (bean.getStatus() == 1) {
            flow.setState("终审通过");
            flow.setRemark(bean.getRemark());
        } else if (bean.getStatus() == 99) {
            flow.setState("拒绝");
            flow.setRemark(bean.getApprovalComment());
        } else if (bean.getStatus() == 0) {
            flow.setState("打回重审");
            flow.setRemark(bean.getRemark());
        }
        flow.setCardOrderId(retailer.getId());
        flow.setLb(2);
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.insert();
        // 发送申请成功通知
        wxMessageService.sendRetailerMessage(retailer.getId());

        // 代理商裂变收益
        if(retailer.getType().equals(1)){
            // 事业合伙人 最多两层裂变
            if(StringUtils.isNotBlank(retailer.getParentMemberId())) {
                // 一层裂变
                Member parent = memberService.getById(retailer.getParentMemberId());
                Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));
                Transaction transaction = new Transaction().setAmount(bean.getFirstReward().multiply(bean.getAmount()).divide(new BigDecimal(100))).setMemberId(retailer.getParentMemberId()).setOrderId(retailer.getMemberId()).setStatus(1).setType(82);
                transaction.insert();
                Map<String, Object> map = new HashedMap();
                map.put("openId", parent.getOpenId());
                map.put("type", retailer1.getType());
                map.put("retailerName", retailer.getName());
                map.put("money", transaction.getAmount());
                map.put("time", new Date());
                map.put("banlance", retailer1.getBalance().add(transaction.getAmount()));
                wxMessageService.lbSendMessage(map);
                retailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer1.getBalance().add(transaction.getAmount())).eq("id", retailer1.getId()));

                if(StringUtils.isNotBlank(retailer1.getParentMemberId())) {
                    // 获取直属领导
                    Retailer retailer2 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer1.getParentMemberId()));
                    parent = memberService.getById(retailer1.getParentMemberId());
                    // 二层裂变
                    Transaction transaction1 = new Transaction().setAmount(bean.getSecondReward().multiply(bean.getAmount()).divide(new BigDecimal(100))).setMemberId(retailer1.getParentMemberId()).setOrderId(retailer.getMemberId()).setStatus(1).setType(82);
                    transaction1.insert();
                    map = new HashedMap();
                    map.put("openId", parent.getOpenId());
                    map.put("type", retailer2.getType());
                    map.put("retailerName", retailer.getName());
                    map.put("money", transaction1.getAmount());
                    map.put("time", new Date());
                    map.put("banlance", retailer2.getBalance().add(transaction1.getAmount()));
                    wxMessageService.lbSendMessage(map);
                    retailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer2.getBalance().add(transaction1.getAmount())).eq("id", retailer2.getId()));
                }
            }

        }
        return BuildSuccessJson("修改成功");
    }

    /**
     * 终审
     *
     * @param bean
     * @return
     */
    @RequestMapping("/endCheck2")
    @ResponseBody
    @Transactional
    public JsonResults endCheck2(Retailer bean) {
        if (StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        var retailer = retailerService.getById(bean.getId());
        if (bean.getStatus() == 1) {//通过
            //添加公告
            String gender = retailer.getGender().equals("男") ? "先生" : "女士";
            String cnts = "恭喜来自" + retailer.getCity().split(" ")[0] + retailer.getCity().split(" ")[1] + "的" + retailer.getName().substring(0, 1) + gender + "成功加入代理";
            new Bulletin().setSort(new Bulletin().selectCount(null) + 1).setContent(cnts).insert();
            //审核通过 会员身份设为合伙人
            memberService.update(new UpdateWrapper<Member>().set("isretailer", 2).eq("id", retailer.getMemberId()));
        }
        retailerService.update(new UpdateWrapper<Retailer>()
                .set(StringUtils.isNotBlank(bean.getApprovalComment()), "approval_comment", bean.getApprovalComment())
                .set("status", bean.getStatus())
                .eq("id", retailer.getId()));

        //流程处理
        CardOrderFlow flow = new CardOrderFlow();
        if (bean.getStatus() == 1) {
            flow.setState("终审通过");
            flow.setRemark(bean.getRemark());
        } else if (bean.getStatus() == 99) {
            flow.setState("拒绝");
            flow.setRemark(bean.getApprovalComment());
        } else if (bean.getStatus() == 0) {
            flow.setState("打回重审");
            flow.setRemark(bean.getRemark());
        }
        flow.setCardOrderId(retailer.getId());
        flow.setLb(2);
        flow.setCreateBy(ShiroUtils.getUserId());
        flow.insert();

        // 发送申请成功通知
        wxMessageService.sendRetailerMessage(retailer.getId());
        if(retailer.getType().equals(2)) {
            // 合伙人 一层裂变
            if(StringUtils.isNotBlank(retailer.getParentMemberId())) {
                Member parent = memberService.getById(retailer.getParentMemberId());
                Retailer retailer1 = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", retailer.getParentMemberId()));
                Transaction transaction = new Transaction().setAmount(bean.getFirstReward().multiply(bean.getAmount()).divide(new BigDecimal(100))).setMemberId(retailer.getParentMemberId()).setOrderId(retailer.getMemberId()).setStatus(1).setType(82);
                transaction.insert();
                Map<String, Object> map = new HashedMap();
                map.put("openId", parent.getOpenId());
                map.put("type", retailer1.getType());
                map.put("retailerName", retailer.getName());
                map.put("money", transaction.getAmount());
                map.put("time", new Date());
                map.put("banlance", retailer1.getBalance().add(transaction.getAmount()));
                wxMessageService.lbSendMessage(map);

                retailerService.update(new UpdateWrapper<Retailer>().set("balance", retailer1.getBalance().add(transaction.getAmount())).eq("id", retailer1.getId()));
            }
        }
        return BuildSuccessJson("修改成功");
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
        retailerService.removeById(id);
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
        retailerService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }
}
