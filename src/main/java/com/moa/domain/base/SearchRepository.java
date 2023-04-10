package com.moa.domain.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SearchRepository<T> {
    T searchOne(Map<String, String> searchCondition);
    Page<T> searchAll(Map<String, String> searchCondition, Pageable pageable);
}
