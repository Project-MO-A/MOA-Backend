package com.moa.dto;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class PageCustomResponse<T> {

	private final List<T> content;
	private final SliceResponse pageResponse;


	public PageCustomResponse(List<T> content, Slice slice) {
		this.content = content;
		this.pageResponse = new SliceResponse(slice);
	}
}