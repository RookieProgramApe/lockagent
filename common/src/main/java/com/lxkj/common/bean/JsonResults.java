package com.lxkj.common.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class JsonResults<T> implements Serializable {

    private static final long serialVersionUID = -9144177980983401769L;
    /**
     * 操作执行成功
     */
    @ApiModelProperty(value = "200=请求成功,500=请求错误，403=token失效,请重新登陆")
    private int code = ResultCodeEnum.SUCCESS.getCode();
    @ApiModelProperty(value = "记录数")
    private Long total = 0L;
    @ApiModelProperty(value = "业务数据")
    private T data;
    @ApiModelProperty(value = "提示信息")
    private String msg;

    public JsonResults() {
        super();
    }

    public JsonResults(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
