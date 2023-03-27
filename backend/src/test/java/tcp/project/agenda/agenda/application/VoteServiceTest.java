package tcp.project.agenda.agenda.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.AlreadyVoteException;
import tcp.project.agenda.agenda.exception.NotTargetMemberException;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.common.exception.ValidationException;
import tcp.project.agenda.common.support.ApplicationServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNotExistSelectItemVoteRequest;

class VoteServiceTest extends ApplicationServiceTest {

    @Autowired
    AgendaService agendaService;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("투표가 되어야 함")
    void voteTest() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        agendaService.vote(regular.getId(), 1L, getBasicVoteRequest());

        //then
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(2);
    }

    @Test
    @DisplayName("잘못된 body일 경우 예외가 발생해야 함")
    void voteTest_invalidRequest() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> agendaService.vote(regular.getId(), 1L, getInvalidVoteRequest()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("없는 안건일 경우 예외가 발생해야 함")
    void voteTest_agendaNotFound() throws Exception {
        //given
        Long notExistAgendaId = 999L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());


        //when then
        assertThatThrownBy(() -> agendaService.vote(regular.getId(), notExistAgendaId, getBasicVoteRequest()))
                .isInstanceOf(AgendaNotFoundException.class);
    }

    @Test
    @DisplayName("없는 유저일 경우 예외가 발생해야 함")
    void voteTest_memberNotFound() throws Exception {
        //given
        Long notExistMember = 999L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when then
        assertThatThrownBy(() -> agendaService.vote(notExistMember, 1L, getBasicVoteRequest()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("이미 마감된 안건에 투표할 경우 예외가 발생해야 함")
    void voteTest_alreadyClosedAgenda() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        agendaService.closeAgenda(regular.getId(), 1L);

        //when then
        assertThatThrownBy(() -> agendaService.vote(regular.getId(), 1L, getBasicVoteRequest()))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("없는 투표 항목을 투표할 경우 예외가 발생해야 함")
    void voteTest_agendaItemNotFound() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when then
        assertThatThrownBy(() -> agendaService.vote(regular.getId(), 1L, getNotExistSelectItemVoteRequest()))
                .isInstanceOf(AgendaItemNotFoundException.class);
    }

    @Test
    @DisplayName("대상이 아닌 안건에 투표할 경우 예외가 발생해야 함")
    void voteTest_NotTargetMember() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when then
        assertThatThrownBy(() -> agendaService.vote(general.getId(), 1L, getNotExistSelectItemVoteRequest()))
                .isInstanceOf(NotTargetMemberException.class);
    }

    @Test
    @DisplayName("이미 투표한 안건에 또 투표를 하면 예외가 발생해야 함")
    void voteTest_alreadyVote() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        agendaService.vote(regular.getId(), 1L, getBasicVoteRequest());

        //when then
        assertThatThrownBy(() -> agendaService.vote(regular.getId(), 1L, getBasicVoteRequest()))
                .isInstanceOf(AlreadyVoteException.class);
    }

}