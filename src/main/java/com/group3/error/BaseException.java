package com.group3.error;

import lombok.Data;

/**
 * @author group3
 * @date 2024/04
 */
@Data
public class BaseException extends RuntimeException {

    private int code;
    private String msg;

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
