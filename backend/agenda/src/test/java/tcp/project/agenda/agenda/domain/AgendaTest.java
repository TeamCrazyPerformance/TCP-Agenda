package tcp.project.agenda.agenda.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.agenda.exception.NotMemberAgendaException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_TITLE;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNoTitleAgendaCreateRequest;

@ExtendWith(MockitoExtension.class)
class AgendaTest {

    @Mock
    Member member;

    @Mock
    Grade grade;

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

    @Test
    @DisplayName("안건 작성자일 경우 예외가 발생하면 안 됨")
    void validateAgendaOwner() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade, BASIC_AGENDA_CLOSED_AT);
        Long memberId = 1L;
        given(member.getId()).willReturn(memberId);

        //when then
        assertThatNoException()
                .isThrownBy(() -> agenda.validateOwner(memberId));
    }

    @Test
    @DisplayName("안건 작성자가 아닐 경우 예외가 발생해야 함")
    void validateAgendaOwner_exception() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade, BASIC_AGENDA_CLOSED_AT);
        given(member.getId()).willReturn(1L);
        Long notMemberId = 999L;

        //when then
        assertThatThrownBy(() -> agenda.validateOwner(notMemberId))
            .isInstanceOf(NotMemberAgendaException.class);
    }

    @Test
    @DisplayName("안건이 마감 되어야 함")
    void closeTest() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade, BASIC_AGENDA_CLOSED_AT);

        //when
        agenda.close();

        //then
        assertThat(agenda.isClosed()).isTrue();
    }

    @Test
    @DisplayName("안건이 마감 되어야 함")
    void closeTest_alreadyClosedException() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade, BASIC_AGENDA_CLOSED_AT);
        agenda.close();

        //when then
        assertThatThrownBy(agenda::close)
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }
}