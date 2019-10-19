package com.lxkj.common.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Jackson数字格式化（序列化）
 */
@Slf4j
public class DoubleSerializer extends JsonSerializer<String> {
    //private DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        String result = value;
        try {
            if (value != null && !StringUtils.equals(value, "-")) {
                result = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
            }
            jsonGenerator.writeString(result);
        } catch (Exception e) {
            log.error("jackson数字格式化失败，源数字为=" + value);
            e.printStackTrace();
        }
    }
}
