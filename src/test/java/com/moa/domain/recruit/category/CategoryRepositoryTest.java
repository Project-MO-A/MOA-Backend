package com.moa.domain.recruit.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.save(new Category("개발"));
    }

    @Test
    void findExistName() {
        List<String> categories = List.of("개발", "웹", "프로젝트");
        List<String> existName = categoryRepository.findExistName(categories);

        assertThat(existName).containsOnly("개발");
    }

    @Test
    void findIdByName() {
        List<String> categories = List.of("웹", "프로젝트");
        categoryRepository.saveAll(categories.stream().map(Category::new).toList());
        List<Long> id = categoryRepository.findIdByName(categories);

        assertThat(id.size()).isEqualTo(2);
        assertThat(id.get(0)).isGreaterThan(1);
    }
}