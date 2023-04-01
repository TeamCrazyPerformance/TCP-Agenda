package tcp.project.agenda.agenda.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaItemUpdateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidUpdateAlreadyVoteStartedAgendaException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.agenda.ui.dto.AgendaDto;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.agenda.ui.dto.AgendaResponse;
import tcp.project.agenda.agenda.ui.dto.SelectItemDto;
import tcp.project.agenda.common.exception.ValidationException;
import tcp.project.agenda.common.support.ApplicationServiceTest;
import tcp.project.agenda.member.domain.GradeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM1;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_ITEM2;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_UPDATE_AGENDA_ITEM;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaItemUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteNotStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaUpdateRequest;

class AgendaServiceTest extends ApplicationServiceTest {

    @Autowired
    AgendaService agendaService;

    @Autowired
    VoteService voteService;

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
    @DisplayName("요청 body가 잘못 되었을 경우 예외가 발생해야 함")
    void createAgendaTest_invalidRequest() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidAgendaCreateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.createAgenda(executive.getId(), request))
                .isInstanceOf(ValidationException.class);
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
    @DisplayName("안건 전체 조회 - 투표한 사람 없는 경우")
    void getAgendaListTest_noVotedMember() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        AgendaListResponse response = agendaService.getAgendaList(pageable);

        //then
        List<AgendaDto> agendaList = response.getAgendaList();
        assertAll(
                () -> assertThat(response.getPageNumber()).isEqualTo(0),
                () -> assertThat(response.isHasNext()).isFalse(),
                () -> assertThat(agendaList).hasSize(1),
                () -> assertThat(agendaList.get(0).getVotedMember()).isEqualTo(0)

        );
    }

    @Test
    @DisplayName("안건 전체 조회 - 투표한 사람 있는 경우")
    void getAgendaListTest_hasVotedMember() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        voteService.vote(regular.getId(), 1L, getBasicVoteRequest());
        voteService.vote(executiveRegular.getId(), 1L, getBasicVoteRequest());

        //when
        AgendaListResponse response = agendaService.getAgendaList(pageable);

        //then
        List<AgendaDto> agendaList = response.getAgendaList();
        assertAll(
                () -> assertThat(response.getPageNumber()).isEqualTo(0),
                () -> assertThat(response.isHasNext()).isFalse(),
                () -> assertThat(agendaList).hasSize(1),
                () -> assertThat(agendaList.get(0).getVotedMember()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("안건 전체 조회 - 투표가 많은 경우")
    void getAgendaListTest_lotOfVotes() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        for (int i = 0; i < 15; i++) {
            agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        }

        //when
        AgendaListResponse response = agendaService.getAgendaList(pageable);

        //then
        List<AgendaDto> agendaList = response.getAgendaList();
        assertAll(
                () -> assertThat(response.getPageNumber()).isEqualTo(0),
                () -> assertThat(response.isHasNext()).isTrue(),
                () -> assertThat(agendaList).hasSize(10),
                () -> assertThat(agendaList.get(0).getVotedMember()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("안건 하나의 데이터를 가져와야 함")
    void getAgendaTest() throws Exception {
        //given
        Long agendaId = 1L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        voteService.vote(regular.getId(), agendaId, getBasicVoteRequest());
        voteService.vote(executiveRegular.getId(), agendaId, getBasicVoteRequest());

        //when
        AgendaResponse response = agendaService.getAgenda(regular.getId(), agendaId);

        //then
        Agenda agenda = agendaRepository.findAll().get(0);
        List<SelectItemDto> selectList = response.getSelectList();
        assertAll(
                () -> assertThat(response.getTitle()).isEqualTo(agenda.getTitle()),
                () -> assertThat(response.getContent()).isEqualTo(agenda.getContent()),
                () -> assertThat(response.getTarget()).isEqualTo(agenda.getTarget().getCode()),
                () -> assertThat(response.getCreatedAt()).isEqualTo(agenda.getCreatedDate()),
                () -> assertThat(response.getClosedAt()).isEqualTo(agenda.getClosedAt()),
                () -> assertThat(response.getVotedMember()).isEqualTo(2),
                () -> assertThat(response.getVotedMember()).isEqualTo(2),
                () -> assertThat(response.isOpen()).isTrue(),
                () -> assertThat(response.isVoted()).isTrue(),
                () -> assertThat(selectList.get(0).getId()).isEqualTo(1),
                () -> assertThat(selectList.get(0).getContent()).isEqualTo(BASIC_AGENDA_ITEM1),
                () -> assertThat(selectList.get(0).getVoteCount()).isEqualTo(2),
                () -> assertThat(selectList.get(1).getId()).isEqualTo(2),
                () -> assertThat(selectList.get(1).getContent()).isEqualTo(BASIC_AGENDA_ITEM2),
                () -> assertThat(selectList.get(1).getVoteCount()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("안건이 없는 경우 예외가 발생해야 함")
    void getAgendaTest_agendaNotFound() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> agendaService.getAgenda(regular.getId(), 999L))
                .isInstanceOf(AgendaNotFoundException.class);
    }

    @Test
    @DisplayName("안건과 투표 항목, 투표들이 삭제되어야 함")
    void deleteAgendaTest() throws Exception {
        //given
        Long agendaId = 1L;
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        voteService.vote(regular.getId(), agendaId, getBasicVoteRequest());
        voteService.vote(executiveRegular.getId(), agendaId, getBasicVoteRequest());

        //when
        agendaService.deleteAgenda(regular.getId(), agendaId);

        //then
        List<Agenda> agendaList = agendaRepository.findAll();
        List<AgendaItem> agendaItems = agendaItemRepository.findAll();
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(0);
        assertThat(agendaItems).hasSize(0);
        assertThat(agendaList).hasSize(0);
    }

    @Test
    @DisplayName("없는 안건인 경우 예외가 발생해야 함")
    void deleteAgendaTest_agendaNotFound() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        assertThatThrownBy(() -> agendaService.deleteAgenda(regular.getId(), 999L))
                .isInstanceOf(AgendaNotFoundException.class);
    }

    @Test
    @DisplayName("안건 작성자가 아닌 경우 예외가 발생해야 함")
    void deleteAgendaTest_notAgendaOwner() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());

        //when
        assertThatThrownBy(() -> agendaService.deleteAgenda(general.getId(), 1L))
                .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("아직 투표가 시작되지 않은 안건일 경우, '제목, 내용, 마감 시간, 대상, 투표 항목'이 바뀔 수 있음")
    void updateAgendaTest_voteNotStarted() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

        //when
        agendaService.updateAgenda(regular.getId(), 1L, request);

        //then
        Agenda agenda = agendaRepository.findAll().get(0);
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(request.getContent()),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt()),
                () -> assertThat(agenda.getTarget()).isEqualTo(GradeType.from(request.getTarget()))
        );
    }

    @Test
    @DisplayName("안건이 없을 경우 경우 예외가 발생해야 함")
    void updateTest_agendaNotFound() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgenda(regular.getId(), 999L, request))
                .isInstanceOf(AgendaNotFoundException.class);
    }

    @Test
    @DisplayName("안건 작성자가 아닐 경우 경우 예외가 발생해야 함")
    void updateTest_notAgendaOwner() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgenda(executive.getId(), 1L, request))
                .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("마감 시간이 시작 시간보다 빠른 경우 예외가 발생해야 함")
    void updateTest_invalidClosedAt() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaUpdateRequest request = getInvalidClosedAtAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgenda(regular.getId(), 1L, request))
                .isInstanceOf(InvalidClosedAgendaTimeException.class);
    }

    @Test
    @DisplayName("이미 종료된 안건인 경우 예외가 발생해야 함")
    void updateTest_alreadyClosed() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        agendaService.closeAgenda(regular.getId(), 1L);
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgenda(regular.getId(), 1L, request))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표가 시작된 안건일 경우, '마감 시간'이 바뀔 수 있음")
    void updateTest_voteStarted() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        voteService.vote(regular.getId(), 1L, getBasicVoteRequest());
        List<AgendaItem> expectedAgendaItems = agendaItemRepository.findAll();
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when
        agendaService.updateAgenda(regular.getId(), 1L, request);

        //then
        Agenda agenda = agendaRepository.findAll().get(0);
        List<AgendaItem> agendaItems = agendaItemRepository.findAll();
        assertAll(
                () -> assertThat(agenda.getTitle()).isEqualTo(request.getTitle()),
                () -> assertThat(agenda.getContent()).isEqualTo(BASIC_AGENDA_CONTENT),
                () -> assertThat(agenda.getClosedAt()).isEqualTo(request.getClosedAt()),
                () -> assertThat(agenda.getTarget()).isEqualTo(GradeType.REGULAR),
                () -> assertThat(agendaItems).hasSameElementsAs(expectedAgendaItems)
        );
    }

    @Test
    @DisplayName("이미 투표가 시작된 안건인 경우에 내용, 대상, 투표 항목을 수정하려고 하면 예외가 발생해야 함")
    void updateTest_invalidUpdateAlreadyStartedVoteAgenda() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        voteService.vote(regular.getId(), 1L, getBasicVoteRequest());
        AgendaUpdateRequest request = getBasicVoteNotStartedAgendaUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgenda(regular.getId(), 1L, request))
                .isInstanceOf(InvalidUpdateAlreadyVoteStartedAgendaException.class);
    }

    @Test
    @DisplayName("투표 항목이 수정 되어야 함")
    void updateAgendaItemsTest() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaItemUpdateRequest request = getBasicAgendaItemUpdateRequest();

        //when
        agendaService.updateAgendaItems(regular.getId(), 1L, request);

        //then
        List<AgendaItem> agendaItems = agendaItemRepository.findAll();
        assertAll(
                () -> assertThat(agendaItems).hasSize(1),
                () -> assertThat(agendaItems.get(0).getContent()).isEqualTo(BASIC_UPDATE_AGENDA_ITEM)
        );
    }

    @Test
    @DisplayName("안건 작석자가 아닌 경우 투표 항목을 수정하면 예외가 발생해야 함")
    void updateAgendaItemsTest_notAgendaOwner() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaItemUpdateRequest request = getBasicAgendaItemUpdateRequest();

        //when then
        assertThatThrownBy(() -> agendaService.updateAgendaItems(general.getId(), 1L, request))
                .isInstanceOf(NotAgendaOwnerException.class);
    }

    @Test
    @DisplayName("마감된 안건인 경우 투표 항목을 수정하면 예외가 발생해야 함")
    void updateAgendaItemsTest_alreadyClosed() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaItemUpdateRequest request = getBasicAgendaItemUpdateRequest();
        agendaService.closeAgenda(regular.getId(), 1L);

        //when then
        assertThatThrownBy(() -> agendaService.updateAgendaItems(regular.getId(), 1L, request))
                .isInstanceOf(AgendaAlreadyClosedException.class);
    }

    @Test
    @DisplayName("투표가 시작된 안건인 경우 투표 항목을 수정하면 예외가 발생해야 함")
    void updateAgendaItemsTest_alreadyVoteStarted() throws Exception {
        //given
        agendaService.createAgenda(regular.getId(), getBasicAgendaCreateRequest());
        AgendaItemUpdateRequest request = getBasicAgendaItemUpdateRequest();
        voteService.vote(regular.getId(), 1L, getBasicVoteRequest());

        //when then
        assertThatThrownBy(() -> agendaService.updateAgendaItems(regular.getId(), 1L, request))
                .isInstanceOf(InvalidUpdateAlreadyVoteStartedAgendaException.class);
    }
}