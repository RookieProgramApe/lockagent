package com.lxkj.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lxkj.facade.WxService;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(WxProperties.class)
public class WxServiceConfig {
    @Resource
    private WxProperties properties;


    @Bean
    public WxService wxService() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(properties.getAppid());
        configStorage.setSecret(properties.getSecret());
        configStorage.setToken(properties.getToken());
        configStorage.setAesKey(properties.getAesKey());
        WxService service = new WxService();
        service.setWxMpConfigStorage(configStorage);
        return service;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setTradeType(WxPayConstants.TradeType.JSAPI);
        payConfig.setSignType(WxPayConstants.SignType.MD5);
        payConfig.setAppId(StringUtils.trimToNull(properties.getAppid()));
        payConfig.setMchId(StringUtils.trimToNull(properties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(properties.getMchKey()));
        payConfig.setNotifyUrl(StringUtils.trimToNull(properties.getWxcallbackUrl()));
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
