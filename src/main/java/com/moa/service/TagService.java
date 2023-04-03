package com.moa.service;

import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.recruit.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {
    private final TagRepository tagRepository;

    public Optional<List<Tag>> updateAndReturn(List<String> tags) {
        if (tags == null || tags.isEmpty()) return Optional.empty();
        update(tags);
        return Optional.of(tagRepository.findAllByName(tags));
    }

    public List<Tag> update(List<String> tags) {
        List<String> exist = tagRepository.findExistName(tags);
        return tagRepository.saveAll(getTargetTag(tags, exist));
    }

    @Transactional(readOnly = true)
    public List<Long> getId(List<String> tags) {
        return tagRepository.findIdByName(tags);
    }

    private List<Tag> getTargetTag(List<String> givenTag, List<String> exist) {
        return givenTag.stream()
                .filter(tag -> !exist.contains(tag))
                .map(Tag::new)
                .toList();
    }
}
