package com.moa.domain.recruit.tag;

import com.moa.base.RepositoryTest;
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

    @DisplayName("findExistName - 이미 DB에 존재하는 태그명을 조회한다.")
    @Test
    void findExistName() {
        //given
        List<String> tags = List.of("tag1", "tag2", "tag3");
        tagRepository.save(new Tag("tag1"));

        //when
        List<String> existName = tagRepository.findExistName(tags);

        //then
        assertThat(existName).containsOnly("tag1");
    }

    @DisplayName("findExistName - 주어진 태그명이 모두 DB에 없을때 빈 List가 반환된다.")
    @Test
    void findExistNameEmpty() {
        //given
        List<String> tags = List.of("tag1", "tag2", "tag3");

        //when
        List<String> existName = tagRepository.findExistName(tags);

        //then
        assertThat(existName).isEmpty();
    }

    @DisplayName("findIdByName - 태그명을 통해 Tag id를 조회한다.")
    @Test
    void findIdByName() {
        //given
        List<String> tags = List.of("tag1", "tag2", "tag3");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());

        //when
        List<Long> id = tagRepository.findIdByName(tags);

        //then
        assertAll(
                () -> assertThat(id.size()).isEqualTo(3),
                () -> assertThat(id.get(0)).isGreaterThan(1)
        );
    }

    @DisplayName("findIdByName - 태그명이 DB에 없을 경우 빈 List 가 반환된다")
    @Test
    void findIdByNameEmpty() {
        //given
        List<String> tags = List.of("tag1", "tag2", "tag3");

        //when
        List<Long> id = tagRepository.findIdByName(tags);

        //then
        assertThat(id).isEmpty();
    }

    @DisplayName("findAllByName - 태그명을 통해 엔티티 리스트를 조회한다.")
    @Test
    void findAllByName() {
        //given
        List<String> tags = List.of("tag1", "tag2", "tag3");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());

        //when
        List<Tag> tagList = tagRepository.findAllByName(tags);

        //then
        assertThat(tagList.size()).isEqualTo(3);
    }
}