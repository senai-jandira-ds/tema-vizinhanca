package com.tcc_vizinhanca.vizinhanca.util;

import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;

import java.time.LocalDateTime;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();

        response.setStatus(true);
        response.setStatusCode(200);
        response.setDeveloper("Leonardo Scotti");
        response.setApiDescription("API para manipular dados da locadora de filmes.");
        response.setVersion("1.0.4.26");
        response.setRequestDate(LocalDateTime.now().toString());
        response.setMessage(message);
        response.setResponse(data);

        return response;
    }

    public static ApiResponse<Void> error(int statusCode, String message) {
        ApiResponse<Void> response = new ApiResponse<>();

        response.setStatus(false);
        response.setStatusCode(statusCode);
        response.setDeveloper("Leonardo Scotti");
        response.setApiDescription("API para manipular dados da locadora de filmes.");
        response.setVersion("1.0.04.26");
        response.setRequestDate(LocalDateTime.now().toString());
        response.setMessage(message);

        return response;
    }
}
