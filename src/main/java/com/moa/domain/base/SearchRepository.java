package com.moa.domain.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;

public interface SearchRepository<T> {
    T searchOne(Map<String, String> searchCondition);
    Page<T> searchPage(Map<String, String> searchCondition, Pageable pageable);
    Slice<T> searchSlice(Map<String, String> searchCondition, Pageable pageable);
}
