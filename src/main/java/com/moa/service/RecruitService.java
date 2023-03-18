package com.moa.service;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.category.CategoryRepository;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.RecruitPostRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruitService {
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Long post(final Long userId, final RecruitPostRequest request, final List<Long> categoryId) {
        User user = userRepository.getReferenceById(userId);
        Recruitment recruitment = request.toEntity(user);
        setAssociation(request, categoryId, recruitment);
        return recruitmentRepository.save(recruitment).getId();
    }

    private void setAssociation(RecruitPostRequest request, List<Long> categoryId, Recruitment recruitment) {
        List<RecruitMember> members = request.memberFields().stream().map(field -> new RecruitMember(field.field(), field.total())).toList();
        List<RecruitCategory> list = categoryId.stream()
                .map(categoryRepository::getReferenceById)
                .toList()
                .stream()
                .map(RecruitCategory::new)
                .toList();

        recruitment.setMembers(members);
        recruitment.setCategory(list);
    }
}
