package com.lxkj.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.lxkj.common.util.DateUtil;
import com.lxkj.common.util.IDGenerator;
import com.lxkj.common.util.collection.Maps;
import com.lxkj.entity.InstallerOrder;
import com.lxkj.entity.Order;
import com.lxkj.facade.SuningService;
import com.lxkj.service.CardOrderService;
import com.lxkj.service.InstallerOrderService;
import com.lxkj.service.OrderService;
import com.lxkj.vo.orderRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
@Slf4j
public class LianpayCallback {
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private InstallerOrderService installerOrderService;
    @Autowired
    private SuningService suningService;
    /**
     * 连连支付回调
     * @param request
     * @return
     */
    @PostMapping("/callback")
    @Transactional
    public Map<String, String> doCallback(HttpServletRequest request) {
        try {
            String result = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            orderRes param=JSONObject.parseObject(result,orderRes.class);
            String result_pay = param.getResult_pay();
            String no_order = param.getNo_order();
            var o = orderService.getOne(new QueryWrapper<Order>().eq("ordernum", no_order));
            log.info(param.toString());
            if ("SUCCESS".equals(result_pay)) {//成功
                this.cardOrderService.finishOrder(no_order);
                if (o != null) {
                    orderService.updateById(new Order().setId(o.getId()).setPaycnts(JSONObject.toJSONString(param)));
                }
            } else {//失败
                if (o != null) {
                    orderService.updateById(new Order().setStatus(9).setId(o.getId()).setPaycnts(JSONObject.toJSONString(param)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Maps.<String, String>builder()
                .put("ret_code", "0000")
                .put("ret_msg", "交易成功")
                .build();
    }

    @PostMapping("/callback2")
    @Transactional
    public Map<String, String> doCallback2(HttpServletRequest request) {
        try {
            String result = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            orderRes param=JSONObject.parseObject(result,orderRes.class);
            String result_pay = param.getResult_pay();
            String no_order = param.getNo_order();
            var o = installerOrderService.getOne(new QueryWrapper<InstallerOrder>().eq("orderNo", no_order));
            log.info(param.toString());
            if ("SUCCESS".equals(result_pay)) {//成功
                String serviceTime= DateUtil.DateToString(new Date(),"yyyyMMdd") +"180000";
                var order=orderService.getById(o.getOrderId());
                //查询国标码-区
                String code="340111";
                List<Map<String, Object>> res1 = jdbcTemplate.queryForList("select * from area where name like ?", "%"+order.getCounty()+"%");
                if (!res1.isEmpty()){
                    code=res1.get(0).get("code").toString();
                }else{
                    //查询国标码-市
                    List<Map<String, Object>> res2 = jdbcTemplate.queryForList("select * from area where name like ?", "%"+order.getCity()+"%");
                    if (!res2.isEmpty()) {
                        code = res2.get(0).get("code").toString();
                    }
                }
                o.setCode(code);
                o.setOrder(order);
                o.setExtdCmmdtyCtgry("R9004788");
                o.setExtdCommodityName("智能门锁");
                o.setBrandCode("15GO");
                o.setSourceOrderItemId(IDGenerator.getOrderNo());
                o.setServiceTime(serviceTime);
                o.setStatus(2);
                String json=suningService.placeOrder(o);
                if(StringUtils.isNotBlank(json)){
                    JSONObject res= JSONObject.parseObject(json);
                    if(res.containsKey("success")&&res.get("success").toString().equals("true")){
                        if(res.containsKey("data")){
                            String data=res.getString("data");
                            JSONObject obj= JSONObject.parseObject(data);
                            String salesOrder=obj.getString("salesOrder");
                            o.setSalesOrder(salesOrder);
                        }
                    }
                }
                o.setMsg(json);
                o.updateById();
            } else {//失败
                if (o != null) {
                    installerOrderService.updateById(new InstallerOrder().setStatus(9).setId(o.getId()).setMsg(JSONObject.toJSONString(param)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Maps.<String, String>builder()
                .put("ret_code", "0000")
                .put("ret_msg", "交易成功")
                .build();
    }
    /**
     * 微信支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/wxcallback")
    @Transactional
    public String wxcallback(HttpServletRequest request) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            WxPayOrderNotifyResult result = this.wxPayService.parseOrderNotifyResult(xmlResult);
            //---------------------支付商品订单----------------
            if(result.getAttach().equals("order")){
                var o = orderService.getOne(new QueryWrapper<Order>().eq("ordernum",  result.getOutTradeNo()));
                if ("SUCCESS".equals(result.getReturnCode())) {
                    this.cardOrderService.finishOrder(o.getOrdernum());
                } else {
                    orderService.updateById(new Order().setStatus(9).setId(o.getId()));
                    return WxPayNotifyResponse.fail(result.getReturnMsg());
                }
            //---------------------安装订单----------------
            } else if(result.getAttach().equals("installerOrder")){
                var o = installerOrderService.getOne(new QueryWrapper<InstallerOrder>().eq("orderNo", result.getOutTradeNo()));
                if ("SUCCESS".equals(result.getReturnCode())) {//成功
                    String serviceTime= DateUtil.DateToString(new Date(),"yyyyMMdd") +"180000";
                    var order=orderService.getById(o.getOrderId());
                    //查询国标码-区
                    String code="340111";
                    List<Map<String, Object>> res1 = jdbcTemplate.queryForList("select * from area where name like ?", "%"+order.getCounty()+"%");
                    if (!res1.isEmpty()){
                        code=res1.get(0).get("code").toString();
                    }else{
                        //查询国标码-市
                        List<Map<String, Object>> res2 = jdbcTemplate.queryForList("select * from area where name like ?", "%"+order.getCity()+"%");
                        if (!res2.isEmpty()) {
                            code = res2.get(0).get("code").toString();
                        }
                    }
                    o.setCode(code);
                    o.setOrder(order);
                    o.setExtdCmmdtyCtgry("R9004788");
                    o.setExtdCommodityName("智能门锁");
                    o.setBrandCode("15GO");
                    o.setSourceOrderItemId(IDGenerator.getOrderNo());
                    o.setServiceTime(serviceTime);
                    o.setStatus(2);
                    String json=suningService.placeOrder(o);
                    if(StringUtils.isNotBlank(json)){
                        JSONObject res= JSONObject.parseObject(json);
                        if(res.containsKey("success")&&res.get("success").toString().equals("true")){
                            if(res.containsKey("data")){
                                String data=res.getString("data");
                                JSONObject obj= JSONObject.parseObject(data);
                                String salesOrder=obj.getString("salesOrder");
                                o.setSalesOrder(salesOrder);
                            }
                        }
                    }
                    o.setMsg(json);
                    o.updateById();
                } else {//失败
                    if (o != null) {
                        installerOrderService.updateById(new InstallerOrder().setStatus(9).setId(o.getId()));
                    }
                }

            }

            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("微信回调结果异常,异常原因{}", e.getLocalizedMessage());
            return WxPayNotifyResponse.fail(e.getLocalizedMessage());
        }
    }
}
