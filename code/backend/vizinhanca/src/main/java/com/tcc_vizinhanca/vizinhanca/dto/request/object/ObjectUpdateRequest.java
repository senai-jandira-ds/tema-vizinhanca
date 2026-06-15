package com.tcc_vizinhanca.vizinhanca.dto.request.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc_vizinhanca.vizinhanca.enums.StatusObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class ObjectUpdateRequest {

    private MultipartFile photo;
    private String title;
    private String deadline;
    private String description;
    private String status;

    @JsonProperty("category_id")
    private Long categoryId;
}
