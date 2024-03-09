package com.spy.antoj.exception;

import com.spy.antoj.common.ErrorCode;

/**
 * 自定义异常
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
