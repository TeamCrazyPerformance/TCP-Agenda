package tcp.project.agenda.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tcp.project.agenda.common.fixture.MemberFixture.getRegularMember;

class MemberTest {

    @Test
    @DisplayName("비밀번호가 맞으면 true를 반환함")
    void validatePasswordTest_true() throws Exception {
        //given
        Member member = getRegularMember();

        //when
        boolean res = member.validatePassword("980101");

        //then
        assertThat(res).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 틀리면 false를 반환함")
    void validatePasswordTest_false() throws Exception {
        //given
        Member member = getRegularMember();

        //when
        boolean res = member.validatePassword("123456");

        //then
        assertThat(res).isFalse();
    }
}