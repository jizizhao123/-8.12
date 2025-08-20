package com.neutech.vo;

public class ResultJson<T> {
    private Integer code;
    private T content;
    private String message;

    public Integer getCode() {
        return code;
    }

    public Object getContent() {
        return content;
    }

    public String getMessage() {
        return message;
    }

    public ResultJson(ResultCode resultCode, T content, String message) {
        this.code = resultCode.getValue();
        this.content = content;
        this.message = message;
    }
    public static <T> ResultJson<T> getInstance(ResultCode resultCode, T content, String message) {
        return new ResultJson(resultCode, content, message);
    }
    public static <T> ResultJson<T> success(T content, String message) {
        return getInstance(ResultCode.SUCCESS, content, message);
    }
    public static <T> ResultJson<T> success(T content) {
        return success(content, null);
    }
    public static <T> ResultJson<T> failed(T content, String message) {
        return getInstance(ResultCode.FAILED, content, message);
    }
    public static <T> ResultJson<T> failed(String message) {
        return failed(null, message);
    }
}
