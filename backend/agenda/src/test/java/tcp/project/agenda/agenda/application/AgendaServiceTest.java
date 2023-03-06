package tcp.project.agenda.agenda.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.common.support.ApplicationServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_TITLE;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNoTitleAgendaCreateRequest;

class AgendaServiceTest extends ApplicationServiceTest {

    @Autowired
    AgendaService agendaService;

    @Autowired
    AgendaRepository agendaRepository;

    @Autowired
    AgendaItemRepository agendaItemRepository;

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
    @DisplayName("안건 제목이 비었을 경우 예외가 발생해야 함")
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
        Agenda agenda = getNewAgenda();

        //when
        agendaService.closeAgenda(regular.getId(), agenda.getId());

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
    @DisplayName("없는 안건일 경우 예외가 발생해야 함")
    void closeTest_notAgendaOwner() throws Exception {
        //given
        Long notOwnerId = 999L;
        Agenda agenda = getNewAgenda();

        //when then
        assertThatThrownBy(() -> agendaService.closeAgenda(notOwnerId, agenda.getId()))
                .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("없는 안건일 경우 예외가 발생해야 함")
    void closeTest_alreadyClosed() throws Exception {
        //given
        Agenda agenda = getNewAgenda();
        agendaService.closeAgenda(regular.getId(), agenda.getId());

        //when then
        assertThatThrownBy(() -> agendaService.closeAgenda(regular.getId(), agenda.getId()))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }


    private Agenda getNewAgenda() {
        return agendaRepository.save(Agenda.createAgendaFrom(regular, BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, regularGrade, BASIC_AGENDA_CLOSED_AT));
    }
}