package com.moa.service;

import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.recruit.tag.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagService tagService;

    @DisplayName("getTargetTag - 이미 존재하는 태그를 제외한 리스트를 반환한다")
    @Test
    void getTargetTag() {
        //given
        List<String> tagNames = List.of("프로젝트", "웹", "백엔드");
        List<String> exists = List.of("프로젝트", "웹");

        //when
        List<Tag> targetTag = getTargetTag(tagNames, exists);

        //then
        assertAll(
                () -> assertThat(targetTag.size()).isEqualTo(1),
                () -> assertThat(targetTag.get(0).getName()).isEqualTo("백엔드")
        );
    }

    @DisplayName("update - 태그 저장에 성공한다.")
    @Test
    void updateSuccess() {
        //given
        List<String> tagNames = List.of("프로젝트", "웹");

        given(tagRepository.findExistName(tagNames))
                .willReturn(tagNames);
        given(tagRepository.saveAll(anyCollection()))
                .willReturn(tagNames.stream()
                        .map(Tag::new)
                        .toList());

        //when
        List<Tag> saveTags = tagService.update(tagNames);

        //then
        assertAll(
                () -> assertThat(saveTags.size()).isEqualTo(2),
                () -> verify(tagRepository).findExistName(tagNames),
                () -> verify(tagRepository).saveAll(anyCollection())
        );
    }

    @DisplayName("update - 저장된 태그명을 제외하고 저장한다.")
    @Test
    void updateSuccessDistinct() {
        //given
        List<String> tagNames = List.of("프로젝트", "웹", "백엔드");
        List<String> exists = List.of("프로젝트", "웹");
        List<Tag> targetTag = getTargetTag(tagNames, exists);

        given(tagRepository.findExistName(tagNames))
                .willReturn(exists);
        given(tagRepository.saveAll(anyCollection()))
                .willReturn(targetTag);

        //when
        List<Tag> saveTags = tagService.update(tagNames);

        //then
        assertAll(
                () -> assertThat(saveTags.size()).isEqualTo(1),
                () -> verify(tagRepository).findExistName(tagNames),
                () -> verify(tagRepository).saveAll(anyCollection())
        );
    }

    @DisplayName("update - 모든 태그가 이미 DB에 있다면 Empty List를 반환한다.")
    @Test
    void updateSuccessEmpty() {
        //given
        List<String> tagNames = List.of("프로젝트", "웹", "백엔드");
        List<String> exists = List.of("프로젝트", "웹", "백엔드");
        List<Tag> targetTag = getTargetTag(tagNames, exists);

        given(tagRepository.findExistName(tagNames))
                .willReturn(exists);
        given(tagRepository.saveAll(anyCollection()))
                .willReturn(targetTag);

        //when
        List<Tag> saveTags = tagService.update(tagNames);

        //then
        assertAll(
                () -> assertThat(saveTags).isEmpty(),
                () -> verify(tagRepository).findExistName(tagNames),
                () -> verify(tagRepository).saveAll(anyCollection())
        );
    }

    @DisplayName("updateAndReturn - 주어진 태그를 저장한뒤 주어진 태그의 ID 값을 반환한다")
    @Test
    void updateAndReturnSuccess() {
        //given
        List<String> tagNames = List.of("프로젝트", "웹", "백엔드");
        List<String> exists = List.of("프로젝트");
        List<Tag> allTag = tagNames.stream().map(Tag::new).toList();
        List<Tag> targetTag = getTargetTag(tagNames, exists);

        given(tagRepository.findExistName(tagNames))
                .willReturn(exists);
        given(tagRepository.findAllByName(tagNames))
                .willReturn(allTag);
        given(tagRepository.saveAll(anyCollection()))
                .willReturn(targetTag);

        //when
        List<Tag> idList = tagService.updateAndReturn(tagNames).get();

        //then
        assertAll(
                () -> assertThat(idList.size()).isEqualTo(3),
                () -> verify(tagRepository).saveAll(anyCollection()),
                () -> verify(tagRepository).findExistName(tagNames),
                () -> verify(tagRepository).findAllByName(tagNames)
        );
    }

    @DisplayName("updateAndReturn - 요청 리스트가 null 일 경우 Optional.empty 가 반환된다.")
    @Test
    void updateAndReturnNull() {
        //given
        List<String> tagNames = null;

        //when & then
        assertAll(
                () -> assertThat(tagService.updateAndReturn(tagNames)).isEmpty(),
                () -> verify(tagRepository, times(0)).findExistName(tagNames),
                () -> verify(tagRepository, times(0)).saveAll(anyCollection()),
                () -> verify(tagRepository, times(0)).findAllByName(tagNames)
        );
    }

    @DisplayName("updateAndReturn - 요청 리스트가 empty List 일 경우 Optional.empty 가 반환된다.")
    @Test
    void updateAndReturnIdEmpty() {
        //given
        List<String> tagNames = new ArrayList<>();

        //when & then
        assertAll(
                () -> assertThat(tagService.updateAndReturn(tagNames)).isEmpty(),
                () -> verify(tagRepository, times(0)).findExistName(tagNames),
                () -> verify(tagRepository, times(0)).saveAll(anyCollection()),
                () -> verify(tagRepository, times(0)).findAllByName(tagNames)
        );
    }

    private List<Tag> getTargetTag(List<String> givenTag, List<String> exist) {
        return givenTag.stream()
                .filter(tag -> !exist.contains(tag))
                .map(Tag::new)
                .toList();
    }
}