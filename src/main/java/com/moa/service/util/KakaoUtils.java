package com.moa.service.util;

import com.moa.domain.member.AttendMember;
import com.moa.dto.notice.Kakao;
import com.moa.global.exception.service.InvalidRequestException;
import com.moa.service.util.GrahamUtils.Point;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.moa.global.exception.ErrorCode.CAN_NOT_STOP_VOTE;
import static com.moa.service.util.GrahamUtils.getOutSide;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = PRIVATE)
public class KakaoUtils {

    public static Mono<Kakao> getRecommendedLocationByKakao(List<AttendMember> members, String path, String key) {
        return WebClient.create(path)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParams(createParams(members)).build())
                .header(AUTHORIZATION, key)
                .retrieve().bodyToMono(Kakao.class);
    }

    private static MultiValueMap<String, String> createParams(List<AttendMember> members) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Point middlePoint = getMiddlePoint(members);
        params.add("x", String.valueOf(middlePoint.getX()));
        params.add("y", String.valueOf(middlePoint.getY()));
        params.add("radius", "2000");
        params.add("query", "역");
        params.add("category_group_code", "SW8");
        return params;
    }

    private static Point getMiddlePoint(List<AttendMember> attendMembers) {
        if (attendMembers.size() == 0) {
            throw new InvalidRequestException(CAN_NOT_STOP_VOTE);
        }

        List<Point> points = attendMembers.stream()
                .map(member -> new Point(member.getUser().getLocationLatitude(), member.getUser().getLocationLongitude()))
                .toList();
        List<Point> outSide = getOutSide(new ArrayList<>(points));

        double sumLatitude = 0.0;
        double sumLongitude = 0.0;
        for (Point point : outSide) {
            sumLatitude += point.getX();
            sumLongitude += point.getY();
        }

        return new Point(sumLatitude / outSide.size(), sumLongitude / outSide.size());
    }
}
