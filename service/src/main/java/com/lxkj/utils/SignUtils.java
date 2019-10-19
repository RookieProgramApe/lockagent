package com.lxkj.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * 〈一句话功能简述〉<br>
 * 数字签名
 * 
 * @author <a href="mailto:zyyy930@126.com">castle</a>
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SignUtils {

    /**
     * 
     * 功能描述: <br>
     * 数字签名
     * 
     * @param binaryData
     * @param charset
     * @return
     * @throws NoSuchAlgorithmException
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String sign(byte[] binaryData, String charset)
            throws NoSuchAlgorithmException {
        return MD5AndBase64Util.getMD5Base64(binaryData, charset);
    }
    
    public static String sign(String str, String charset)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return MD5AndBase64Util.getMD5Base64(str.getBytes(charset), null);
    }
    
    
    public static void main(String[] ss) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    	System.out.println(sign("锄禾日当午","UTF-8"));
    }

}
