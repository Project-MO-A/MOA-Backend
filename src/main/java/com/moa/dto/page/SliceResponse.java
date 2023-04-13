package com.moa.dto.page;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {
    protected final List<T> content;
    protected final SortResponse sort;
    protected final int currentPage;
    protected final int size;
    protected final boolean first;
    protected final boolean last;

    public SliceResponse(Slice<T> sliceContent) {
        this.content = sliceContent.getContent();
        this.sort = new SortResponse(sliceContent.getSort());
        this.currentPage = sliceContent.getNumber() + 1;
        this.size = sliceContent.getSize();
        this.first = sliceContent.isFirst();
        this.last = sliceContent.isLast();
    }
}
