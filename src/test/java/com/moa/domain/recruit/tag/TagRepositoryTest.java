package com.moa.domain.recruit.tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.save(new Tag("개발"));
    }

    @Test
    void findExistName() {
        List<String> categories = List.of("개발", "웹", "프로젝트");
        List<String> existName = tagRepository.findExistName(categories);

        assertThat(existName).containsOnly("개발");
    }

    @Test
    void findIdByName() {
        List<String> categories = List.of("웹", "프로젝트");
        tagRepository.saveAll(categories.stream().map(Tag::new).toList());
        List<Long> id = tagRepository.findIdByName(categories);

        assertThat(id.size()).isEqualTo(2);
        assertThat(id.get(0)).isGreaterThan(1);
    }
}