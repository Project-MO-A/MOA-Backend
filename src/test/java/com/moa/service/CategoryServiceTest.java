package com.moa.service;

import com.moa.domain.recruit.category.Category;
import com.moa.domain.recruit.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.save(new Category("개발"));
    }

    @DisplayName("save - 카테고리 저장에 성공한다.")
    @Test
    void save1() {
        //given
        List<String> list = List.of("프로젝트", "웹");

        //when
        categoryService.update(list);

        //then
        List<Long> idList = categoryRepository.findIdByName(list);
        assertThat(idList.get(0)).isGreaterThan(1);
        assertThat(idList.size()).isEqualTo(2);
    }

    @DisplayName("save - 중복되는 카테고리를 제외하고 저장한다.")
    @Test
    void save2() {
        //given
        List<String> list = List.of("개발", "프로젝트", "웹");

        //when
        List<Category> save = categoryService.update(list);

        //then
        List<Long> idList = categoryRepository.findIdByName(list);
        assertThat(idList.size()).isEqualTo(3);
        assertThat(save.size()).isEqualTo(2);
    }
}