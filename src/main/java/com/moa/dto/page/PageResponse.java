package com.moa.dto.page;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> extends SliceResponse<T> {
    private final long totalElement;
    private final int totalPage;

    public PageResponse(Page<T> pageContent) {
        super(pageContent);
        this.totalElement = pageContent.getTotalElements();
        this.totalPage = pageContent.getTotalPages();
    }
}
