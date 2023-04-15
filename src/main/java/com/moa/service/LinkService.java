package com.moa.service;

import com.moa.domain.link.Link;
import com.moa.domain.link.LinkRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public Long addLink(Long userId, String uri) {
        User user = userRepository.getReferenceById(userId);
        return linkRepository.save(new Link(uri, user)).getId();
    }
}
