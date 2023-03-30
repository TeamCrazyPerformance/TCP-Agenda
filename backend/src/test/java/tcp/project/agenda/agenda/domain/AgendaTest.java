package tcp.project.agenda.agenda.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidUpdateAlreadyVoteStartedAgendaException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.agenda.exception.NotTargetMemberException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_SELECTED_LIST_DTO;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_TITLE;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicNotVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaUpdateRequest;

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
        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), null, grade.getGradeType(), request.getClosedAt());

        //then
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(""),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt())
        );
    }

    @Test
    @DisplayName("안건 제목이 비었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidClosedAt() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidClosedAtAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), grade.getGradeType(), request.getClosedAt()))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }

    @Test
    @DisplayName("안건 작성자일 경우 예외가 발생하면 안 됨")
    void validateAgendaOwner() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);
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
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);
        given(member.getId()).willReturn(1L);
        Long notMemberId = 999L;

        //when then
        assertThatThrownBy(() -> agenda.validateOwner(notMemberId))
            .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("안건이 마감 되어야 함")
    void closeTest() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);

        //when
        agenda.close();

        //then
        assertThat(agenda.isClosed()).isTrue();
    }

    @Test
    @DisplayName("이미 마감이 된 안건인 경우 예외가 발생해야 함")
    void closeTest_alreadyClosedException() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);
        agenda.close();

        //when then
        assertThatThrownBy(agenda::close)
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표 대상 등급이 포함된 경우 아무 일도 일어나지 않음")
    void validateIsTargetGradeTest() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        List<Grade> grades = List.of(new Grade(GradeType.REGULAR), new Grade(GradeType.EXECUTIVE));

        //when then
        assertThatNoException()
                .isThrownBy(() -> agenda.validateIsTargetGrade(grades));
    }

    @Test
    @DisplayName("투표 대상이 아닌 경우 예외가 발생해야 함")
    void validateIsTargetGradeTest_alreadyClosedException() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        List<Grade> grades = List.of(new Grade(GradeType.GENERAL), new Grade(GradeType.EXECUTIVE));

        //when then
        assertThatThrownBy(() -> agenda.validateIsTargetGrade(grades))
                .isInstanceOf(NotTargetMemberException.class);
    }

    @Test
    @DisplayName("아직 투표가 시작되지 않은 안건일 경우, '제목, 내용, 마감 시간, 대상, 투표 항목'이 바뀔 수 있음")
    void updateTest_voteNotStarted() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();

        //when
        agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget());

        //then
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(request.getContent()),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt()),
                () -> assertThat(agenda.getTarget()).isEqualTo(GradeType.from(request.getTarget()))
        );
    }

    @Test
    @DisplayName("마감 시간이 시작 시간보다 빠른 경우 예외가 발생해야 함")
    void updateTest_invalidClosedAt() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaUpdateRequest request = getInvalidClosedAtAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }

    @Test
    @DisplayName("이미 종료된 안건인 경우 예외가 발생해야 함")
    void updateTest_alreadyClosed() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        agenda.close();
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표가 시작된 안건일 경우, '제목, 마감 시간'이 바뀔 수 있음")
    void updateTest_voteStarted() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        List<AgendaItem> agendaItemList = BASIC_AGENDA_SELECTED_LIST_DTO.stream().map(agendaItemDto -> new AgendaItem(agenda, agendaItemDto.getContent())).collect(Collectors.toList());
        agenda.addAgendaItems(agendaItemList);
        agenda.addVote(mock(Vote.class));
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when
        agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget());

        //then
        List<AgendaItem> changedAgendaItems = agenda.getAgendaItems();
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(BASIC_AGENDA_CONTENT),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt()),
                () -> assertThat(agenda.getTarget()).isEqualTo(GradeType.REGULAR),
                () -> assertThat(changedAgendaItems).hasSize(2)
        );
    }

    @Test
    @DisplayName("이미 투표가 시작된 안건인 경우에 내용, 대상을 수정하려고 하면 예외가 발생해야 함")
    void updateTest_invalidUpdateAlreadyStartedVoteAgenda() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, GradeType.REGULAR, BASIC_AGENDA_CLOSED_AT);
        List<AgendaItem> agendaItemList = BASIC_AGENDA_SELECTED_LIST_DTO.stream().map(agendaItemDto -> new AgendaItem(agenda, agendaItemDto.getContent())).collect(Collectors.toList());
        agenda.addAgendaItems(agendaItemList);
        agenda.addVote(mock(Vote.class));
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(InvalidUpdateAlreadyVoteStartedAgendaException.class);
    }
}