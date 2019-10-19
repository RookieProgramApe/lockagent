package com.lxkj.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Apple
 */
@Slf4j
public class IPUtil {
    /**
     * 描述：获取IP地址
     *
     * @param request
     * @return
     * @author huaping hu
     * @date 2016年6月1日下午5:25:44
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (!checkIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getRemoteAddr();
        }
        if(StringUtils.equals(ip,"0:0:0:0:0:0:0:1")){
            ip = "127.0.0.1";
        }
        return ip;
    }
    private static boolean checkIP(String ip) {
        if (ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip)
                || ip.split(".").length != 4) {
            return false;
        }
        return true;
    }

    /**
     * 描述：将InputStream转换成String
     *
     * @param is
     * @return
     * @author huaping hu
     * @date 2016年6月1日下午5:51:53
     */
    public static String streamConvertToSting(InputStream is) {

        String tempStr = "";
        try {

            if (is == null) return null;
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
            byte[] by = new byte[1024];
            int len = 0;
            while ((len = is.read(by)) != -1) {
                arrayOut.write(by, 0, len);
            }
            tempStr = new String(arrayOut.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempStr;
    }


}
