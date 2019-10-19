package com.lxkj.common.exception;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public BusinessException() {}

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }
}
