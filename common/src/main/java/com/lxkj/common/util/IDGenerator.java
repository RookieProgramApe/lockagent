package com.lxkj.common.util;

import com.lxkj.common.bean.DateStyle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * java ID序列号生成器
 * 描述：可生成不重复、不连续的订单号
 */
@Slf4j
public class IDGenerator {
    /**
     * 获取带时间的序列号（适合订单编码等）
     * @param bits 序列号位数
     * @return
     */
    public static String generatorIdUnionTime(int bits){
        String currTime = DateUtil.DateToString(new Date(),DateStyle.YYYYMMDD);
        String randomString = RandomStringUtils.randomNumeric(bits);
        return  currTime+randomString;
    }

    public static String getOrderNo() {
        String orderNo = "";
        UUID uuid = UUID.randomUUID();
        String trandNo = String.valueOf((Math.random() * 9 + 1) * 1000000);
        String sdf = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date());
        orderNo = uuid.toString().substring(0, 8);
        orderNo = orderNo + sdf;
        return orderNo;
    }
}
