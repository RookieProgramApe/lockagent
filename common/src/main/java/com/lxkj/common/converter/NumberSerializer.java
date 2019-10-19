package com.lxkj.common.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Jackson数字格式化为两位小数点（序列化）
 */
@Slf4j
public class NumberSerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        String result = "";
        try {
            if (value != null) {
                if (value instanceof String && !StringUtils.equals((String) value, "-")) {
                    String temp = (String) value;
                    result = new BigDecimal(temp).setScale(2, RoundingMode.HALF_UP).toString();
                }
                if (value instanceof BigDecimal) {
                    BigDecimal temp = (BigDecimal) value;
                    result = temp.setScale(2, RoundingMode.HALF_UP).toString();
                }
                if (value instanceof Double) {
                    Double temp = (Double) value;
                    result = new BigDecimal(temp).setScale(2, RoundingMode.HALF_UP).toString();
                }
            }
            jsonGenerator.writeString(result);
        } catch (Exception e) {
            log.error("jackson数字格式化失败，源数字为=" + value);
            e.printStackTrace();
        }
    }
}
