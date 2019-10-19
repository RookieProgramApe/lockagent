package com.lxkj.utils;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class UberEncrypt {
    private static final String ALGORITHM_TYPE = "AES/CTR/NoPadding";
    private static final String RSA_ECB_OAEP_PADDING_TYPE = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_RSA = "RSA";

    public UberEncrypt() {
    }

    public static String rsaEncrypt(String source, String public_key) throws Exception {
        BASE64Decoder b64d = new BASE64Decoder();
        byte[] keyByte = b64d.decodeBuffer(public_key);
        X509EncodedKeySpec x509ek = new X509EncodedKeySpec(keyByte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509ek);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(1, publicKey);
        byte[] sbt = source.getBytes("UTF-8");
        byte[] epByte = cipher.doFinal(sbt);
        BASE64Encoder encoder = new BASE64Encoder();
        String epStr = encoder.encode(epByte);
        return epStr;
    }

    public static byte[] rsaDecrypt(String cryptograph, String private_key) throws Exception {
        BASE64Decoder b64d = new BASE64Decoder();
        byte[] keyByte = b64d.decodeBuffer(private_key);
        PKCS8EncodedKeySpec s8ek = new PKCS8EncodedKeySpec(keyByte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(s8ek);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(2, privateKey);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(cryptograph);
        byte[] b = cipher.doFinal(b1);
        return b;
    }

    public static String aesEncrypt(byte[] msgbt, byte[] aesKey, byte[] nonce) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ips = createCtrIv(nonce);
        cipher.init(1, secretKeySpec, ips);
        byte[] epByte = cipher.doFinal(msgbt);
        BASE64Encoder encoder = new BASE64Encoder();
        String epStr = encoder.encode(epByte);
        return epStr;
    }

    public static String aesDecrypt(String msgbt, byte[] aesKey, byte[] iv) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ips = createCtrIv(iv);
        cipher.init(2, secretKeySpec, ips);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(msgbt);
        byte[] b = cipher.doFinal(b1);
        return new String(b, "UTF-8");
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if (n < b.length - 1) {
                hs = hs;
            }
        }

        return hs.toUpperCase();
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        } else {
            int l = strhex.length();
            if (l % 2 == 1) {
                return null;
            } else {
                byte[] b = new byte[l / 2];

                for(int i = 0; i != l / 2; ++i) {
                    b[i] = (byte)Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
                }

                return b;
            }
        }
    }

    private static IvParameterSpec createCtrIv(byte[] nonce) {
        byte[] counter = new byte[]{0, 0, 0, 0, 0, 0, 0, 1};
        byte[] output = new byte[nonce.length + counter.length];

        int i;
        for(i = 0; i < nonce.length; ++i) {
            output[i] = nonce[i];
        }

        for(i = 0; i < counter.length; ++i) {
            output[i + nonce.length] = counter[i];
        }

        return new IvParameterSpec(output);
    }

    public static byte[] encodeHmacSHA256(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        byte[] digest = mac.doFinal(data);
        return digest;
    }

    public static boolean isvalidate(String base64_nonce, String base64_encrypted_hmac_key, String trader_pri_key, String base64_ciphertext, String signature) {
        try {
            byte[] hmac_key = rsaDecrypt(base64_encrypted_hmac_key, trader_pri_key);
            String message = base64_nonce + "$" + base64_ciphertext;
            byte[] sign = encodeHmacSHA256(message.getBytes(), hmac_key);
            BASE64Encoder encoder = new BASE64Encoder();
            String new_signature = encoder.encode(sign);
            if (signature.equals(new_signature)) {
                return true;
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return false;
    }

    public static String uberDecrypt(String base64_ciphertext, String base64_encrypted_aes_key, String base64_nonce, String trader_pri_key) {
        try {
            byte[] aes_key = rsaDecrypt(base64_encrypted_aes_key, trader_pri_key);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] nonce = decoder.decodeBuffer(base64_nonce);
            String new_original_data = aesDecrypt(base64_ciphertext, aes_key, nonce);
            return new_original_data;
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static String uberEncrypt(String req, String public_key, String hmack_key, String version, String aes_key, String nonce) {
        try {
            BASE64Encoder encoder = new BASE64Encoder();
            String B64hmack_key = rsaEncrypt(hmack_key, public_key);
            String B64aes_key = rsaEncrypt(aes_key, public_key);
            String B64nonce = encoder.encode(nonce.getBytes());
            String encry = aesEncrypt(req.getBytes("UTF-8"), aes_key.getBytes(), nonce.getBytes());
            String message = B64nonce + "$" + encry;
            byte[] sign = encodeHmacSHA256(message.getBytes(), hmack_key.getBytes());
            String B64sign = encoder.encode(sign);
            String encrpt_str = version + "$" + B64hmack_key + "$" + B64aes_key + "$" + B64nonce + "$" + encry + "$" + B64sign;
            return encrpt_str;
        } catch (Exception var15) {
            var15.printStackTrace();
            return null;
        }
    }
}
