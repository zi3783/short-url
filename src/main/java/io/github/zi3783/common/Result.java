package io.github.zi3783.common;

import lombok.Data;

@Data
public class Result <T> {
    private int code;
    private T data;
    private String msg;

    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    //失败
    public static <T> Result<T> error(int code,String msg) {
        return new Result<>(code, null, msg);
    }
    //成功
    public static <T> Result<T> success() {
        return new Result<>(200, null, "success");
    }
    //成功 有数据
    public static <T> Result<T> success(T data) {
        return new Result<T>(200, data, "success");
    }
}
