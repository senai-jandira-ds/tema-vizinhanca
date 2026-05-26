package com.tcc_vizinhanca.vizinhanca.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@JsonPropertyOrder({
        "total_elements",
        "total_pages",
        "current_page",
        "page_size",
        "content"
})
public class PageResponse<T> {

    @JsonProperty("total_elements")
    private long totalElements;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("page_size")
    private int pageSize;

    private List<T> content;

    public <E> PageResponse(Page<E> page, Function<E, T> mapper) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.content = page.getContent().stream().map(mapper).toList();
    }
}