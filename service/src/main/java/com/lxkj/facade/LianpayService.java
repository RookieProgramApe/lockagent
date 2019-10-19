package com.lxkj.facade;


import com.lxkj.common.util.Strings;
import com.lxkj.common.util.collection.Lists;
import com.lxkj.common.util.collection.Maps;
import com.lxkj.common.util.json.JSON;
import com.lxkj.config.WxProperties;
import com.lxkj.entity.InstallerOrder;
import com.lxkj.entity.Member;
import com.lxkj.entity.Order;
import com.lxkj.service.MemberService;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lxkj.utils.LianLianPaySecurity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * l连连支付请求
 */
@Service
@Transactional
public class LianpayService extends AbstractRemoteConnector {

    private static final Logger logger = LoggerFactory.getLogger(LianpayService.class);
    private static final DateTimeFormatter basic_iso_date_time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String privateKey;
    private static final String publicKey;
    private static final String prepay_url = "https://mpayapi.lianlianpay.com/v1/bankcardprepay";
    private static final String merchant_id = "201908080002552020";

    private static String readKey(String resource) {
        try (InputStream in = LianpayService.class.getClassLoader().getResourceAsStream(resource)) {
            assert in != null;
            String asset = IOUtils.toString(in, StandardCharsets.UTF_8);
            return asset.lines().collect(Collectors.joining()).trim();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static {
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        privateKey = readKey("rsa_private_key.txt");
        publicKey = readKey("rsa_public_key.txt");
    }

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private MemberService memberService;

    /**
     * 【商品下单】调起连连支付
     *
     * @param order     订单
     * @param localhost 请求方IP地址
     * @return 连连支付返回的应答，其中payload是调起微信支付时需要使用的数据
     */
    public Map<String, Object> prepay(Order order, String localhost) {
        String now = LocalDateTime.now().format(basic_iso_date_time);
        Member member = this.memberService.getById(order.getMemberId());
        String goodsName = order.getCargoName() + " " + order.getSkuName();
        // build requestBody
        Map<String, String> riskItem = Maps.<String, String>builder()
                // 风控参数 https://open.lianlianpay.com/docs/development/risk-item-overview
                // 通用风控参数
                .put("frms_ware_category", "4016")
                .put("user_info_mercht_userno", order.getMemberId())
                .put("user_info_bind_phone", order.getMobile())
                .put("user_info_dt_register", new SimpleDateFormat("yyyyMMddHHmmss").format(member.getCreateTime()))
                .put("goods_name", goodsName)
                // 实物风控参数
                .put("virtual_goods_status", "0")
                .put("goods_count", Strings.of(order.getCount()))
                .put("delivery_full_name", order.getRecipient())
                .put("delivery_phone", order.getMobile())
                .put("logistics_mode", "2")
                .put("delivery_cycle", "48h")
                .put("delivery_addr_province", "340000")
                .put("delivery_addr_city", "340100")
                // API风控参数
                .put("frms_client_chnl", "16")
                .put("frms_ip_addr", localhost)
                .put("user_auth_flag", "1")
                .build();
        Map<String, String> extParam = Maps.<String, String>builder()
                // 微信请求参数
                .put("appid", wxProperties.getAppid())
                .put("openid", member.getOpenId())
                .build();
        Map<String, String> payload = Maps.<String, String>builder()
                // 支付请求参数
                .put("user_id", order.getMemberId())
                .put("oid_partner", merchant_id)
                .put("sign_type", "RSA")
                .put("busi_partner", "109001")
                .put("no_order", order.getOrdernum())
                .put("dt_order", now)
                .put("name_goods", goodsName)
                .put("money_order", order.getTotalPrice().toString())
                .put("notify_url", wxProperties.getCallbackUrl())
                .put("risk_item", JSON.stringify(riskItem))
                .put("pay_type", "W")
                .put("ext_param", JSON.stringify(extParam))
                .build();
        sign(payload);
        logger.info(JSON.stringify(payload));
        try {
            Map<String, String> params = Maps.<String, String>builder()
                    .put("oid_partner", merchant_id)
                    .put("pay_load", encrypt(payload))
                    .build();
            String requestBody = JSON.stringify(params);
            logger.info(requestBody);
            // send request
            HttpResponse<String> response = this.POST(prepay_url, APPLICATION_JSON_UTF8, requestBody);
            String responseBody = response.body();
            logger.info(responseBody);
            return JSON.parseMap(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
    /**
     * 【安装下单】调起连连支付
     */
    public Map<String, Object> prepay_install(InstallerOrder installorder, String localhost) {
        String now = LocalDateTime.now().format(basic_iso_date_time);
        var order=installorder.getOrder();
        Member member = this.memberService.getById(order.getMemberId());
        String goodsName = order.getCargoName() + " " + order.getSkuName();
        // build requestBody
        Map<String, String> riskItem = Maps.<String, String>builder()
                // 风控参数 https://open.lianlianpay.com/docs/development/risk-item-overview
                // 通用风控参数
                .put("frms_ware_category", "4016")
                .put("user_info_mercht_userno", order.getMemberId())
                .put("user_info_bind_phone", order.getMobile())
                .put("user_info_dt_register", new SimpleDateFormat("yyyyMMddHHmmss").format(member.getCreateTime()))
                .put("goods_name", goodsName)
                // 实物风控参数
                .put("virtual_goods_status", "0")
                .put("goods_count", Strings.of(order.getCount()))
                .put("delivery_full_name", order.getRecipient())
                .put("delivery_phone", order.getMobile())
                .put("logistics_mode", "2")
                .put("delivery_cycle", "48h")
                .put("delivery_addr_province", "340000")
                .put("delivery_addr_city", "340100")
                // API风控参数
                .put("frms_client_chnl", "16")
                .put("frms_ip_addr", localhost)
                .put("user_auth_flag", "1")
                .build();
        Map<String, String> extParam = Maps.<String, String>builder()
                // 微信请求参数
                .put("appid", wxProperties.getAppid())
                .put("openid", member.getOpenId())
                .build();
        Map<String, String> payload = Maps.<String, String>builder()
                // 支付请求参数
                .put("user_id", order.getMemberId())
                .put("oid_partner", merchant_id)
                .put("sign_type", "RSA")
                .put("busi_partner", "109001")
                .put("no_order", installorder.getOrderNo())
                .put("dt_order", now)
                .put("name_goods", goodsName)
                .put("money_order", installorder.getTotalPrice().toString())
                .put("notify_url", wxProperties.getCallbackUrl2())
                .put("risk_item", JSON.stringify(riskItem))
                .put("pay_type", "W")
                .put("ext_param", JSON.stringify(extParam))
                .build();
        sign(payload);
        logger.info(JSON.stringify(payload));
        try {
            Map<String, String> params = Maps.<String, String>builder()
                    .put("oid_partner", merchant_id)
                    .put("pay_load", encrypt(payload))
                    .build();
            String requestBody = JSON.stringify(params);
            logger.info(requestBody);
            // send request
            HttpResponse<String> response = this.POST(prepay_url, APPLICATION_JSON_UTF8, requestBody);
            String responseBody = response.body();
            logger.info(responseBody);
            return JSON.parseMap(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
    private String joint(Map<String, String> payload) {
        List<String> keys = new ArrayList<>(payload.keySet());
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        List<String> pairs = Lists.of();
        for (String key : keys) {
            String value = payload.get(key);
            if ("sign".equals(key) || value == null) {
                continue;
            }
            pairs.add(key + "=" + value);
        }
        return String.join("&", pairs);
    }

    private void sign(Map<String, String> payload) {
        String source = joint(payload);
        String sign = RSAUtils.sign(source, privateKey);
        payload.put("sign", sign);
    }

    private String encrypt(Map<String, String> payload) throws Exception {
        String source = JSON.stringify(payload);
        String encryptStr = LianLianPaySecurity.encrypt(source, publicKey);
        //return RSAUtils.encrypt(source, publicKey);
        return encryptStr;
    }

}
