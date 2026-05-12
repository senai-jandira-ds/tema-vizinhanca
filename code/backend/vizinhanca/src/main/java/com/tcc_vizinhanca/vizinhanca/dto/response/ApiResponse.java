package com.tcc_vizinhanca.vizinhanca.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "status_code",
        "developer",
        "api_description",
        "version",
        "request_date",
        "message",
        "response"
})
public class ApiResponse<T> {

    private boolean status;

    @JsonProperty("status_code")
    private int statusCode;

    private String developer = "Leonardo Scotti";

    @JsonProperty("api_description")
    private String apiDescription = "API do sistema Vizinhança.";

    private String version = "1.0.04.26";

    @JsonProperty("request_date")
    private String requestDate = LocalDate.now().toString();

    private String message;

    private T response;
}
