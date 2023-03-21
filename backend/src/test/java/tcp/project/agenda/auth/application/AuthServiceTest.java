package tcp.project.agenda.auth.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import tcp.project.agenda.auth.application.dto.LoginRequest;
import tcp.project.agenda.auth.ui.dto.TokenResponse;
import tcp.project.agenda.common.support.ApplicationServiceTest;
import tcp.project.agenda.auth.exception.InvalidPasswordException;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.infrastructure.JwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static tcp.project.agenda.common.fixture.AuthFixture.getInvalidPasswordLoginRequest;
import static tcp.project.agenda.common.fixture.AuthFixture.getNotExistMemberLoginRequest;
import static tcp.project.agenda.common.fixture.AuthFixture.getRegularMemberLoginRequest;

class AuthServiceTest extends ApplicationServiceTest {

    @Autowired
    AuthService authService;

    @MockBean
    JwtTokenProvider provider;

    @Test
    @DisplayName("로그인에 성공하면 TokenResponse를 응답함")
    void login() throws Exception {
        //given
        String token = "token";
        LoginRequest request = getRegularMemberLoginRequest();
        given(provider.createToken(any()))
                .willReturn(token);

        //when
        TokenResponse result = authService.login(request);

        //then
        assertThat(result.getAccessToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("없는 아이디로 로그인 할 경우 예외가 발생해야 함")
    void login_memberNotFound() throws Exception {
        //given
        LoginRequest request = getNotExistMemberLoginRequest();

        //when then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호를 입력했을 경우 예외가 발생해야 함")
    void login_invalidPassword() throws Exception {
        //given
        LoginRequest request = getInvalidPasswordLoginRequest();

        //when then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(InvalidPasswordException.class);
    }
}