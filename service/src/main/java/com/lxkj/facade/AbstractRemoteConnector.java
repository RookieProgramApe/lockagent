package com.lxkj.facade;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.lxkj.common.util.Strings;
import com.lxkj.common.util.collection.Lists;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRemoteConnector {

  protected static final Logger logger = LoggerFactory.getLogger(AbstractRemoteConnector.class);
  protected static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
  protected static final String APPLICATION_FORM_URLENCODED_UTF8 = "application/x-www-form-urlencoded;charset=UTF-8";
  // 连接超时时间，默认10秒
  private static final int socketTimeout = 10000;
  // 传输超时时间，默认30秒
  private static final int connectTimeout = 30000;

  protected HttpResponse<String> POST(final String url, final String contentType, final String requestBody) {
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header(CONTENT_TYPE, contentType)
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      logger.info(response.body());
      return response;
    } catch (IOException | InterruptedException e) {
      logger.error(e.getLocalizedMessage(), e);
      return null;
    }
  }
  public static String sendPost(String url, String json) throws ClientProtocolException, IOException,
          UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
    HttpPost httpPost = new HttpPost(url);
    // 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
    StringEntity postEntity = new StringEntity(json, "UTF-8");
    httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
    httpPost.setEntity(postEntity);

    // 设置请求器的配置
    RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
            .setConnectTimeout(connectTimeout).build();
    httpPost.setConfig(requestConfig);

    org.apache.http.client.HttpClient httpClient = HttpClients.createDefault();
    org.apache.http.HttpResponse response = httpClient.execute(httpPost);
    HttpEntity entity = response.getEntity();
    String result = EntityUtils.toString(entity, "UTF-8");
    return result;
  }


  protected String formData(final Map<String, ?> param) {
    return param.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + Strings.of(entry.getValue()))
        .collect(Collectors.joining("&"));
  }

  protected String formData(final Map<String, ?> param, Comparator<String> keyComparator) {
    List<String> keys = new ArrayList<>(param.keySet());
    keys.sort(keyComparator);
    List<String> pairs = Lists.of();
    for (String key : keys) {
      String value = Strings.of(param.get(key));
      pairs.add(key + "=" + value);
    }
    return String.join("&", pairs);
  }

  protected String md5(String source) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(source.getBytes());
      return new BigInteger(1, md.digest()).toString(16);
    } catch (Exception e) {
      logger.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

}
