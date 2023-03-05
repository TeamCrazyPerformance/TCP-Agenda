package tcp.project.agenda.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tcp.project.agenda.auth.exception.NoSuchGradeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static tcp.project.agenda.member.domain.GradeType.EXECUTIVE;
import static tcp.project.agenda.member.domain.GradeType.GENERAL;
import static tcp.project.agenda.member.domain.GradeType.REGULAR;

class GradeTypeTest {

    @Test
    @DisplayName("각 code에 맞는 enum을 리턴해야 함")
    void fromTest() throws Exception {
        //given
        String executiveCode = EXECUTIVE.getCode();
        String regularCode = REGULAR.getCode();
        String generalCode = GENERAL.getCode();

        //when
        GradeType executive = GradeType.from(executiveCode);
        GradeType regular = GradeType.from(regularCode);
        GradeType general = GradeType.from(generalCode);

        //then
        assertAll(
                () -> assertThat(executive).isEqualTo(EXECUTIVE),
                () -> assertThat(regular).isEqualTo(REGULAR),
                () -> assertThat(general).isEqualTo(GENERAL)
        );
    }

    @Test
    @DisplayName("없는 code일 경우 예외가 발생해야 함")
    void fromTest_noSuchGradeType() throws Exception {
        //given
        String wrongCode = "wrong code";

        //when then
        assertThatThrownBy(() -> GradeType.from(wrongCode))
                .isInstanceOf(NoSuchGradeException.class);
    }
}