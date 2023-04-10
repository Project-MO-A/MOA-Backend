package com.moa.domain.base;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SearchRepository<T> {
    T searchOne(Map<String, String> searchCondition);
    List<T> searchAll(Map<String, String> searchCondition, Pageable pageable);
    List<T> searchAll(Map<String, String> searchCondition, Pageable pageable, String orderField, String direction);
}
