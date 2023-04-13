package com.moa.dto;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Getter
public class SliceResponse {
	private final boolean first;
	private final boolean last;
	private final int currentPage;
	private final int size;
	private final Sort sort;
	private final boolean hasNext;

	public SliceResponse(Slice slice) {
		this.first = slice.isFirst();
		this.last = slice.isLast();
		this.currentPage = slice.getNumber();
		this.size = slice.getSize();
		this.sort = slice.getSort();
		this.hasNext = slice.hasNext();
	}
}