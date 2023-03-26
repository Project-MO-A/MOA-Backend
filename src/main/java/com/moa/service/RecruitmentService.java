package com.moa.service;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.category.CategoryRepository;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.StatusResponse;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.global.exception.ErrorCode.RECRUITMENT_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Long post(final Long userId, final RecruitPostRequest request, final List<Long> categoryId) {
        User user = userRepository.getReferenceById(userId);
        Recruitment recruitment = request.toEntity(user, request.toMemberList(), getRecruitCategories(categoryId));
        return recruitmentRepository.save(recruitment).getId();
    }

    @Transactional(readOnly = true)
    public RecruitInfoResponse getInfo(final Long recruitId) {
        Recruitment recruitment = recruitmentRepository.findByIdFetchUser(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        return new RecruitInfoResponse(recruitment);
    }

    public Long update(final Long recruitId, final RecruitUpdateRequest request, final List<Long> categoryId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitment.update(request, getRecruitCategories(categoryId));
        return recruitment.getId();
    }

    public StatusResponse updateStatus(final Long recruitId, final Integer statusCode) {
        Recruitment recruitment = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitment.updateState(statusCode);
        return new StatusResponse(recruitment.getStatus().name());
    }

    public Long delete(final Long recruitId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitmentRepository.delete(recruitment);
        return recruitId;
    }

    private List<RecruitCategory> getRecruitCategories(List<Long> categoryId) {
        return categoryId.stream()
                .map(categoryRepository::getReferenceById)
                .toList()
                .stream()
                .map(RecruitCategory::new)
                .toList();
    }
}
