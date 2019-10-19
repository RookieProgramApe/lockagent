package com.lxkj.facade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * RSA签名和加密
 */
@Slf4j
public class RSAUtils {

  private static final String KEY_ALGORITHM = "RSA";
  private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
  private static final int MAX_ENCRYPT_BLOCK = 117;

  public static String sign(String source, String privateKey) {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(privateKey);
      PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
      PrivateKey key = keyFactory.generatePrivate(priPKCS8);
      Signature signet = Signature.getInstance(SIGNATURE_ALGORITHM);
      signet.initSign(key);
      signet.update(source.getBytes(StandardCharsets.UTF_8));
      byte[] signed = signet.sign();
      return Base64.getEncoder().encodeToString(signed);
    } catch (GeneralSecurityException e) {
      log.error("签名失败," + e.getMessage(), e);
      return null;
    }
  }

  public static String encrypt(String source, String publicKey) {
    try {
      byte[] data = source.getBytes();
      byte[] keyBytes = Base64.getDecoder().decode(publicKey);
      X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
      Key key = keyFactory.generatePublic(x509KeySpec);
      Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
      cipher.init(Cipher.ENCRYPT_MODE, key);
      int inputLen = data.length;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int offSet = 0;
      byte[] cache;
      int i = 0;
      while (inputLen - offSet > 0) {
        if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
          cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
        } else {
          cache = cipher.doFinal(data, offSet, inputLen - offSet);
        }
        out.write(cache, 0, cache.length);
        i++;
        offSet = i * MAX_ENCRYPT_BLOCK;
      }
      byte[] encryptedData = out.toByteArray();
      out.close();
      return Base64.getEncoder().encodeToString(encryptedData);
    } catch (GeneralSecurityException | IOException e) {
      log.error("加密失败" + e.getMessage(), e);
      return null;
    }
  }


  /**
   * MD5方法
   *
   * @param text 明文
   * @throws Exception
   */
  public static String md5(String text) {
    //加密后的字符串
    String encodeStr= DigestUtils.md5Hex(text);
    return encodeStr;
  }


  /**
   * Base64方法
   *
   * @param text 明文
   * @throws Exception
   */
  public static String Base64(String text) {
    //加密后的字符串
    String encodeStr=Base64.getEncoder().encodeToString(text.getBytes());
    return encodeStr;
  }




}
