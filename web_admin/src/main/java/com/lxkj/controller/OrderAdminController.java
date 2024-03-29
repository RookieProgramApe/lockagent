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
import com.lxkj.common.util.ID;
import com.lxkj.common.util.PageData;
import com.lxkj.entity.*;
import com.lxkj.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 提货订单 前端控制器
 * </p>
 * 首页路由：/Order/list
 * @author 一个烧包
 * @since 2019-07-19
 */
@Controller
@RequestMapping("/Order")
@Slf4j
public class OrderAdminController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private GiftcardService giftcardService;
    @Autowired
    private CargoAttachmentService cargoAttachmentService;
    @Autowired
    private RetailerGiftcardService retailerGiftcardService;
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private DeliveryService deliveryService;

    /**
     * 首页
     */
    @RequestMapping("/list")
    public ModelAndView list(ModelAndView model) {
        model.setViewName("/admin/Order/list");
        return model;
    }

    /**
     * 首页-运单导入
     */
    @RequestMapping("/listImport")
    public ModelAndView listImport(ModelAndView model) {
        model.setViewName("/admin/Order/listImport");
        return model;
    }

    @RequestMapping("/toImport")
    public ModelAndView toImport(ModelAndView model) {
        model.setViewName("/admin/Order/import");
        return model;
    }

    /**
     * 首页(物流管理-代发货订单)
     */
    @RequestMapping("/toExport")
    public ModelAndView toExport(ModelAndView model) {
        model.setViewName("/admin/Order/export");
        return model;
    }

    /**
     * 详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public ModelAndView detail(String id,ModelAndView model) {
        Order order = orderService.getById(id);
        model.addObject("Order",order);
        model.addObject("member",null);
        if(StringUtils.isNotBlank(order.getMemberId())){
            Member m = memberService.getById(order.getMemberId());
                model.addObject("member",(m!=null?m:null));
        }
        model.addObject("Giftcard",null);
        if(StringUtils.isNotBlank(order.getGiftcardId())){
            Giftcard gf = giftcardService.getById(order.getGiftcardId());
            RetailerGiftcard rg = retailerGiftcardService.getOne(new QueryWrapper<RetailerGiftcard>().eq("giftcard_id", gf.getId()).eq("status", 0));
            if(rg == null){
                gf.setOccupant("未分配");
            }else {
                Retailer retailer = retailerService.getOne(new QueryWrapper<Retailer>().eq("member_id", rg.getMemberId()));
                gf.setOccupant(retailer.getName());
            }
            model.addObject("Giftcard",(gf!=null?gf:null));
        }
        if(StringUtils.isBlank(order.getCargoImage())){
            if(StringUtils.isNotBlank(order.getCargoId())){
                CargoAttachment ca = cargoAttachmentService.getOne(Wrappers.<CargoAttachment>query().eq("cargo_id",order.getCargoId()).eq("type",1));
                 if(StringUtils.isNotBlank(ca.getUrl())){
                     order.setCargoImage(ca.getUrl());
                 }
            }
        }
        model.setViewName("/admin/Order/detail");
        return model;
    }

    /**
     * 分页列表
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public DataGridModel<Order> pageList(String ordernum,String status, String type) {
        PageData params=this.getPageData();
        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Order>()
                        .like(StringUtils.isNotBlank(ordernum),"ordernum",ordernum)
                        .like(StringUtils.isNotBlank(status),"status",status)
                        .like(StringUtils.isNotBlank(type), "type", type)
                        .orderByDesc("create_time"));
        page.getRecords().stream().forEach(p->{
            if(StringUtils.isNotBlank(p.getMemberId())){
                Member m = memberService.getById(p.getMemberId());
                if(m!=null&&m.getNickname()!=null&&m.getNickname()!=""){
                     p.setMemberId(m.getNickname());
                }else{
                    p.setMemberId("");
                }
            }else{
                p.setMemberId("");
            }
        });
        DataGridModel<Order> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    /**
     * 待发货订单分页列表
     * @return
     */
    @RequestMapping("/pageList1")
    @ResponseBody
    public DataGridModel<Order> pageList1() {
        PageData params=this.getPageData();
        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Order>()
                        .orderByDesc("create_time"));
        DataGridModel<Order> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    /**
     * 快递公司列表
     * @return
     */
    @RequestMapping("/selectDelivery")
    @ResponseBody
    public JsonResults selectDelivery() {
        List<Delivery> deliveryList = deliveryService.list(new QueryWrapper<Delivery>().eq("is_del", 0));
        return BuildSuccessJson(deliveryList,"查询成功");
    }

    /**
     * 导出待发货信息
     * @return
     */
    @RequestMapping("/pageListExpress")
    @ResponseBody
    public DataGridModel<Order> pageListExpress(String keyword) {
        PageData params=this.getPageData();
        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Order>()
                .eq("status",2)
                .and(StringUtils.isNotBlank(keyword),i->i.like("sequence",keyword).or().like("delivery_track",keyword).or().like("cargo_name",keyword).or().like("recipient",keyword))
                .orderByAsc("create_time"));
        DataGridModel<Order> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    /**
     * 导出已发货信息
     * @return
     */
    @RequestMapping("/pageList3")
    @ResponseBody
    public DataGridModel<Order> pageList3() {
        PageData params=this.getPageData();
        IPage<Order> page=orderService.page(new Page<Order>(params.getInteger("page"),params.getInteger("limit")),
                new QueryWrapper<Order>()
                        .eq("status",3)
                        .orderByAsc("create_time"));
        page.getRecords().stream().forEach(p->{
            if(StringUtils.isNotBlank(p.getMemberId())){
                Member m = memberService.getById(p.getMemberId());
                if(m!=null&&m.getNickname()!=null&&m.getNickname()!=""){
                    p.setMemberId(m.getNickname());
                }else{
                    p.setMemberId("");
                }
            }else{
                p.setMemberId("");
            }
        });
        DataGridModel<Order> grid=new DataGridModel(page.getRecords(),page.getTotal());
        return  grid;
    }

    @RequestMapping("/batchImport")
    @ResponseBody
    public JsonResults batchImport( String fileName) throws Exception {
        URL url = new URL(fileName);
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return BuildFailJson("上传文件格式不正确");
        }
        String squ = orderService.batchImport(url.openStream(), ShiroUtils.getUserId());
        if(squ==null){
            return BuildFailJson("导入失败，请联系管理员");
        }
        return BuildSuccessJson("导入完成");
    }

    /**
     * 导出订单信息
     * @param orderType  订单类型
     * @param orderStatus  订单状态
     * @param orderTime  订单起止时间
     */
    @RequestMapping("/exportOrder")
    @ResponseBody
    public void exportOrder(String orderType, String orderStatus, String orderTime) {
        String startTime = "";
        String endTime = "";
        // 截取起止时间
        if(StringUtils.isNotBlank(orderTime.trim())){
            startTime = orderTime.substring(0, 10);
            endTime = orderTime.substring(orderTime.length()-10, orderTime.length());
        }


        String filePath = "static/files/orderTemplate.xls";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filePath);
        if(input==null){
            throw new RuntimeException("模板文件没有找到，请联系管理员");
        }
        List<Order> list = orderService.list(new QueryWrapper<Order>().eq(StringUtils.isNotBlank(orderStatus), "status",orderStatus)
                .eq(StringUtils.isNotBlank(orderType), "type", orderType)
                .between(StringUtils.isNotBlank(orderTime), "create_time", startTime, endTime)
                .orderByDesc("create_time"));
        list.stream().forEach(p->{
            if(StringUtils.isNotBlank(p.getMemberId())){
                Member m = memberService.getById(p.getMemberId());
                if(m!=null&&m.getNickname()!=null&&m.getNickname()!=""){
                    p.setMemberId(m.getNickname());
                }else{
                    p.setMemberId("");
                }
            }else{
                p.setMemberId("");
            }

            // 判断订单类型
            if(p.getType().equals(1)) {
                p.setTypeName("零售订单");
            }else if(p.getType().equals(2)) {
                p.setTypeName("砍价订单");
            }else if(p.getType().equals(3)) {
                p.setTypeName("积分商品订单");
            }else {
                p.setTypeName("其他订单");
            }

            // 判断订单状态
            if(p.getStatus().equals(1)) {
                p.setStatusName("待支付");
            }else if(p.getStatus().equals(2)) {
                p.setStatusName("已支付");
            }else if(p.getStatus().equals(3)) {
                p.setStatusName("已发货");
            }else if(p.getStatus().equals(4)) {
                p.setStatusName("已完成");
            }else {
                p.setStatusName("其他");
            }
        });
        String title = "交易订单";
        String[] keyHeaders = {"memberId","cargoName","ordernum","skuName","typeName","count","totalPrice","statusName","sequence","deliveryTrack","createTime"};
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        String fileName =title+ ID.nextGUID()+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +"."+ suffix;

        OutputStream out=null;
        ByteArrayOutputStream fos=null;
        byte[] retArr = null;
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            fileName=new String(fileName.getBytes("GB2312"),"ISO-8859-1");
            fos = new ByteArrayOutputStream();
            orderService.exportExcel(title,input,list,keyHeaders,fos);//file
            retArr = fos.toByteArray();
            out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);//要保存的文件名
            response.setContentType("application/octet-stream; charset=utf-8");
            out.write(retArr);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出失败");
        }finally{
            try {
                if(out!=null)out.close();
                if(fos!=null)out.close();
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException("导出失败");
            }
        }
    }

    @RequestMapping("/export")
    @ResponseBody
    public void export() {
        String filePath = "static/files/yuantongTemplate.xls";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filePath);
        if(input==null){
            throw new RuntimeException("模板文件没有找到，请联系管理员");
        }
        List<Order> list = orderService.list( new QueryWrapper<Order>().eq("status",2)
                .orderByDesc("create_time"));
//        if(list==null||list.size()==0){
//            throw new RuntimeException("当前没有数据需要导出");
//        }
//        List<Map<String,Object>> addresserlist = jdbcTemplate.queryForList("select * from addresser_message order by create_time desc limit 1") ;
//        if(addresserlist!=null&&addresserlist.size()>0&&addresserlist.get(0)!=null){
//            Map<String,Object> addresser = (Map)addresserlist.get(0);
//            if(addresser==null){
//                throw new RuntimeException("查不到收件人信息，请联系管理员");
//            }else{
//                if(list!=null&&list.size()>0){
//                    list.stream().forEach(p->{
//                        p.setAddresserName((String)addresser.get("addresser"));
//                        p.setAddresserProvince((String)addresser.get("province"));
//                        p.setAddresserCity((String)addresser.get("city"));
//                        p.setAddresserCounty((String)addresser.get("county"));
//                        p.setAddresserAddress((String)addresser.get("address"));
//                        p.setAddresserMobile((String)addresser.get("mobile"));
//                        p.setDeliveryProvider((String)addresser.get("delivery_provider"));
//                    });
//                }
//            }
//        }
        list.stream().forEach(p->{
            p.setAddresserName(p.getProvince()+p.getCity()+p.getCounty()+p.getAddress());
        });
        String title = "待发货商品";
        String[] keyHeaders = {"sequence","cargoName","skuName","count","recipient","province","city","county","address","mobile","addresserName"};
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        String fileName =title+ ID.nextGUID()+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +"."+ suffix;

        OutputStream out=null;
        ByteArrayOutputStream fos=null;
        byte[] retArr = null;
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            fileName=new String(fileName.getBytes("GB2312"),"ISO-8859-1");
            fos = new ByteArrayOutputStream();
            orderService.exportExcel(title,input,list,keyHeaders,fos);//file
            retArr = fos.toByteArray();
            out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);//要保存的文件名
            response.setContentType("application/octet-stream; charset=utf-8");
            out.write(retArr);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出失败");
        }finally{
            try {
                if(out!=null)out.close();
                if(fos!=null)out.close();
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException("导出失败");
            }
        }
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
            model.addObject("Order",orderService.getById(id));
        }else{
            model.addObject("Order",new Order());
        }
        model.setViewName("/admin/Order/add");
        return model;
    }
    /**
     * 跳转添加/编辑物流信息界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toEditDelivery")
    public ModelAndView toEditDelivery(String id,ModelAndView model) {
        if (StringUtils.isNotBlank(id)) {
            model.addObject("Order", orderService.getById(id));
        }else{
            model.addObject("Order",new Order());
        }
        model.setViewName("/admin/Order/editDelivery");
        return model;
    }

    /**
     * 保存物流信息
     * @param
     * @return
     */
    @RequestMapping("/saveDelivery")
    @ResponseBody
    @Transactional
    public JsonResults saveDelivery(Order bean) {
        orderService.update(new UpdateWrapper<Order>().set("delivery_provider", bean.getDeliveryProvider()).set("delivery_track", bean.getDeliveryTrack()).eq("`id`", bean.getId()));
        return BuildSuccessJson("提交成功");
    }


    /**
     * 保存
     * @param
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Transactional
    public JsonResults save(Order bean) {
        if(StringUtils.isNotBlank(bean.getId())){
            orderService.updateById(bean);
        }else{
            orderService.save(bean);
        }
        return BuildSuccessJson("提交成功");
    }

    /**
     * 修改
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public JsonResults update(Order bean){
        if(StringUtils.isBlank(bean.getId())) return BuildFailJson("主键不能为空");
        orderService.updateById(bean);
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
        orderService.removeById(id);
        return BuildSuccessJson("删除成功");
    }

    /**
     * 确认发货
     * @param
     * @return
     */
    @RequestMapping("/confirmOrder")
    @ResponseBody
    @Transactional
    public JsonResults confirmOrder(String id) {
        orderService.update(new UpdateWrapper<Order>().set("`status`", 3).eq("`id`", id));
        return BuildSuccessJson("操作成功");
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
        orderService.removeByIds(Arrays.asList(ids));
        return BuildSuccessJson("删除成功");
    }
}
