package com.lxkj.utils;

import com.alibaba.fastjson.JSONObject;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class FuncUtils {
    public FuncUtils() {
    }

    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String getSysDate() {
        SimpleDateFormat formdate = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date();
        String ss = formdate.format(curDate);
        return ss;
    }

    public static String getSysTime() {
        SimpleDateFormat formdate = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date();
        String ss = formdate.format(curDate);
        return ss;
    }

    public static boolean WriteFile(String fileName, String StrBuf) throws IOException {
        File file = new File(fileName);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(StrBuf, 0, StrBuf.length());
        bw.newLine();
        bw.close();
        fw.close();
        return true;
    }

    public static String FormatStringAddBlank(String sReturnBuf, int length) throws Exception {
        StringBuffer tempBuffer = new StringBuffer();
        if (null != sReturnBuf && !sReturnBuf.equals("") && !sReturnBuf.equals("null")) {
            String s2 = new String(sReturnBuf.getBytes("GB2312"), "ISO8859_1");
            int iLength = s2.length();
            if (length > iLength) {
                tempBuffer.append(sReturnBuf);

                for (int j = 0; j < length - iLength; ++j) {
                    tempBuffer.append(" ");
                }

                sReturnBuf = tempBuffer.toString();
            } else if (length < iLength) {
                sReturnBuf = absoluteSubstring(sReturnBuf, 0, length);
            }

            return sReturnBuf;
        } else {
            for (int i = 0; i < length; ++i) {
                tempBuffer.append(" ");
            }

            return tempBuffer.toString();
        }
    }

    public static int getStringLength(String s1) throws Exception {
        if (null != s1 && !s1.equals("")) {
            String s2 = new String(s1.getBytes("GB2312"), "ISO8859_1");
            return s2.length();
        } else {
            return 0;
        }
    }

    public static String FormatStringAddZero(String sReturnBuf, int length) {
        StringBuffer tempBuffer = new StringBuffer();
        int iLength;
        if (null != sReturnBuf && !sReturnBuf.equals("") && !sReturnBuf.equals("null")) {
            iLength = sReturnBuf.length();
            if (length > iLength) {
                for (int j = 0; j < length - iLength; ++j) {
                    sReturnBuf = "0" + sReturnBuf;
                }
            } else if (length < iLength) {
                sReturnBuf = absoluteSubstring(sReturnBuf, 0, length);
            }

            return sReturnBuf;
        } else {
            for (iLength = 0; iLength < length; ++iLength) {
                tempBuffer.append("0");
            }

            return tempBuffer.toString();
        }
    }

    public static String MultString(String sMoney, int iEr, String flag) {
        if (null != sMoney && !sMoney.equals("")) {
            if (null != flag && !flag.equals("")) {
                sMoney = stringMoveZero(sMoney);
                double iTemp = new Double(sMoney);
                if (flag.equals("+")) {
                    iTemp *= (double) iEr;
                    int aa = (new Double(iTemp)).intValue();
                    return String.valueOf(aa);
                } else {
                    if (flag.equals("-")) {
                        iTemp /= (double) iEr;
                    }

                    return String.valueOf(iTemp);
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String MultStringExt(String sMoney, int iEr, String flag) {
        if (null != sMoney && !sMoney.equals("")) {
            if (null != flag && !flag.equals("")) {
                sMoney = stringMoveZero(sMoney);
                double iTemp = new Double(sMoney);
                if (flag.equals("+")) {
                    iTemp *= (double) iEr;
                    double aa = new Double(iTemp);
                    return String.valueOf(aa);
                } else {
                    if (flag.equals("-")) {
                        iTemp /= (double) iEr;
                    }

                    return String.valueOf(iTemp);
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String stringMoveZero(String sMoney) {
        if (null != sMoney && !sMoney.equals("")) {
            int ilen = sMoney.indexOf("-");
            return ilen > 0 ? sMoney.substring(ilen, sMoney.length()) : sMoney;
        } else {
            return "";
        }
    }

    public static String absoluteSubstring(String s, int start, int end) {
        if (s != null && !s.equals("")) {
            try {
                String s2 = new String(s.getBytes("GB2312"), "ISO8859_1");
                s2 = s2.substring(start, end);
                return new String(s2.getBytes("ISO8859_1"), "GB2312");
            } catch (Exception var4) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String[] getAbsoluteSubstringArray(String s, int ilength) throws Exception {
        if (s != null && !s.equals("")) {
            try {
                String s2 = new String(s.getBytes("GB2312"), "ISO8859_1");
                int ilen = s2.length() / ilength;
                if (ilen == 0) {
                    return new String[0];
                } else {
                    int start = 0;
                    int end = ilength;
                    String[] returnarray = new String[ilen];

                    for (int i = 0; i < ilen; ++i) {
                        String s1 = s2.substring(start, end);
                        start = end;
                        end += ilength;
                        returnarray[i] = new String(s1.getBytes("ISO8859_1"), "GB2312");
                    }

                    return returnarray;
                }
            } catch (Exception var9) {
                return new String[0];
            }
        } else {
            return new String[0];
        }
    }


    public static String trimAllBlank(String str) {
        return isNull(str) ? "" : str.replace(" ", "");
    }

    public static String[] spiltStr(String fieldsru, String tag) {
        char dot = tag.charAt(0);
        String field = fieldsru + dot;
        int num = 0;
        int field_len = field.length();

        for (int i = 0; i < field_len; ++i) {
            if (field.charAt(i) == dot) {
                ++num;
            }
        }

        String[] returnarray = new String[num];
        int begin = 0;

        for (int j = 0; j < num; ++j) {
            int end = field.indexOf(dot, begin);
            returnarray[j] = field.substring(begin, end);
            begin = end + 1;
        }

        return returnarray;
    }

    public static boolean checkMobile(String fieldsru) {
        return isNull(fieldsru) ? false : Pattern.matches("1[0-9]{10}", fieldsru.trim());
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

                for (int i = 0; i != l / 2; ++i) {
                    b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
                }

                return b;
            }
        }
    }

    public static String strchange(String str) {
        return null != str && !str.equals("NULL") && !str.equals("null") ? str : "";
    }

    public static boolean isNull(String str) {
        return null == str || str.equalsIgnoreCase("NULL") || str.equals("");
    }

    public static String stringToThouMony(String sMoney) {
        if (null != sMoney && !sMoney.equals("")) {
            double d = new Double(sMoney);
            DecimalFormat df = new DecimalFormat("##,###,###,###,##0.00");
            return df.format(d);
        } else {
            return "";
        }
    }

    public static String FormatMoney(String sMoney) {
        if (null != sMoney && !sMoney.equals("")) {
            double d = new Double(sMoney);
            DecimalFormat df = new DecimalFormat("##########0.00");
            return df.format(d);
        } else {
            return "";
        }
    }

    public static double roundDouble(double val, int precision) {
        double factor = Math.pow(10.0D, (double) precision);
        return Math.floor(val * factor) / factor;
    }

    public static String formatFee(double fee, int precision) {
        DecimalFormat format = new DecimalFormat("#0.0000");
        return String.valueOf(roundDouble(Double.parseDouble(format.format(fee)), precision));
    }

    public static String getRandomNumber(int index) {
        Random random = new Random();
        String sRand = "";

        for (int i = 0; i < index; ++i) {
            String rand = null;
            if (random.nextInt(6) == i) {
                rand = String.valueOf((char) (65 + random.nextInt(26)));
            } else if (random.nextInt(6) == i + 1) {
                rand = String.valueOf((char) (97 + random.nextInt(26)));
            } else {
                rand = String.valueOf(random.nextInt(10));
            }

            sRand = sRand + rand;
        }

        return sRand;
    }





    public static String formatLTYMoneny(String money) {
        if (money != null && !money.trim().equals("") && !money.equals("null")) {
            try {
                double dmoney = Double.parseDouble(money);
                dmoney /= 1000.0D;
                DecimalFormat format = new DecimalFormat("#0.00");
                return format.format(dmoney);
            } catch (Exception var4) {
                return "0";
            }
        } else {
            return "0";
        }
    }

    public static String formatLTYMonenyInt(String money) {
        if (money != null && !money.trim().equals("") && !money.equals("null")) {
            try {
                double dmoney = Double.parseDouble(money);
                dmoney /= 1000.0D;
                DecimalFormat format = new DecimalFormat("#");
                return format.format(dmoney);
            } catch (Exception var4) {
                return "0";
            }
        } else {
            return "0";
        }
    }

    public static String formatYTLMoneny(String yuan) {
        try {
            double dYuan = Double.parseDouble(yuan);
            double dLi = dYuan * 1000.0D;
            DecimalFormat format = new DecimalFormat("#");
            return format.format(dLi);
        } catch (Exception var6) {
            return "0";
        }
    }

    public static String hiddenCard(String cardNo) {
        if (null == cardNo) {
            return null;
        } else {
            return cardNo.length() <= 4 ? cardNo : cardNo.substring(cardNo.length() - 4, cardNo.length());
        }
    }

    public static String hiddenCertifiID(String idNo) {
        if (isNull(idNo)) {
            return idNo;
        } else {
            String head = idNo.substring(0, 1);
            String end = idNo.substring(idNo.length() - 1);
            String sbf = head;

            for (int i = 0; i < idNo.length() - 2; ++i) {
                sbf = sbf + "*";
            }

            sbf = sbf + end;
            return sbf;
        }
    }

    public static String hiddenCvv(String cvv) {
        return isNull(cvv) ? cvv : "***";
    }

    public static String buildCertSerialnumber(String numer) {
        if (isNull(numer)) {
            return numer;
        } else {
            String first = numer.substring(0, 1);
            return Integer.valueOf(first, 16) >= 8 ? "00" + numer : numer;
        }
    }

    public static String changeDay(String date, int day) {
        if (date != null && date.trim().length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date newDate = sdf.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(newDate);
                cal.add(5, day);
                Date nextDate = cal.getTime();
                String next_dateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(nextDate);
                return next_dateStr;
            } catch (Exception var7) {
                return date;
            }
        } else {
            return date;
        }
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (!isNull(ip) && ip.contains(",")) {
            String[] ips = ip.split(",");
            ip = ips[ips.length - 1];
        }

        ip = isNull(ip) ? ip : ip.trim();
        return ip;
    }

    public static String formatStringAddZero(String sReturnBuf, int length) {
        StringBuffer tempBuffer = new StringBuffer();
        int iLength;
        if (null != sReturnBuf && !sReturnBuf.equals("") && !sReturnBuf.equals("null")) {
            if (sReturnBuf.length() > length) {
                return sReturnBuf;
            } else {
                iLength = sReturnBuf.length();
                if (length > iLength) {
                    for (int j = 0; j < length - iLength; ++j) {
                        sReturnBuf = "0" + sReturnBuf;
                    }
                } else if (length < iLength) {
                    sReturnBuf = absoluteSubstring(sReturnBuf, 0, length);
                }

                return sReturnBuf;
            }
        } else {
            for (iLength = 0; iLength < length; ++iLength) {
                tempBuffer.append("0");
            }

            return tempBuffer.toString();
        }
    }

    public static String encrytion(String str) {
        if (!isNull(str) && str.length() >= 2) {
            int length = str.length();
            if (length < 5) {
                return "*" + str.substring(1);
            } else {
                return length == 11 ? str.substring(0, 3) + "****" + str.substring(7) : str.substring(0, length - 4) + "**";
            }
        } else {
            return str;
        }
    }

    public static boolean isMachineId(String id) {
        if (isNull(id)) {
            return false;
        } else {
            return id.length() == 32;
        }
    }

    public static boolean isSigns(String signs) {
        return isNull(signs) ? false : Pattern.matches("[1-9]{4,9}", signs.trim());
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException var12) {
            var12.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException var11) {
            }

        }

        return sb.toString();
    }

    public static boolean isBankCard(String bankcard) {
        if (isNull(bankcard)) {
            return false;
        } else {
            return bankcard.matches("^\\d{14,19}$");
        }
    }



    public static String genSignData(JSONObject jsonObject) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList(jsonObject.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < keys.size(); ++i) {
            String key = (String) keys.get(i);
            if (!"sign".equals(key)) {
                String value = jsonObject.getString(key);
                if (!isNull(value)) {
                    content.append((i == 0 ? "" : "&") + key + "=" + value);
                }
            }
        }

        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc = signSrc.replaceFirst("&", "");
        }

        return signSrc;
    }

    public static boolean isMoney(String money) {
        if (isNull(money)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("^[0-9]{0,}[.]{0,1}[0-9]{0,2}$");
            if (!pattern.matcher(money).matches()) {
                return false;
            } else {
                return Double.parseDouble(money) >= 0.01D;
            }
        }
    }

    public static boolean isMoneyEqual(String money1, String money2) {
        if (isMoney(money1) && isMoney(money2)) {
            long long1 = Double.doubleToLongBits(Double.parseDouble(money1));
            long long2 = Double.doubleToLongBits(Double.parseDouble(money2));
            return long1 == long2;
        } else {
            return false;
        }
    }

    public static String getRefererAddress(HttpServletRequest request) {
        String doamin = request.getHeader("Referer");
        if (isNull(doamin)) {
            return "<unknown>";
        } else {
            try {
                doamin = doamin.substring(doamin.indexOf(":") + 3);
                doamin = doamin.substring(0, doamin.indexOf("/"));
            } catch (Exception var3) {
                return "<unknown>";
            }

            return isNull(doamin) ? "<unknown>" : doamin;
        }
    }

    public static String formatBankCard(String bankcard) {
        if (isNull(bankcard)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < bankcard.length(); ++i) {
                sb.append(bankcard.charAt(i));
                if ((i + 1) % 4 == 0) {
                    sb.append(" ");
                }
            }

            return sb.toString();
        }
    }

    public static String hiddenName(String name) {
        if (isNull(name)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("*");
            sb.append(name.substring(1));
            return sb.toString();
        }
    }

    public static String hidden500Name(String name) {
        if (isNull(name)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(name.substring(0, 1));

            for (int i = 1; i < name.length(); ++i) {
                sb.append("*");
            }

            return sb.toString();
        }
    }

    public static String hiddenBankCard(String bankcard) {
        if (!isBankCard(bankcard)) {
            return bankcard;
        } else {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < bankcard.length(); ++i) {
                if (i > 5 && i < bankcard.length() - 4) {
                    sb.append("*");
                } else {
                    sb.append(bankcard.charAt(i));
                }
            }

            return sb.toString();
        }
    }


    public static void main(String[] args) {
        String s = "155844";
        String a = formatYTWMoney(s);
        System.out.println(a);
        String ss = "245245259";
        String aa = formatLTYMoneny(ss);
        System.out.println(aa);
    }

    public static int checkChineselen(String str) {
        int len = 0;
        if (isNull(str)) {
            return len;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                String bb = str.substring(i, i + 1);
                boolean cc = Pattern.matches("[一-龥]", bb);
                if (cc) {
                    len += 3;
                } else {
                    ++len;
                }
            }

            return len;
        }
    }

    public static boolean checkSessionId(HttpServletRequest httpRequest, String sessionId) {
        Cookie[] cookies = httpRequest.getCookies();
        if (null == cookies) {
            return false;
        } else {
            String sessionIdGroup = null;

            for (int i = 0; i < cookies.length; ++i) {
                Cookie c = cookies[i];
                if (null != c.getName() && c.getName().equals("sessionId")) {
                    sessionIdGroup = c.getValue();
                    break;
                }
            }

            if (!isNull(sessionIdGroup) && !isNull(sessionId)) {
                return sessionIdGroup.contains(sessionId);
            } else {
                return false;
            }
        }
    }

    public static String formatYTWMoney(String yuan) {
        if (yuan != null && !yuan.trim().equals("") && !yuan.equals("null")) {
            try {
                double dmoney = Double.parseDouble(yuan);
                dmoney /= 10000.0D;
                DecimalFormat format = new DecimalFormat("#0.00");
                format.setRoundingMode(RoundingMode.FLOOR);
                String money = format.format(dmoney);
                if (money.endsWith("00")) {
                    money = money.substring(0, money.indexOf("."));
                } else if (money.endsWith("0")) {
                    money = money.substring(0, money.lastIndexOf("0"));
                }

                return money;
            } catch (Exception var5) {
                return "0";
            }
        } else {
            return "0";
        }
    }

    public static String formatMoney(double sMoney, int maximum) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(maximum);
        return numberFormat.format(sMoney);
    }

    public static boolean isMoneyIncludeZero(String money) {
        if (isNull(money)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("^[0-9]{0,}[.]{0,1}[0-9]{0,2}$");
            return pattern.matcher(money).matches();
        }
    }
}
