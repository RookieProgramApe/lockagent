package com.lxkj.utils;




import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class Endecrypt {
    public Endecrypt() {
    }

    public byte[] md5(String strSrc) {
        byte[] returnByte = (byte[]) null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            returnByte = md5.digest(strSrc.getBytes("GBK"));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return returnByte;
    }

    private byte[] getEnKey(String spKey) {
        byte[] desKey = (byte[]) null;

        try {
            byte[] desKey1 = spKey.getBytes();
            desKey = new byte[16];

            int i;
            for (i = 0; i < desKey1.length && i < 16; ++i) {
                desKey[i] = desKey1[i];
            }

            if (i < 16) {
                desKey[i] = 0;
                ++i;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return desKey;
    }

    public byte[] Encrypt(byte[] src, byte[] enKey) {
        byte[] encryptedData = (byte[]) null;

        try {
            DESedeKeySpec dks = new DESedeKeySpec(enKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(1, key);
            encryptedData = cipher.doFinal(src);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return encryptedData;
    }

    public String getBase64Encode(byte[] src) {
        String requestValue = "";

        try {
            BASE64Encoder base64en = new BASE64Encoder();
            requestValue = base64en.encode(src);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return requestValue;
    }

    public String filter(String str) {
        String output = null;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < str.length(); ++i) {
            int asc = str.charAt(i);
            if (asc != '\n' && asc != '\r') {
                sb.append(str.subSequence(i, i + 1));
            }
        }

        output = new String(sb);
        return output;
    }

    public String getURLEncode(String src) {
        String requestValue = "";

        try {
            requestValue = URLEncoder.encode(src, "UTF-8");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return requestValue;
    }

    public String get3DESEncrypt(String src, String spkey) {
        String requestValue = "";

        try {
            byte[] enKey = this.getEnKey(spkey);
            byte[] src2 = src.getBytes("UTF-16LE");
            byte[] encryptedData = this.Encrypt(src2, enKey);
            String base64String = this.getBase64Encode(encryptedData);
            String base64Encrypt = this.filter(base64String);
            requestValue = this.getURLEncode(base64Encrypt);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return requestValue;
    }

    public String getURLDecoderdecode(String src) {
        String requestValue = "";

        try {
            requestValue = URLDecoder.decode(src, "UTF-8");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return requestValue;
    }

    public String deCrypt(byte[] debase64, String spKey) {
        String strDe = null;
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            byte[] key = this.getEnKey(spKey);
            System.out.println(key);
            DESedeKeySpec dks = new DESedeKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey sKey = keyFactory.generateSecret(dks);
            cipher.init(2, sKey);
            byte[] ciphertext = cipher.doFinal(debase64);
            strDe = new String(ciphertext, "UTF-16LE");
        } catch (Exception var10) {
            strDe = "";
            var10.printStackTrace();
        }

        return strDe;
    }

    public String get3DESDecrypt(String src, String spkey) {
        String requestValue = "";

        try {
            String base64Decrypt = this.filter(src);
            String URLValue = this.getURLDecoderdecode(base64Decrypt);
            BASE64Decoder base64Decode = new BASE64Decoder();
            byte[] base64DValue = base64Decode.decodeBuffer(URLValue);
            requestValue = this.deCrypt(base64DValue, spkey);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return requestValue;
    }


}
