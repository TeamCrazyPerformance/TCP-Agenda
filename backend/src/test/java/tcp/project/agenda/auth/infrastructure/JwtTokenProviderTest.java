package tcp.project.agenda.auth.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tcp.project.agenda.auth.exception.ExpiredTokenException;
import tcp.project.agenda.auth.exception.InvalidAccessTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tcp.project.agenda.common.fixture.AuthFixture.SECRET_KEY;
import static tcp.project.agenda.common.fixture.AuthFixture.INVALID_TOKEN;

class JwtTokenProviderTest {

    JwtTokenProvider provider;

    @Test
    @DisplayName("올바른 토큰을 만들어야 함")
    void createToken() throws Exception {
        //given
        Long memberId = 1L;
        provider = new JwtTokenProvider(SECRET_KEY, 1000000);

        //when
        String token = provider.createToken(String.valueOf(memberId));
        Long result = provider.getMemberId(token);

        //then
        assertThat(result).isEqualTo(memberId);
    }

    @Test
    @DisplayName("토큰이 만료되었을 경우 예외가 발생해야 함")
    void expiredToken() throws Exception {
        //given
        provider = new JwtTokenProvider(SECRET_KEY, 0);
        String token = provider.createToken(String.valueOf(1L));

        //when then
        assertThatThrownBy(() -> provider.getMemberId(token))
            .isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    @DisplayName("잘못된 형식의 accessToken이 들어오면 예외가 발생해야 함")
    void invalidToken() throws Exception {
        //given
        provider = new JwtTokenProvider(SECRET_KEY, 1000000);

        //when then
        assertThatThrownBy(() -> provider.getMemberId(INVALID_TOKEN))
            .isInstanceOf(InvalidAccessTokenException.class);
    }
}