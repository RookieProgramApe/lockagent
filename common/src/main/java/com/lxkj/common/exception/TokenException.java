package com.lxkj.common.exception;

/**
 * token失效异常
 */
public class TokenException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public TokenException() {}

    public TokenException(String message) {
        super(message);
        this.message = message;
    }
}
