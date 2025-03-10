package com.xiaohai.llminterface.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ofSuccess(T data) {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }

    public static <T> Result<T> ofError(T data) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), data);
    }

    public static <T> Result<T> ofParamsError() {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), "ParamsError", null);
    }

    public static <T> Result<T> fail(T data, String msg) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    public static <T> Result<T> ofError(int code, String message) {
        return new Result<>(code, message, null);
    }
}
