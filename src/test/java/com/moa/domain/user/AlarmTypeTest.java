package com.moa.domain.user;

import org.junit.jupiter.api.Test;

import static com.moa.domain.user.AlarmType.PARTICIPATION_APPROVAL;
import static org.assertj.core.api.Assertions.assertThat;

class AlarmTypeTest {
    final Long RELATE_ID = 13L;

    @Test
    void uriFormatTest() {
        assertThat(PARTICIPATION_APPROVAL.getRedirectURI(RELATE_ID)).isEqualTo("/recruitment/" + RELATE_ID);
    }

    @Test
    void messageFormatTest() {
        assertThat(PARTICIPATION_APPROVAL.getMessage(RELATE_ID)).isEqualTo( RELATE_ID + "번 모집글에 대한 참여 요청이 승인되었습니다.");
    }
}