package com.gp.common.core.result;

import java.io.Serializable;

import lombok.Data;

@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return result(null, 200, "操作成功");
    }

    public static <T> Result<T> success(T data) {
        return result(data, 200, "操作成功");
    }

    public static <T> Result<T> success(T data, String msg) {
        return result(data, 200, msg);
    }

    public static <T> Result<T> error() {
        return result(null, 500, "操作失败");
    }

    public static <T> Result<T> error(String msg) {
        return result(null, 500, msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return result(null, code, msg);
    }

    private static <T> Result<T> result(T data, Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setData(data);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

}
