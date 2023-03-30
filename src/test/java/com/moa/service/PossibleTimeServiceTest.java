package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.ApplimentSearchRepository;
import com.moa.domain.possible.Day;
import com.moa.domain.possible.PossibleTime;
import com.moa.domain.possible.PossibleTimeRepository;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.possible.PossibleTimeData;
import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.InvalidTimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.moa.support.fixture.ApplimentFixture.APPROVED_MEMBER;
import static com.moa.support.fixture.PossibleTimeFixture.*;
import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PossibleTimeServiceTest {
    @Mock
    private ApplimentSearchRepository applimentSearchRepository;
    @Mock
    private PossibleTimeRepository possibleTimeRepository;
    @Mock
    private ApplimentMemberRepository applimentMemberRepository;

    @InjectMocks
    private PossibleTimeService possibleTimeService;

    @DisplayName("getAllMembersTimeList - 모든 승인된 멤버의 시간을 가져온다")
    @Test
    void getAllMembersTimeList() {
        //given
        final Long recruitmentId = 3L;
        List<ApprovedMemberResponse> responses = List.of(
                new ApprovedMemberResponse(2L, 2L, "nick", "백엔드", 3.5),
                new ApprovedMemberResponse(3L, 3L, "nicka", "프론트엔드", 3.5)
        );

        List<PossibleTime> possibleTimes1 = List.of(
                MONDAY_DAYTIME.빈_객체_생성(),
                TUESDAY_ALL.빈_객체_생성()
        );
        List<PossibleTime> possibleTimes2 = List.of(
                MONDAY_DAYTIME.빈_객체_생성(),
                TUESDAY_ALL.빈_객체_생성()
        );

        given(applimentSearchRepository.findAllApprovedMembers(recruitmentId))
                .willReturn(responses);
        given(possibleTimeRepository.findAllByApplimentMemberId(2L))
                .willReturn(possibleTimes1);
        given(possibleTimeRepository.findAllByApplimentMemberId(3L))
                .willReturn(possibleTimes2);

        ///when
        List<PossibleTimeResponse> allMembersTimeList = possibleTimeService.getAllMembersTimeList(recruitmentId);

        //then
        assertAll(
                () -> assertThat(allMembersTimeList.size()).isEqualTo(2),
                () -> assertThat(allMembersTimeList.get(0).getNickname()).isEqualTo("nick"),
                () -> verify(applimentSearchRepository).findAllApprovedMembers(recruitmentId),
                () -> verify(possibleTimeRepository, times(2)).findAllByApplimentMemberId(any(Long.class))
        );
    }

    @DisplayName("getAllMembersTimeList - 잘못된 모집글 ID 입력시 빈 리스트가 반환된다.")
    @Test
    void getAllMembersTimeList_invalidRecruitId() {
        //given
        final Long recruitmentId = 3L;

        given(applimentSearchRepository.findAllApprovedMembers(recruitmentId))
                .willReturn(new ArrayList<>());

        ///when
        List<PossibleTimeResponse> allMembersTimeList = possibleTimeService.getAllMembersTimeList(recruitmentId);

        //then
        assertAll(
                () -> assertThat(allMembersTimeList).isEmpty(),
                () -> verify(applimentSearchRepository).findAllApprovedMembers(recruitmentId),
                () -> verify(possibleTimeRepository, times(0)).findAllByApplimentMemberId(any(Long.class))
        );
    }

    @DisplayName("getTimeList - 개인의 시간을 가져온다")
    @Test
    void getTimeList() {
        //given
        final Long recruitmentId = 3L;
        final Long userId = 2L;
        ApplimentMember applimentMember = APPROVED_MEMBER.생성(PINGU.생성(), BACKEND_MEMBER.생성());

        List<PossibleTime> possibleTimes1 = List.of(
                MONDAY_DAYTIME.빈_객체_생성(),
                TUESDAY_ALL.빈_객체_생성(),
                SATURDAY_ALL.빈_객체_생성()
        );

        given(applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId))
                .willReturn(Optional.ofNullable(applimentMember));
        given(possibleTimeRepository.findAllByApplimentMemberId(any()))
                .willReturn(possibleTimes1);

        //when
        PossibleTimeResponse timeList = possibleTimeService.getTimeList(recruitmentId, userId);

        //then
        assertAll(
                () -> assertThat(timeList.getPossibleTimeData().size()).isEqualTo(3),
                () -> verify(applimentMemberRepository).findByRecruitIdAndUserId(recruitmentId, userId),
                () -> verify(possibleTimeRepository).findAllByApplimentMemberId(any())
        );
    }

    @DisplayName("getTimeList - 잘못된 모집글 아이디를 입력하면 예외가 발생한다.")
    @Test
    void getTimeList_invalidRecruitId() {
        //given
        Long invalidId = 99L;
        given(applimentMemberRepository.findByRecruitIdAndUserId(invalidId, 3L))
                .willThrow(EntityNotFoundException.class);

        //then
        assertAll(
                () -> assertThatThrownBy(() -> possibleTimeService.getTimeList(invalidId, 3L))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(applimentMemberRepository).findByRecruitIdAndUserId(invalidId, 3L),
                () -> verify(possibleTimeRepository, times(0)).findAllByApplimentMemberId(any())
        );
    }

    @DisplayName("getTimeList - 잘못된 유저 아이디를 입력하면 예외가 발생한다.")
    @Test
    void getTimeList_invalidUserId() {
        //given
        Long invalidId = 99L;
        given(applimentMemberRepository.findByRecruitIdAndUserId(3L, invalidId))
                .willThrow(EntityNotFoundException.class);

        //then
        assertAll(
                () -> assertThatThrownBy(() -> possibleTimeService.getTimeList(3L, invalidId))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(applimentMemberRepository).findByRecruitIdAndUserId(3L, invalidId),
                () -> verify(possibleTimeRepository, times(0)).findAllByApplimentMemberId(any())
        );
    }

    @DisplayName("setTime - 개인의 시간을 설정한다.")
    @Test
    void setTime() {
        //given
        final Long recruitmentId = 3L;
        final Long userId = 2L;
        ApplimentMember applimentMember = APPROVED_MEMBER.생성(PINGU.생성(), BACKEND_MEMBER.생성());
        PossibleTimeRequest timeRequest = new PossibleTimeRequest(
                Stream.of(        MONDAY_DAYTIME.빈_객체_생성(),
                                TUESDAY_ALL.빈_객체_생성(),
                                SATURDAY_ALL.빈_객체_생성())
                        .map(time -> new PossibleTimeData(time.getDay().name(), time.getStartTime(), time.getEndTime()))
                        .toList()
        );

        given(applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId))
                .willReturn(Optional.ofNullable(applimentMember));

        //when
        possibleTimeService.setTime(timeRequest, recruitmentId, userId);

        //then
        assertAll(
                () -> verify(applimentMemberRepository).findByRecruitIdAndUserId(recruitmentId, userId),
                () -> verify(possibleTimeRepository).deleteAllByApplimentMember(applimentMember),
                () -> verify(possibleTimeRepository, times(3)).save(any(PossibleTime.class))
        );
    }

    @DisplayName("setTime - 개인의 시간을 설정하는데 실패한다. (startTime 보다 endTime이 더 큼)")
    @Test
    void setTimeFail() {
        //given
        final Long recruitmentId = 3L;
        final Long userId = 2L;
        ApplimentMember applimentMember = APPROVED_MEMBER.생성(PINGU.생성(), BACKEND_MEMBER.생성());
        PossibleTimeRequest timeRequest = new PossibleTimeRequest(
                List.of(
                        new PossibleTimeData(Day.MONDAY.name(), LocalTime.of(18, 0), LocalTime.of(17, 0)),
                        new PossibleTimeData(Day.TUESDAY.name(), LocalTime.of(10, 0), LocalTime.of(17, 0))
                )
        );

        given(applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId))
                .willReturn(Optional.ofNullable(applimentMember));

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> possibleTimeService.setTime(timeRequest, recruitmentId, userId))
                        .isExactlyInstanceOf(InvalidTimeException.class),
                () -> verify(applimentMemberRepository).findByRecruitIdAndUserId(recruitmentId, userId),
                () -> verify(possibleTimeRepository).deleteAllByApplimentMember(applimentMember),
                () -> verify(possibleTimeRepository, times(0)).save(any(PossibleTime.class))
        );
    }

    @DisplayName("setTime - 잘못된 모집글 아이디를 입력하면 예외가 발생한다.")
    @Test
    void setTime_invalidRecruitId() {
        //given
        Long invalidId = 99L;
        given(applimentMemberRepository.findByRecruitIdAndUserId(invalidId, 3L))
                .willThrow(EntityNotFoundException.class);
        PossibleTimeRequest timeRequest = new PossibleTimeRequest(
                List.of(
                        new PossibleTimeData(Day.MONDAY.name(), LocalTime.of(14, 0), LocalTime.of(17, 0)),
                        new PossibleTimeData(Day.TUESDAY.name(), LocalTime.of(10, 0), LocalTime.of(17, 0))
                )
        );

        //then
        assertAll(
                () -> assertThatThrownBy(() -> possibleTimeService.setTime(timeRequest, invalidId, 3L))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(possibleTimeRepository, times(0)).deleteAllByApplimentMember(any()),
                () -> verify(possibleTimeRepository, times(0)).save(any())
        );
    }

    @DisplayName("setTime - 잘못된 유저 아이디를 입력하면 예외가 발생한다.")
    @Test
    void setTime_invalidUserId() {
        //given
        Long invalidId = 99L;
        given(applimentMemberRepository.findByRecruitIdAndUserId(3L, invalidId))
                .willThrow(EntityNotFoundException.class);
        PossibleTimeRequest timeRequest = new PossibleTimeRequest(
                List.of(
                        new PossibleTimeData(Day.MONDAY.name(), LocalTime.of(14, 0), LocalTime.of(17, 0)),
                        new PossibleTimeData(Day.TUESDAY.name(), LocalTime.of(10, 0), LocalTime.of(17, 0))
                )
        );

        //then
        assertAll(
                () -> assertThatThrownBy(() -> possibleTimeService.setTime(timeRequest,3L, invalidId))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(possibleTimeRepository, times(0)).deleteAllByApplimentMember(any()),
                () -> verify(possibleTimeRepository, times(0)).save(any())
        );
    }
}