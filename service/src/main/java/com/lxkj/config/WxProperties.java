package com.lxkj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wechat.mp")
public class WxProperties {

  /**
   * 设置微信小程序的appid
   */
  private String appid;

  /**
   * 设置微信小程序的Secret
   */
  private String secret;

  /**
   * 设置微信小程序的token
   */
  private String token;

  /**
   * 设置微信小程序的EncodingAESKey
   */
  private String aesKey;

  /**
   * 连连支付回调
   */
  private String callbackUrl;
  private String callbackUrl2;
  /**
   * 微信支付回调
   */
  private String wxcallbackUrl;
  /**
   * 商户号
   */
  private String mchId;

  /**
   * 支付密钥
   */
  private String mchKey;

}