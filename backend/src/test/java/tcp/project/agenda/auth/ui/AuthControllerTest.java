package tcp.project.agenda.auth.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tcp.project.agenda.auth.application.dto.LoginRequest;
import tcp.project.agenda.auth.ui.dto.TokenResponse;
import tcp.project.agenda.auth.exception.InvalidPasswordException;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.common.support.MockControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tcp.project.agenda.common.fixture.AuthFixture.getRegularMemberLoginRequest;
import static tcp.project.agenda.common.fixture.AuthFixture.getTokenResponse;

class AuthControllerTest extends MockControllerTest {

    @Test
    @DisplayName("로그인에 성공하면 TokenResponse와 200을 응답해야 함")
    void login() throws Exception {
        //given
        LoginRequest request = getRegularMemberLoginRequest();
        TokenResponse response = getTokenResponse();
        given(authService.login(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("없는 아이디일 경우 400을 응답해야 함")
    void login_memberNotFound() throws Exception {
        //given
        LoginRequest request = getRegularMemberLoginRequest();
        given(authService.login(any()))
                .willThrow(new MemberNotFoundException(any()));

        //when then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 틀릴 경우 400을 응답해야 함")
    void login_invalidPassword() throws Exception {
        //given
        LoginRequest request = getRegularMemberLoginRequest();
        given(authService.login(any()))
                .willThrow(new InvalidPasswordException(any(), "wrong password"));

        //when then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}