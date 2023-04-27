package com.moa.dto;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class PageCustomResponse<T> {

	private final List<T> value;
	private final SliceResponse pageResponse;


	public PageCustomResponse(List<T> value, Slice slice) {
		this.value = value;
		this.pageResponse = new SliceResponse(slice);
	}
}