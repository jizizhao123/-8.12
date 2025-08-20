package com.neutech.vo;

public enum ResultCode {
    SUCCESS(200),
    FAILED(500),
    UNAUTHORIZED(401),
    FORBID(403);
    private Integer value;

    public Integer getValue() {
        return value;
    }

    ResultCode(Integer value) {
        this.value = value;
    }
}
