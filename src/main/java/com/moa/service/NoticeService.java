package com.moa.service;

import com.moa.domain.member.AttendMember;
import com.moa.domain.member.AttendMemberRepository;
import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.dto.notice.Kakao;
import com.moa.dto.notice.NoticesResponse;
import com.moa.dto.notice.PostNoticeRequest;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.InvalidRequestException;
import com.moa.service.util.GrahamUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.global.exception.ErrorCode.CAN_NOT_STOP_VOTE;
import static com.moa.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import static com.moa.service.util.GrahamUtils.getOutSide;
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

    public GrahamUtils.Point finishVote(Long recruitmentId, Long noticeId) {
        Notice notice = noticeRepository.findFetchMemberByIdAndRecruitmentId(noticeId, recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException(NOTICE_NOT_FOUND));
        GrahamUtils.Point middlePoint = getMiddlePoint(notice.getAttendMembers().stream()
                .filter(member -> member.getAttendance().equals(ATTENDANCE))
                .toList());
        /*String recommendedLocation = findLocation(
                notice.getAttendMembers().stream()
                        .filter(member -> member.getAttendance().equals(ATTENDANCE))
                        .toList()
        );*/
        notice.recommend(middlePoint);
        notice.finishVote();
        return middlePoint;
    }

    private String findLocation(List<AttendMember> attendMembers) {
        List<Kakao.Documents> documents = getRecommendedLocationByKakao(attendMembers, path, restApiKey)
                .block()
                .documents();
        System.out.println("==========kakao info==========");
        for (Kakao.Documents document : documents) {
            System.out.println("document = " + document.place_name());
        }
        return documents
                .get(0)
                .place_name();
    }

    private GrahamUtils.Point getMiddlePoint(List<AttendMember> attendMembers) {
        if (attendMembers.size() == 0) {
            throw new InvalidRequestException(CAN_NOT_STOP_VOTE);
        }

        List<GrahamUtils.Point> points = attendMembers.stream()
                .map(member -> new GrahamUtils.Point(member.getUser().getLocationLatitude(), member.getUser().getLocationLongitude()))
                .toList();
        List<GrahamUtils.Point> outSide = getOutSide(new ArrayList<>(points));

        System.out.println("==========outside points with member point============");
        double sumLatitude = 0.0;
        double sumLongitude = 0.0;
        for (GrahamUtils.Point point : outSide) {
            sumLatitude += point.getX();
            sumLongitude += point.getY();
        }

        return new GrahamUtils.Point(sumLatitude / outSide.size(), sumLongitude / outSide.size());
    }
}
