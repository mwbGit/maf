package com.mwb.maf.core.base;

import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;

@ToString
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = -3540272309116909111L;
    private static final int OK_CODE = 0;
    private static final String OK_MSG = "成功";
    private static final int FAIL_CODE = -1;
    private static final String FAIL_MSG = "成功";
    public static final ApiResult FAIL;
    public static final ApiResult OK;
    protected int code;
    protected String message;
    protected T zpData = (T) Collections.EMPTY_MAP;

    public ApiResult() {
    }

    public ApiResult(int code, String message, T zpData) {
        this.code = code;
        this.message = message;
        this.zpData = zpData;
    }

    private static <T> ApiResult<T> wrap(int code, String msg, T data) {
        return new ApiResult<T>(code, msg, data);
    }

    public static <T> ApiResult<T> success(T data) {
        return wrap(OK_CODE, OK_MSG, data);
    }

    public static <T> ApiResult<T> failed(int code, String msg) {
        return wrap(code, msg, (T) Collections.EMPTY_MAP);
    }

    public static <T> ApiResult<T> failed(String msg) {
        return failed(FAIL_CODE, msg);
    }

    public static <T> ApiResult<T> failed(T data) {
        return wrap(FAIL_CODE, FAIL_MSG, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getZpData() {
        return zpData;
    }

    public void setZpData(T zpData) {
        this.zpData = zpData;
    }

    static {
        FAIL = failed(FAIL_CODE, FAIL_MSG);
        OK = failed(OK_CODE, OK_MSG);
    }
}
