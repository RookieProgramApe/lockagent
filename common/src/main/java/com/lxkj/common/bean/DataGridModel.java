package com.lxkj.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataGridModel <T> implements Serializable {

    /**
     * layUi table返回数据格式
     */
    private static final long serialVersionUID = 3570024749499559397L;

    //table主数据
    private Integer code=0;
    private String msg;
    private Long count = 0L;
    private List<T> data;

    //状态数据
    public static final int   Status_Success   = 200;
    public static final int   Status_Error     = 300;
    public static final int   Status_TimeOut   = 301;
    public static final int Status_SessionOut = 304;
    private int  status   = Status_Error;

    public DataGridModel() {
    }


    public DataGridModel(String message) {
        this.status=Status_Error;
    }

    public DataGridModel(List<T> data, Long total) {
        this.status=Status_Success;
        this.code=0;
        this.count = total;
        this.data = data;
    }
}
