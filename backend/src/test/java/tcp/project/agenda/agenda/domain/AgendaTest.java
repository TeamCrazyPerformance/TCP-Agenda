package tcp.project.agenda.agenda.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidUpdateAlreadyVoteStartedAgendaException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.agenda.exception.NotTargetMemberException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM1;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM2;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_TITLE;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_UPDATE_AGENDA_ITEM;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteNotStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaUpdateRequest;
import static tcp.project.agenda.member.domain.GradeType.GENERAL;
import static tcp.project.agenda.member.domain.GradeType.REGULAR;

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
    @DisplayName("투표 항목이 수정 되어야 함")
    void updateAgendaItemsTest() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);

        //when
        agenda.updateAgendaItems(List.of(AgendaItem.createAgendaItem(BASIC_UPDATE_AGENDA_ITEM)));

        //then
        assertThat(agenda.getAgendaItems()).hasSize(1);
        assertThat(agenda.getAgendaItems().get(0).getContent()).isEqualTo(BASIC_UPDATE_AGENDA_ITEM);
    }

    @Test
    @DisplayName("마감된 안건인 경우 투표 항목을 수정하면 예외가 발생해야 함")
    void updateAgendaItemsTest_alreadyClosed() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, grade.getGradeType(), BASIC_AGENDA_CLOSED_AT);
        agenda.updateAgendaItems(List.of(AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM1), AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM2)));
        agenda.close();

        //when then
        assertThatThrownBy(() -> agenda.updateAgendaItems(List.of(AgendaItem.createAgendaItem(BASIC_UPDATE_AGENDA_ITEM))))
                .isInstanceOf(AgendaAlreadyClosedException.class);
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
    @DisplayName("안건에 투표를 함")
    void voteTest() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaItem agendaItem1 = mock(AgendaItem.class);
        AgendaItem agendaItem2 = mock(AgendaItem.class);
        AgendaItem agendaItem3 = mock(AgendaItem.class);
        agenda.updateAgendaItems(List.of(agendaItem1, agendaItem2, agendaItem3));
        given(agendaItem1.getId()).willReturn(1L);
        given(agendaItem2.getId()).willReturn(2L);
        given(agendaItem3.getId()).willReturn(3L);
        given(member.getGrades()).willReturn(List.of(REGULAR));

        //when
        List<Vote> vote = agenda.vote(member, List.of(1L, 3L));

        //then
        assertThat(vote).hasSize(2);
    }

    @Test
    @DisplayName("마감된 안건에 투표를 하면 예외가 발생해야 함")
    void voteTest_alreadyClosed() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        agenda.close();

        //when then
        assertThatThrownBy(() -> agenda.vote(member, List.of(1L, 3L)))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("안건에 투표한 항목이 없을 경우 예외가 발생해야 함")
    void voteTest_agendaItemNotFound() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaItem agendaItem1 = mock(AgendaItem.class);
        AgendaItem agendaItem2 = mock(AgendaItem.class);
        AgendaItem agendaItem3 = mock(AgendaItem.class);
        agenda.updateAgendaItems(List.of(agendaItem1, agendaItem2, agendaItem3));
        given(agendaItem1.getId()).willReturn(1L);
        given(agendaItem2.getId()).willReturn(2L);
        given(agendaItem3.getId()).willReturn(3L);
        given(member.getGrades()).willReturn(List.of(REGULAR));


        //when then
        assertThatThrownBy(() -> agenda.vote(member, List.of(4L)))
                .isInstanceOf(AgendaItemNotFoundException.class);
    }

    @Test
    @DisplayName("대상이 아닌 안건에 투표를 하면 예외가 발생해야 함")
    void voteTest_notTargetMember() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        given(member.getGrades()).willReturn(List.of(GENERAL));

        //when then
        assertThatThrownBy(() -> agenda.vote(member, List.of(1L, 3L)))
                .isInstanceOf(NotTargetMemberException.class);
    }

    @Test
    @DisplayName("아직 투표가 시작되지 않은 안건일 경우, '제목, 내용, 마감 시간, 대상'이 바뀔 수 있음")
    void updateTest_voteNotStarted() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

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
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        AgendaUpdateRequest request = getInvalidClosedAtAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }

    @Test
    @DisplayName("이미 종료된 안건인 경우 예외가 발생해야 함")
    void updateTest_alreadyClosed() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        agenda.close();
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표가 시작된 안건일 경우, '마감 시간'이 바뀔 수 있음")
    void updateTest_voteStarted() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        agenda.updateAgendaItems(List.of(AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM1), AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM2)));
        agenda.addVote(mock(Vote.class));
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when
        agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget());

        //then
        List<AgendaItem> changedAgendaItems = agenda.getAgendaItems();
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(BASIC_AGENDA_TITLE),
                () -> assertThat(agenda.getContent()).isEqualTo(BASIC_AGENDA_CONTENT),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt()),
                () -> assertThat(agenda.getTarget()).isEqualTo(REGULAR),
                () -> assertThat(changedAgendaItems).hasSize(2)
        );
    }

    @Test
    @DisplayName("이미 투표가 시작된 안건인 경우에 내용, 대상을 수정하려고 하면 예외가 발생해야 함")
    void updateTest_invalidUpdateAlreadyStartedVoteAgenda() throws Exception {
        //given
        Agenda agenda = Agenda.createAgendaFrom(member, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, REGULAR, BASIC_AGENDA_CLOSED_AT);
        agenda.updateAgendaItems(List.of(AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM1), AgendaItem.createAgendaItem(BASIC_AGENDA_ITEM2)));
        agenda.addVote(mock(Vote.class));
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget()))
                .isInstanceOf(InvalidUpdateAlreadyVoteStartedAgendaException.class);
    }
}