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
import com.moa.service.graham.GrahamAlgo.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.moa.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import static com.moa.service.graham.GrahamAlgo.getOutSide;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    @Value("${kakao.api.path}")
    private final String path;
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
        String recommendedLocation = findLocation(notice.getAttendMembers());
        notice.recommend(recommendedLocation);
        notice.finishVote();
        return recommendedLocation;
    }

    private String findLocation(List<AttendMember> attendMembers) {
        Mono<Kakao> response = getRecommendedLocationByKakao(getMiddlePoint(attendMembers));
        return response.block().documents().get(0).place_name();
    }

    private static Point getMiddlePoint(List<AttendMember> attendMembers) {
        List<Point> points = attendMembers.stream()
                .map(member -> new Point(member.getUser().getLocationLatitude(), member.getUser().getLocationLongitude()))
                .toList();
        List<Point> outSide = getOutSide(points);

        double sumLatitude = 0.0;
        double sumLongitude = 0.0;
        for (Point point : outSide) {
            sumLatitude += point.getX();
            sumLongitude += point.getY();
        }

        return new Point(sumLatitude / outSide.size(), sumLongitude / outSide.size());
    }

    private Mono<Kakao> getRecommendedLocationByKakao(Point middlePoint) {
        return WebClient.create(path)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParams(createParams(middlePoint)).build())
                .header(AUTHORIZATION, restApiKey)
                .retrieve().bodyToMono(Kakao.class);
    }

    private static MultiValueMap<String, String> createParams(Point middlePoint) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("x", String.valueOf(middlePoint.getX()));
        params.add("y", String.valueOf(middlePoint.getY()));
        params.add("radius", "2000");
        params.add("query", "역");
        params.add("category_group_code", "SW8");
        return params;
    }
}
