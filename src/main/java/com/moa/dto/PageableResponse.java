package com.moa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class PageableResponse {
	private boolean first;
	private boolean last;
	private int currentPage;
	private int size;
	private Sort sort;
	private boolean hasNext;

	public PageableResponse(Slice slice) {
		this.first = slice.isFirst();
		this.last = slice.isLast();
		this.currentPage = slice.getNumber();
		this.size = slice.getSize();
		this.sort = slice.getSort();
		this.hasNext = slice.hasNext();
	}
}