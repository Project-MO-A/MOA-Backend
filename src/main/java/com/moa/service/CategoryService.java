package com.moa.service;

import com.moa.domain.recruit.category.Category;
import com.moa.domain.recruit.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Optional<List<Long>> updateAndReturnId(List<String> categories) {
        if (categories == null || categories.isEmpty()) return Optional.empty();
        update(categories);
        return Optional.of(getId(categories));
    }

    public List<Category> update(List<String> categories) {
        List<String> exist = categoryRepository.findExistName(categories);
        return categoryRepository.saveAll(getTargetCategory(categories, exist));
    }

    @Transactional(readOnly = true)
    public List<Long> getId(List<String> categories) {
        return categoryRepository.findIdByName(categories);
    }

    private List<Category> getTargetCategory(List<String> givenCategory, List<String> exist) {
        List<String> target = new ArrayList<>();
        for (String given : givenCategory) {
            if (!exist.contains(given)) target.add(given);
        }

        return target
                .stream()
                .map(Category::new)
                .toList();
    }
}
