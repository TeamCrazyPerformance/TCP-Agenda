package tcp.project.agenda.agenda.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.AlreadyVoteException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidUpdateAlreadyVoteStartedAgendaException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.agenda.exception.NotTargetMemberException;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.agenda.ui.dto.AgendaResponse;
import tcp.project.agenda.common.support.MockControllerTest;
import tcp.project.agenda.member.domain.GradeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tcp.project.agenda.common.fixture.AgendaFixture.BASIC_AGENDA_CONTENT;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaListResponse;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaResponse;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicNotVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteStartedAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaUpdateRequest;
import static tcp.project.agenda.common.fixture.AuthFixture.ACCESS_TOKEN;

class AgendaControllerTest extends MockControllerTest {

    @BeforeEach
    void init() {
        given(jwtTokenProvider.getMemberId(ACCESS_TOKEN))
                .willReturn(1L);
    }

    @Test
    @DisplayName("agenda가 만들어지면 200을 응답해야 함")
    void createAgendaTest() throws Exception {
        //given
        AgendaCreateRequest request = getBasicAgendaCreateRequest();

        //when then
        mockMvc.perform(post("/agenda")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 마감 시간이 포함된 요청인 경우 400을 응답해야 함")
    void createAgendaTest_invalidClosedAt() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidClosedAtAgendaCreateRequest();
        doThrow(new InvalidClosedAgendaTimeException())
                .when(agendaService)
                .createAgenda(any(), any());

        //when then
        mockMvc.perform(post("/agenda")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건 마감에 성공하면 200을 응답해야 함")
    void closeAgendaTest() throws Exception {
        //given
        doNothing().when(agendaService).closeAgenda(any(), any());

        //when then
        mockMvc.perform(post("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("없는 안건인 경우 400을 응답해야 함")
    void closeAgendaTest_agendaNotFound() throws Exception {
        //given
        doThrow(new AgendaNotFoundException(1L))
                .when(agendaService)
                .closeAgenda(any(), any());

        //when then
        mockMvc.perform(post("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건 작성자가 아닌인 경우 400을 응답해야 함")
    void closeAgendaTest_notAgendaOwner() throws Exception {
        //given
        doThrow(new NotAgendaOwnerException(1L, 1L))
                .when(agendaService)
                .closeAgenda(any(), any());

        //when then
        mockMvc.perform(post("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미 마감된 안건인 경우 400을 응답해야 함")
    void closeAgendaTest_alreadyClosed() throws Exception {
        //given
        doThrow(new AgendaAlreadyClosedException())
                .when(agendaService)
                .closeAgenda(any(), any());

        //when then
        mockMvc.perform(post("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표에 성공하면 200을 응답해야 함")
    void voteTest() throws Exception {
        //given
        doNothing().when(voteService).vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("없는 안건인 경우 400을 응답해야 함")
    void voteTest_agendaNotFound() throws Exception {
        //given
        doThrow(new AgendaNotFoundException(1L))
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건 작성자가 아닌인 경우 400을 응답해야 함")
    void voteTest_notAgendaOwner() throws Exception {
        //given
        doThrow(new NotAgendaOwnerException(1L, 1L))
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미 마감된 안건인 경우 400을 응답해야 함")
    void voteTest_alreadyClosedAgenda() throws Exception {
        //given
        doThrow(new AgendaAlreadyClosedException())
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미 투표한 안건인 경우 400을 응답해야 함")
    void voteTest_alreadyVote() throws Exception {
        //given
        doThrow(new AlreadyVoteException(1L))
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표 대상이 아닌 안건인 경우 400을 응답해야 함")
    void voteTest_notTarget() throws Exception {
        //given
        doThrow(new NotTargetMemberException())
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 투표 항목인 경우 400을 응답해야 함")
    void voteTest_agendaItemNotFound() throws Exception {
        //given
        doThrow(new AgendaItemNotFoundException(1L))
                .when(voteService)
                .vote(any(), any(), any());

        //when then
        mockMvc.perform(post("/agenda/1/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getBasicVoteRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표를 취소하면 200을 응답해야 함")
    void voteCancelTest() throws Exception {
        //given

        //when then
        mockMvc.perform(delete("/agenda/1/cancel")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("전체 안건을 조회하면 올바른 데이터와 200을 응답해야 함")
    void findAgendaListTest() throws Exception {
        //given
        AgendaListResponse response = getBasicAgendaListResponse();
        given(agendaService.getAgendaList(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/agenda")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("안건 하나를 조회하면 올바른 데이터와 200을 응답해야 함")
    void getAgendaTest() throws Exception {
        //given
        AgendaResponse response = getBasicAgendaResponse();
        given(agendaService.getAgenda(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("없는 안건을 조회할 경우 400을 응답해야 함")
    void getAgendaTest_agendaNotFound() throws Exception {
        //given
        given(agendaService.getAgenda(any()))
                .willThrow(new AgendaNotFoundException(1L));

        //when then
        mockMvc.perform(get("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건을 삭제할 경우 200을 응답해야 함")
    void deleteAgendaTest() throws Exception {
        //given

        //when then
        mockMvc.perform(delete("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("없는 안건을 삭제할 경우 400을 응답해야 함")
    void deleteAgendaTest_agendaNotFound() throws Exception {
        //given
        doThrow(new AgendaNotFoundException(1L))
                .when(agendaService)
                .deleteAgenda(any(), any());

        //when then
        mockMvc.perform(delete("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건 작성자가 아닌데 안건을 삭제할 경우 400을 응답해야 함")
    void deleteAgendaTest_notAgendaOwner() throws Exception {
        //given
        doThrow(new NotAgendaOwnerException(1L, 1L))
                .when(agendaService)
                .deleteAgenda(any(), any());

        //when then
        mockMvc.perform(delete("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아직 투표가 시작되지 않은 안건일 경우, '제목, 내용, 마감 시간, 대상, 투표 항목'이 바뀔 수 있음")
    void updateAgendaTest_voteNotStarted() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("안건이 없을 경우 경우 예외가 발생해야 함")
    void updateTest_agendaNotFound() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();
        doThrow(new AgendaNotFoundException(1L))
                .when(agendaService)
                .updateAgenda(any(), any(), any());

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("안건 작성자가 아닐 경우 경우 예외가 발생해야 함")
    void updateTest_notAgendaOwner() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();
        doThrow(new NotAgendaOwnerException(1L, 1L))
                .when(agendaService)
                .updateAgenda(any(), any(), any());

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("마감 시간이 시작 시간보다 빠른 경우 예외가 발생해야 함")
    void updateTest_invalidClosedAt() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();
        doThrow(new InvalidClosedAgendaTimeException())
                .when(agendaService)
                .updateAgenda(any(), any(), any());

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미 종료된 안건인 경우 예외가 발생해야 함")
    void updateTest_alreadyClosed() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicNotVoteStartedAgendaUpdateRequest();
        doThrow(new AgendaAlreadyClosedException())
                .when(agendaService)
                .updateAgenda(any(), any(), any());

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표가 시작된 안건일 경우, '제목, 마감 시간'이 바뀔 수 있음")
    void updateTest_voteStarted() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이미 투표가 시작된 안건인 경우에 내용, 대상, 투표 항목을 수정하려고 하면 예외가 발생해야 함")
    void updateTest_invalidUpdateAlreadyStartedVoteAgenda() throws Exception {
        //given
        AgendaUpdateRequest request = getBasicVoteStartedAgendaUpdateRequest();
        doThrow(new InvalidUpdateAlreadyVoteStartedAgendaException())
                .when(agendaService)
                .updateAgenda(any(), any(), any());

        //when then
        mockMvc.perform(put("/agenda/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}