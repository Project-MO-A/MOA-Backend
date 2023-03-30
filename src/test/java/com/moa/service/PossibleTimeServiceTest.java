package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.member.RecruitMemberRepository;
import com.moa.domain.possible.Day;
import com.moa.domain.possible.PossibleTime;
import com.moa.domain.possible.PossibleTimeRepository;
import com.moa.dto.possible.PossibleTimeData;
import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.exception.service.InvalidTimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static com.moa.InitData.*;
import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("data")
@Transactional
@SpringBootTest
class PossibleTimeServiceTest {
    @Autowired
    PossibleTimeService possibleTimeService;
    @Autowired
    ApplimentMemberRepository applimentMemberRepository;
    @Autowired
    RecruitMemberRepository recruitMemberRepository;
    @Autowired
    PossibleTimeRepository possibleTimeRepository;

    private ApplimentMember APPLIMENT1;
    private ApplimentMember APPLIMENT2;

    @BeforeEach
    void setUp() {
        RecruitMember backend = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("백엔드", RECRUITMENT1.getId()).get();
        RecruitMember front = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("프론트엔드", RECRUITMENT1.getId()).get();
        APPLIMENT1 = applimentMemberRepository.save(new ApplimentMember(backend, USER3, APPROVED));
        APPLIMENT2 = applimentMemberRepository.save(new ApplimentMember(front, USER2, APPROVED));

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.TUESDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(20, 0))
                .applimentMember(APPLIMENT1)
                .build());

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.MONDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(22, 0))
                .applimentMember(APPLIMENT1)
                .build());

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.FRIDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(22, 0))
                .applimentMember(APPLIMENT1)
                .build());

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.SUNDAY)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(20, 0))
                .applimentMember(APPLIMENT2)
                .build());

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.SATURDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(22, 0))
                .applimentMember(APPLIMENT2)
                .build());

        possibleTimeRepository.save(PossibleTime.builder()
                .day(Day.THURSDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(22, 0))
                .applimentMember(APPLIMENT2)
                .build());
    }

    @AfterEach
    void tearDown() {
        possibleTimeRepository.deleteAll();
    }

    @DisplayName("getAllMembersTimeList - 모든 승인된 멤버의 시간을 가져온다")
    @Test
    void getAllMembersTimeList() {
        //when
        List<PossibleTimeResponse> allMembersTimeList = possibleTimeService.getAllMembersTimeList(RECRUITMENT1.getId());

        //then
        assertThat(allMembersTimeList.size()).isEqualTo(3);
    }

    @DisplayName("getTimeList - 개인의 시간을 가져온다")
    @Test
    void getTimeList() {
        //when
        PossibleTimeResponse timeList = possibleTimeService.getTimeList(RECRUITMENT1.getId(), USER3.getId());

        //then
        assertThat(timeList.getPossibleTimeData().size()).isEqualTo(3);
        assertThat(timeList.getPossibleTimeData().get(0).startTime().getHour()).isGreaterThan(14);
        assertThat(timeList.getPossibleTimeData().get(2).endTime().getHour()).isGreaterThan(19);
    }

    @DisplayName("setTime - 개인의 시간을 설정한다.")
    @Test
    void setTime() {
        //given
        PossibleTimeRequest possibleTimeRequest = new PossibleTimeRequest(List.of(
                PossibleTimeData.builder()
                        .day(Day.WEDNESDAY.name())
                        .startTime(LocalTime.of(3, 10))
                        .endTime(LocalTime.of(9, 10)).build(),
                PossibleTimeData.builder()
                        .day(Day.SATURDAY.name())
                        .startTime(LocalTime.of(3, 10))
                        .endTime(LocalTime.of(9, 10)).build()));

        //when
        possibleTimeService.setTime(possibleTimeRequest, RECRUITMENT1.getId(), USER3.getId());

        //then
        List<PossibleTime> allByApplyId = possibleTimeRepository.findAllByApplimentMemberId(APPLIMENT1.getId());
        assertThat(allByApplyId.size()).isEqualTo(2);
        assertThat(allByApplyId.get(0).getStartTime().getHour()).isEqualTo(3);
        assertThat(allByApplyId.get(0).getEndTime().getHour()).isEqualTo(9);
    }

    @DisplayName("setTime - 개인의 시간을 설정하는데 실패한다. (startTime 보다 endTime이 더 큼)")
    @Test
    void setTimeFail() {
        //given
        PossibleTimeRequest possibleTimeRequest = new PossibleTimeRequest(List.of(
                PossibleTimeData.builder()
                        .day(Day.WEDNESDAY.name())
                        .startTime(LocalTime.of(10, 10))
                        .endTime(LocalTime.of(9, 10)).build(),
                PossibleTimeData.builder()
                        .day(Day.SATURDAY.name())
                        .startTime(LocalTime.of(8, 20))
                        .endTime(LocalTime.of(9, 10)).build()));

        //when
        assertThatThrownBy(() -> possibleTimeService.setTime(possibleTimeRequest, RECRUITMENT1.getId(), USER3.getId()))
                .isInstanceOf(InvalidTimeException.class);
    }
}