package com.lxkj.common.util;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 说明：参数封装Map
 * 创建人：FH Q313596790
 * 修改时间：2014年9月20日
 *
 * @author kenny
 */
public class PageData extends HashMap<Object, Object> implements Map<Object, Object> {

    private static final long serialVersionUID = -1930707188972221016L;
//    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static final SimpleDateFormat ddfTime = new SimpleDateFormat("yyyy-MM-dd");
    Map<Object, Object> map = null;
    HttpServletRequest request;

    public PageData(HttpServletRequest request) {
        this.request = request;
        Map<?, ?> properties = request.getParameterMap();
        Map<Object, Object> returnMap = new HashMap<Object, Object>();
        Iterator<?> entries = properties.entrySet().iterator();
        Entry<?, ?> entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Entry<?, ?>) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        map = returnMap;
    }

    public PageData() {
        map = new HashMap<Object, Object>();
    }

    @Override
    public Object get(Object key) {
        Object obj = null;
        if (map.get(key) instanceof Object[]) {
            Object[] arr = (Object[]) map.get(key);
            obj = request == null ? arr : (request.getParameter((String) key) == null ? arr : arr[0]);
        } else {
            obj = map.get(key);
        }
        return obj;
    }

    public String getString(Object key) {
        return (String) get(key);
    }

    public int getInteger(Object key) {
        return this.getInteger(key, 0);
    }


    public Date getDate(Object key,String pattern) {
        try {
            SimpleDateFormat sdfTime = new SimpleDateFormat(pattern);
            return sdfTime.parse(this.getString(key));
        } catch (ParseException e) {
            return null;
        }
    }

    public Long getLong(Object key) {
       return  this.getLong(key,0L);
    }

    public Long getLong(Object key, Long DefaultValue) {
        try {
            return Long.parseLong(this.getString(key));
        } catch (Exception e) {
            return DefaultValue;
        }
    }
    public int getInteger(Object key, Integer DefaultValue) {
        try {
            return Integer.parseInt(this.getString(key));
        } catch (Exception e) {
            return DefaultValue;
        }
    }


    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return map.containsValue(value);
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        // TODO Auto-generated method stub
        return map.entrySet();
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return map.isEmpty();
    }

    @Override
    public Set<Object> keySet() {
        // TODO Auto-generated method stub
        return map.keySet();
    }

    @Override
    public void putAll(Map<?, ?> t) {
        // TODO Auto-generated method stub
        map.putAll(t);
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return map.size();
    }

    @Override
    public Collection<Object> values() {
        // TODO Auto-generated method stub
        return map.values();
    }

}
