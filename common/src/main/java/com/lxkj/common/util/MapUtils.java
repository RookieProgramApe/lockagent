package com.lxkj.common.util;

import java.util.Map;

public class MapUtils {
    public static void putIfNotEmpty(Map<String, Object> map, String field, Object paramValue) {
        if (!EmptyCheckUtils.isEmpty(paramValue)) {
            map.put(field, paramValue);
        }

    }
}
