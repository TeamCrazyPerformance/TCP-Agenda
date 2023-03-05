package tcp.project.agenda.agenda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNoTitleAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.MemberFixture.getExecutiveMember;

@ExtendWith(MockitoExtension.class)
class AgendaTest {

    Member member;

    @Mock
    Grade grade;

    @BeforeEach
    void init() {
        member = getExecutiveMember();
    }

    @Test
    @DisplayName("content가 null일 경우 빈 문자열이 저장되어야 함")
    void createAgendaTest() throws Exception {
        //given
        AgendaCreateRequest request = getBasicAgendaCreateRequest();

        //when
        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), null, grade, request.getClosedAt());

        //then
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(""),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt())
        );
    }

    @Test
    @DisplayName("안건 제목이 비었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidTitle() throws Exception {
        //given
        AgendaCreateRequest request = getNoTitleAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), grade, request.getClosedAt()))
                .isInstanceOf(InvalidTitleException.class);
    }

    @Test
    @DisplayName("안건 제목이 비었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidClosedAt() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidClosedAtAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), grade, request.getClosedAt()))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }
}