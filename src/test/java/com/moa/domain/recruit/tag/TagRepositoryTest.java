package com.moa.domain.recruit.tag;

import com.moa.domain.RepositoryTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RepositoryTest
class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.save(new Tag("개발"));
    }

    @DisplayName("findExistName - 이미 DB에 존재하는 태그명을 조회한다.")
    @Test
    void findExistName() {
        //given
        List<String> tags = List.of("개발", "웹", "프로젝트");

        //when
        List<String> existName = tagRepository.findExistName(tags);

        //then
        assertThat(existName).containsOnly("개발");
    }

    @DisplayName("findExistName - 주어진 태그명이 모두 DB에 없을때 빈 List가 반환된다.")
    @Test
    void findExistNameEmpty() {
        //given
        List<String> tags = List.of("백엔드", "웹", "프로젝트");

        //when
        List<String> existName = tagRepository.findExistName(tags);

        //then
        assertThat(existName).isEmpty();
    }

    @DisplayName("findIdByName - 태그명을 통해 Tag id를 조회한다.")
    @Test
    void findIdByName() {
        //given
        List<String> categories = List.of("웹", "프로젝트");
        tagRepository.saveAll(categories.stream().map(Tag::new).toList());

        //when
        List<Long> id = tagRepository.findIdByName(categories);

        //then
        assertAll(
                () -> assertThat(id.size()).isEqualTo(2),
                () -> assertThat(id.get(0)).isGreaterThan(1)
        );
    }

    @DisplayName("findIdByName - 태그명이 DB에 없을 경우 빈 List 가 반환된다")
    @Test
    void findIdByNameEmpty() {
        //given
        List<String> categories = List.of("웹", "프로젝트");

        //when
        List<Long> id = tagRepository.findIdByName(categories);

        assertThat(id).isEmpty();
    }
}