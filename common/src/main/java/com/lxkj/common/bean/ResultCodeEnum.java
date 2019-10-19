package com.lxkj.common.bean;

public enum ResultCodeEnum {
    SUCCESS(200),//系统响应成功
    FAIL(500),//系统响应失败
    UNAUTHORIZED(401),//未认证（签名错误）
    NOT_FOUND(404),//接口不存在
    TOKENN_OTFOUND(403);//token失效


    public int code;

    ResultCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
