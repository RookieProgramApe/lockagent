package com.lxkj.facade;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lxkj.common.bean.JsonResults;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.Map;

/**
 * 物流查询业务
 * @author Zhanqian
 * @date 2019/11/19 17:41
 */
@Service
public class DeliveryLookService extends AbstractRemoteConnector {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryLookService.class);
    protected static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    @Value("${kd.customer}")
    private String customer;
    @Value("${kd.key}")
    private String key;
    @Value("${kd.apiUrl}")
    private String apiUrl;


    /**
     *
     * @param deliveryTrack  快递运单号
     * @param com  快递公司代码
     * @return
     */
    public List<Map> queryDelivery(String deliveryTrack, String com, String phone) throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {



        JSONObject param = new JSONObject();
        param.put("com", com);
        param.put("num", deliveryTrack);
        // 顺丰需要手机号
        if(com.equals("shunfeng")){
            param.put("phone", phone);
        }


//                Map<String, String> params = new HashedMap();
//                params.put("param", param.toJSONString());
//                params.put("sign", this.md5(param.toJSONString() + key + customer).toUpperCase());
//                params.put("customer", customer);
        // 这个接口只能用url传参=============
        String requestBody = "customer=" + customer + "&sign=" + this.md5(param.toJSONString() + key + customer).toUpperCase() + "&param=" + URLEncoder.encode(param.toJSONString(), "UTF-8");

        String reslut = sendPost1(apiUrl, requestBody);


        return this.formData(reslut);
    }
}
