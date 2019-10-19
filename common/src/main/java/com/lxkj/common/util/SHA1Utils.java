package com.lxkj.common.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author apple
 * @Date 2018/3/31 2:11
 * @Description:
 * @Modified by:
 */
@Slf4j
public class SHA1Utils {

    /**
     * 对字符串进行SHA_1加密
     * @param content 需要加密的字符串
     * @return
     */
    public static String getSha1String(String content) {
        String sha1String = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            sha1String = byteArrayToHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("sha_1加密失败，原因为：" + e.getMessage());
        }
        return sha1String;
    }

    // 将字节数组转换为十六进制字符串
    private static String byteArrayToHexString(byte[] bytearray) {
        String strDigest = "";
        for (int i = 0; i < bytearray.length; i++) {
            strDigest += byteToHexString(bytearray[i]);
        }
        return strDigest;
    }

    // 将字节转换为十六进制字符串
    private static String byteToHexString(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }
}
