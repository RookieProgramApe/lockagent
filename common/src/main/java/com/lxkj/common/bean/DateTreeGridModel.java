package com.lxkj.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Treetable主数据
@Data
public class DateTreeGridModel<T> implements Serializable {

    private List<T> rows = new ArrayList<T>(); // 行数据
    private Long total = 0L;// 数据总记录数，用于分页
    private String message;
    private boolean flag;

    public DateTreeGridModel(String message) {
        this.message = message;
    }

    public DateTreeGridModel(List<T> data, Long total) {
        this.total = total;
        this.rows = data;
        this.flag = true;
    }


}