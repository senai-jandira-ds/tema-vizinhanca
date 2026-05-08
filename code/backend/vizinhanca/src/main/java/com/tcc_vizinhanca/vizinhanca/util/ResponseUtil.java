package com.tcc_vizinhanca.vizinhanca.util;

import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;

import java.time.LocalDate;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();

        response.setStatus(true);
        response.setStatusCode(200);
        response.setMessage(message);
        response.setResponse(data);

        return response;
    }

    public static ApiResponse<Void> error(int statusCode, String message) {
        ApiResponse<Void> response = new ApiResponse<>();

        response.setStatus(false);
        response.setStatusCode(statusCode);
        response.setMessage(message);

        return response;
    }
}
