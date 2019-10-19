package com.lxkj.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.lxkj.common.util.collection.Lists;
import com.lxkj.common.util.collection.Maps;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 根据单号查找快递信息
 */
@Service
public class DeliveryLookupService extends AbstractRemoteConnector {

  private static final Logger logger = LoggerFactory.getLogger(DeliveryLookupService.class);
  private static final ObjectMapper xmlMapper;
  private static final DateTimeFormatter full_date_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final String url = "http://MarketingInterface.yto.net.cn";
  private static final String app_key = "LsRnRI";
  private static final String secret_key = "6i9e8p";
  private static final String method = "yto.Marketing.WaybillTrace";
  private static final String user_id = "TANZHI123";
  private static final String api_version = "1.01";

  static {
    JacksonXmlModule module = new JacksonXmlModule();
    module.setDefaultUseWrapper(false);
    xmlMapper = new XmlMapper(module);
  }

  /**
   * 查询运单轨迹
   *
   * @param deliveryTrack 运单号
   * @return [{Waybill_No, Upload_Time, ProcessInfo}] 列表：{运单号，更新时间，处理信息}
   */
  public List<WaybillProcessInfo> lookup(String deliveryTrack) throws IOException {
    if (deliveryTrack == null || deliveryTrack.isBlank()) {
      logger.warn("运单号为空");
      return null;
    }
    Map<String, Object> trackData = Maps.of("ufinterface",
        Maps.of("Result",
            Maps.of("WaybillCode",
                Maps.of("Number", deliveryTrack)))
    );
    String requestedTrackData = xmlMapper.writeValueAsString(trackData)
        .replace("<EnhancedLinkedHashMap>", "")
        .replace("</EnhancedLinkedHashMap>", "");
    Map<String, String> param = Maps.<String, String>builder()
        .put("app_key", app_key)
        .put("method", method)
        .put("format", "XML")
        .put("timestamp", LocalDateTime.now().format(full_date_time))
        .put("user_id", user_id)
        .put("v", api_version)
        .build();
    String sign = this.sign(param);
    String requestBody = "sign=" + sign + "&" + this.formData(param, String.CASE_INSENSITIVE_ORDER) + "&param=" + requestedTrackData;
    logger.info(requestBody);
    HttpResponse<String> response = this.POST(url, APPLICATION_FORM_URLENCODED_UTF8, requestBody);
    ufinterface o = xmlMapper.readValue(response.body(), ufinterface.class);
    return o.getResult().getWaybillProcessInfo();
  }

  private String sign(Map<String, String> param) {
    List<String> keys = Lists.of("user_id", "app_key", "format", "method", "timestamp", "v");
    keys.sort(String.CASE_INSENSITIVE_ORDER);
    List<String> pairs = Lists.of();
    for (String key : keys) {
      String value = param.get(key);
      pairs.add(key + value);
    }
    String combo = secret_key + String.join("", pairs);
    return this.md5(combo).toUpperCase();
  }

  @Data
  public static class ufinterface {

    @JacksonXmlProperty(localName = "Result")
    private Result Result;
  }

  @Data
  public static class Result {

    @JacksonXmlProperty(localName = "WaybillProcessInfo")
    private List<WaybillProcessInfo> WaybillProcessInfo = Lists.of();
  }

  @Data
  public static class WaybillProcessInfo {

    @JacksonXmlProperty(localName = "Waybill_No")
    private String Waybill_No;

    @JacksonXmlProperty(localName = "Upload_Time")
    private String Upload_Time;

    @JacksonXmlProperty(localName = "ProcessInfo")
    private String ProcessInfo;

  }

}
