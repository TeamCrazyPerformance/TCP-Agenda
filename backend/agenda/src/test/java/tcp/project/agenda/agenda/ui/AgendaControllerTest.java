package tcp.project.agenda.agenda.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.common.support.MockControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tcp.project.agenda.common.fixture.AgendaFixture.getBasicAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getInvalidClosedAtAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AgendaFixture.getNoTitleAgendaCreateRequest;
import static tcp.project.agenda.common.fixture.AuthFixture.ACCESS_TOKEN;

class AgendaControllerTest extends MockControllerTest {

    @Test
    @DisplayName("agenda가 만들어지면 200을 응답해야 함")
    void createAgendaTest() throws Exception {
        //given
        AgendaCreateRequest request = getBasicAgendaCreateRequest();
        given(jwtTokenProvider.getMemberId(ACCESS_TOKEN))
                .willReturn(1L);

        //when then
        mockMvc.perform(post("/agenda")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("title이 없는 요청인 경우 400을 응답해야 함")
    void createAgendaTest_invalidTitle() throws Exception {
        //given
        AgendaCreateRequest request = getNoTitleAgendaCreateRequest();
        given(jwtTokenProvider.getMemberId(ACCESS_TOKEN))
                .willReturn(1L);
        doThrow(new InvalidTitleException())
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
    @DisplayName("잘못된 마감 시간이 포함된 요청인 경우 400을 응답해야 함")
    void createAgendaTest_invalidClosedAt() throws Exception {
        //given
        AgendaCreateRequest request = getInvalidClosedAtAgendaCreateRequest();
        given(jwtTokenProvider.getMemberId(ACCESS_TOKEN))
                .willReturn(1L);
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
}