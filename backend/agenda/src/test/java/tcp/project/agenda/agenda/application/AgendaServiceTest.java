package tcp.project.agenda.agenda.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.common.support.ApplicationServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_TITLE;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNoTitleAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNotExistSelectItemVoteRequest;

class AgendaServiceTest extends ApplicationServiceTest {

    @Autowired
    AgendaService agendaService;

    @Autowired
    AgendaRepository agendaRepository;

    @Autowired
    AgendaItemRepository agendaItemRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("올바른 요청이 올 경우 agenda가 만들어져야 함")
    void createAgendaTest() throws Exception {
        //given
        AgendaCreateRequest request = getBasicAgendaCreateRequest();

        //when
        agendaService.createAgenda(executive.getId(), request);

        //then
        List<Agenda> agendaList = agendaRepository.findAll();
        List<AgendaItem> agendaItems = agendaItemRepository.findAll();

        assertThat(agendaList).hasSize(1);
        assertThat(agendaItems).hasSize(2);
    }

    @Test
    @DisplayName("안건 제목이 비었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidTitle() throws Exception {
        //given
        AgendaCreateRequest request = getNoTitleAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.createAgenda(executive.getId(), request))
            .isInstanceOf(InvalidTitleException.class);
    }

    @Test
    @DisplayName("안건 마감 시간이 잘못되었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidClosedAt() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidClosedAtAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.createAgenda(executive.getId(), request))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }

    @Test
    @DisplayName("안건이 마감되어야 함")
    void closeTest() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        agendaService.closeAgenda(regular.getId(), 1L);

        //then
        Agenda findAgenda = agendaRepository.findAll().get(0);
        assertThat(findAgenda.isClosed()).isTrue();
    }

    @Test
    @DisplayName("없는 안건일 경우 예외가 발생해야 함")
    void closeTest_agendaNotFound() throws Exception {
        //given
        Long notExistAgendaId = 999L;

        //when then
        assertThatThrownBy(() -> agendaService.closeAgenda(regular.getId(), notExistAgendaId))
                .isInstanceOf(AgendaNotFoundException.class);
    }

    @Test
    @DisplayName("안건 작성자가 아닐 경우 예외가 발생해야 함")
    void closeTest_notAgendaOwner() throws Exception {
        //given
        Long notOwnerId = 999L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when then
        assertThatThrownBy(() -> agendaService.closeAgenda(notOwnerId, 1L))
                .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("이미 마감된 안건일 경우 예외가 발생해야 함")
    void closeTest_alreadyClosed() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        agendaService.closeAgenda(regular.getId(), 1L);

        //when then
        assertThatThrownBy(() -> agendaService.closeAgenda(regular.getId(), 1L))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표가 되어야 함")
    void voteTest() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        agendaService.vote(general.getId(), 1L, getBasicVoteRequest());

        //then
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(2);
    }

    @Test
    @DisplayName("없는 안건일 경우 예외가 발생해야 함")
    void voteTest_agendaNotFound() throws Exception {
        //given
        Long notExistAgendaId = 999L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());


        //when then
        assertThatThrownBy(() -> agendaService.vote(general.getId(), notExistAgendaId, getBasicVoteRequest()))
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
        assertThatThrownBy(() -> agendaService.vote(general.getId(), 1L, getBasicVoteRequest()))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("없는 투표 항목을 투표할 경우 예외가 발생해야 함")
    void voteTest_agendaItemNotFound() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when then
        assertThatThrownBy(() -> agendaService.vote(general.getId(), 1L, getNotExistSelectItemVoteRequest()))
                .isInstanceOf(AgendaItemNotFoundException.class);
    }
}