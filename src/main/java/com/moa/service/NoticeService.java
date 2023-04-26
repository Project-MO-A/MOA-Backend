package com.moa.service;

import com.moa.domain.member.AttendMember;
import com.moa.domain.member.AttendMemberRepository;
import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.dto.notice.NoticesResponse;
import com.moa.dto.notice.PostNoticeRequest;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import static com.moa.service.util.KakaoUtils.getRecommendedLocationByKakao;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    @Value("${kakao.api.path}")
    private String path;
    @Value("${kakao.api.key}")
    private String restApiKey;

    private final NoticeRepository noticeRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final AttendMemberRepository attendMemberRepository;

    public Long post(Long recruitmentId, PostNoticeRequest request) {
        Recruitment recruitment = recruitmentRepository.getReferenceById(recruitmentId);
        Notice savedNotice = noticeRepository.save(request.toEntity(recruitment));
        attendMemberRepository.saveFromApplimentMember(savedNotice, recruitmentId);
        return savedNotice.getId();
    }

    public Long update(Long recruitmentId, Long noticeId, UpdateNoticeRequest request) {
        Recruitment recruitment = recruitmentRepository.getReferenceById(recruitmentId);
        noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException(NOTICE_NOT_FOUND))
                .update(recruitment, request);
        return noticeId;
    }

    public Long delete(Long recruitmentId, Long noticeId) {
        Notice notice = noticeRepository.findByIdAndRecruitmentId(noticeId, recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException(NOTICE_NOT_FOUND));
        noticeRepository.delete(notice);
        return noticeId;
    }

    @Transactional(readOnly = true)
    public NoticesResponse findAll(Long recruitmentId) {
        List<Notice> notices = noticeRepository.findAllByRecruitmentId(recruitmentId);
        List<Long> noticeIds = notices.stream()
                .map(Notice::getId)
                .toList();
        List<AttendMember> attendMembers = attendMemberRepository.findAllByNoticeIdIn(noticeIds);
        return new NoticesResponse(notices, attendMembers);
    }

    public String finishVote(Long recruitmentId, Long noticeId) {
        Notice notice = noticeRepository.findFetchMemberByIdAndRecruitmentId(noticeId, recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException(NOTICE_NOT_FOUND));
        String recommendedLocation = findLocation(
                notice.getAttendMembers().stream()
                        .filter(member -> member.getAttendance().equals(ATTENDANCE))
                        .toList()
        );
        notice.recommend(recommendedLocation);
        notice.finishVote();
        return recommendedLocation;
    }

    private String findLocation(List<AttendMember> attendMembers) {
        return getRecommendedLocationByKakao(attendMembers, path, restApiKey)
                .block()
                .documents()
                .get(0)
                .place_name();
    }
}
