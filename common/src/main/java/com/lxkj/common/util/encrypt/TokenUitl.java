package com.lxkj.common.util.encrypt;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TokenUitl {
    private static final String encryModel = "MD5";

    /**
     * @param srcData 约定用来计算token的参数
     * @return
     * @Description : 身份验证token值算法：
     * 算法是：将特定的某几个参数一map的数据结构传入，
     * 进行字典序排序以后进行md5加密,32位小写加密；
     * @Method_Name : authentication
     */
    public static String authentication(Map<String, Object> srcData) {
        //排序，根据keyde 字典序排序
        List<Entry<String, Object>> list = new ArrayList<Entry<String, Object>>(srcData.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Object>>() {
            //升序排序
            public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        StringBuffer srcSb = new StringBuffer();
        for (Entry<String, Object> srcAtom : list) {
            srcSb.append(String.valueOf(srcAtom.getValue()));
        }
        String token = TokenUitl.md5(srcSb.toString());
        return token;
    }


    /*
     * md5加密
     */
    public static String md5(String str) {
        return encrypt(encryModel, str);
    }

    public static String encrypt(String algorithm, String str) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(str.getBytes());
            StringBuffer sb = new StringBuffer();
            byte[] bytes = md.digest();
            for (int i = 0; i < bytes.length; i++) {
                int b = bytes[i] & 0xFF;
                if (b < 0x10) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
