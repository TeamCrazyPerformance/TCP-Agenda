package tcp.project.agenda.agenda.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.common.support.ApplicationServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CLOSED_AT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM1;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM2;
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
}