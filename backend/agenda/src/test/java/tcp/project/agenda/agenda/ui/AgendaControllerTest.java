package tcp.project.agenda.agenda.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.common.support.MockControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicVoteRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
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
        doNothing().when(agendaService).vote(any(), any(), any());

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
                .when(agendaService)
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
                .when(agendaService)
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
                .when(agendaService)
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
                .when(agendaService)
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
}