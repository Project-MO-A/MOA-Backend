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

    @DisplayName("update - 카테고리 저장에 성공한다.")
    @Test
    void update1() {
        //given
        List<String> list = List.of("프로젝트", "웹");

        //when
        categoryService.update(list);

        //then
        List<Long> idList = categoryRepository.findIdByName(list);
        assertThat(idList.get(0)).isGreaterThan(1);
        assertThat(idList.size()).isEqualTo(2);
    }

    @DisplayName("update - 중복되는 카테고리를 제외하고 저장한다.")
    @Test
    void update2() {
        //given
        List<String> list = List.of("개발", "프로젝트", "웹");

        //when
        List<Category> save = categoryService.update(list);

        //then
        List<Long> idList = categoryRepository.findIdByName(list);
        assertThat(idList.size()).isEqualTo(3);
        assertThat(save.size()).isEqualTo(2);
    }

    @DisplayName("updateAndReturnId - 중복된 카테고리 제외하고 저장 - 요청으로 들어온 카테고리 ID 값을 반환한다")
    @Test
    void updateAndReturnIdSuccess() {
        //given
        List<String> list = List.of("개발", "프로젝트", "웹");

        //when
        List<Long> idList = categoryService.updateAndReturnId(list).get();

        //then
        List<String> existName = categoryRepository.findExistName(list);
        assertThat(idList.size()).isEqualTo(3);
        assertThat(existName.size()).isEqualTo(3);
        assertThat(existName).containsOnly("개발", "프로젝트", "웹");
    }

    @DisplayName("updateAndReturnId - 요청 리스트가 null 일 경우 Optional.empty 가 반환된다.")
    @Test
    void updateAndReturnIdNull() {
        //given
        List<String> list = null;

        //when & then
        assertThat(categoryService.updateAndReturnId(list).isEmpty()).isTrue();
    }
}