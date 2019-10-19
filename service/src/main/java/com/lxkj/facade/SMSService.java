package com.lxkj.facade;

import com.lxkj.common.util.Strings;
import com.lxkj.common.util.collection.Maps;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * 短信
 */
@Service
public class SMSService extends AbstractRemoteConnector {

  private static final String url = "http://sms.bamikeji.com:8890/mtPort/mt/normal/send";
  private static final String id = "2475";
  private static final String password = "bm1907080808";
  private static final String sign = "【安纹智能家居】";

  private String hashedPassword;

  public SMSService() {
    this.hashedPassword = this.md5(password);
  }

  /**
   * 发送短信
   *
   * @param content 短信内容
   * @param mobiles 手机号
   */
  public void send(String content, String... mobiles) {
    if (Strings.isBlank(content) || mobiles == null || mobiles.length == 0) {
      logger.warn("内容为空或手机号码为空");
      return;
    }
    String mobile = Stream.of(mobiles)
        .filter(Strings::isNotBlank)
        .map(Strings::trim)
        .collect(Collectors.joining(","));
    String encodedContent = URLEncoder.encode(content + sign, StandardCharsets.UTF_8);
    Map<String, String> param = Maps.<String, String>builder()
        .put("uid", id)
        .put("passwd", hashedPassword)
        .put("phonelist", mobile)
        .put("content", encodedContent)
        .build();
    String requestBody = this.formData(param);
    this.POST(url, APPLICATION_FORM_URLENCODED_UTF8, requestBody);
  }

  /**
   * 发送四位随机数字
   *
   * @param mobile 手机号
   * @return 发送的随机数字
   */
  public String sendRandomDigit(String mobile) {
    String digits = Strings.randomNumeric(4);
    this.send("您的验证码是" + digits, mobile);
    return digits;
  }

}
