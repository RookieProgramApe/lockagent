package com.lxkj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import Decoder.BASE64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 〈一句话功能简述〉<br>
 * 数据签名校验
 * 
 * @author castle
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public final class MD5AndBase64Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(MD5AndBase64Util.class);

    public static final BASE64Encoder baseEncoder = new BASE64Encoder();

    /**
     * 
     * 功能描述: <br>
     * MD5 编码
     * 
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static byte[] getMD5(byte[] value) { 
        MessageDigest md5 = null;
        try {
        	if(value!=null){
	            md5 = MessageDigest.getInstance("MD5");
	            return md5.digest(value);
        	}
        	return null;
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("MD5加密失败：{}", ex);
            return null;// 此处返回空 让业务方报错
        }

    }

    /**
     * 
     * 功能描述: <br>
     * md5 base64 编码
     * 
     * @param value
     * @param charset
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String getMD5Base64(byte[] value, String charset) {
        try {
        	if (value!=null){
        		 return baseEncoder.encode(getMD5(value));
        	}
        	return "";
           
        } catch (Exception ex) {
            LOGGER.error("MD5Base64失败：{}", ex);
            return "";// 此处返回空 让业务方报错
        }

    }

}
