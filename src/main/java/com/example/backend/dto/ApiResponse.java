package com.example.backend.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private String resCd;
    private String resMsg;
    private T data;

    public static <T> ApiResponse<T> success(String resMsg, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResCd("S000");
        response.setResMsg(resMsg);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> fail(String resMsg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResCd("S001");
        response.setResMsg(resMsg);
        return response;
    }
}
