package com.tcc_vizinhanca.vizinhanca.dto.request.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class ObjectUpdateRequest {

    private MultipartFile photo;
    private String title;
    private LocalDate deadline;
    private String description;
    private String status;

    @JsonProperty("category_id")
    private Long categoryId;
}
