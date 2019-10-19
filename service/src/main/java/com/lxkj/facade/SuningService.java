package com.lxkj.facade;

import com.lxkj.common.util.collection.Maps;
import com.lxkj.common.util.json.JSON;
import com.lxkj.entity.InstallerOrder;
import com.lxkj.entity.Order;
import com.lxkj.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * 苏宁开放平台接口
 */
@Service
public class SuningService extends AbstractRemoteConnector {
    private static final Logger logger = LoggerFactory.getLogger(SuningService.class);
    private static final DateTimeFormatter basic_iso_date_time = DateTimeFormatter.ofPattern("yyyyMMdd");
    /*******************************KEY/密钥/商户编码**********************************/
    private static final String app_key = "00800092";
    private static final String app_secret = "hp7VXNNfDGFlKeRQ5diy7Q==";
    private static final String vendorCode = "70971041";
    //下单接口
    private static final String addOrder = "http://asapi.suning.com/asapi-web/api/v1?method=suning.lb.createorder.create";


    public String placeOrder(InstallerOrder order) {
        Map<String, String> riskItem = Maps.<String, String>builder()
                .put("vendorCode", vendorCode)
                .put("sourceOrderItemId", order.getSourceOrderItemId())
                .put("consignee", order.getOrder().getRecipient())
                .put("mobPhoneNum", order.getOrder().getMobile())
                .put("province", order.getOrder().getProvince())
                .put("city",  order.getOrder().getCity())
                .put("area",  order.getOrder().getCounty())
                .put("street", "全区")
                .put("address",  order.getOrder().getAddress())
                .put("gbCode",order.getCode())//国标码
                .put("cmmdtyQaType", "0")
                .put("extdCmmdtyCtgry", order.getExtdCmmdtyCtgry())//苏宁商品编码-变量
                .put("extdCommodityName",order.getExtdCommodityName())//苏宁商品名称-变量
                .put("brandCode",order.getBrandCode())//苏宁品牌编码-变量
                .put("proName", "01")//01：安装；02维修
                .put("saleQty", "1")
                .put("serviceTime", order.getServiceTime())//预约时间 090000 上午 150000 下午 180000 全天
                .build();
        String requestBody = JSON.stringify(riskItem);
        StringBuffer param = new StringBuffer();
        param.append(app_key);
        param.append("suning.lb.createorder.create");
        param.append(requestBody);
        param.append(app_secret);
        try {
            var sign = URLEncoder.encode(SignUtils.sign(param.toString(), "UTF-8"), StandardCharsets.UTF_8);//获取签名
            logger.info("安装下单：" + param.toString());
            String url = addOrder + "&app_key=" + app_key + "&sign=" + sign;
            HttpResponse<String> response = this.POST(url, APPLICATION_JSON_UTF8, requestBody);
            String responseBody = response.body();
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }


    public static void main(String[] args) {
        SuningService bean = new SuningService();
    }



}
