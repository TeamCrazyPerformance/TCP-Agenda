package tcp.project.agenda.auth.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tcp.project.agenda.auth.exception.ExpiredTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tcp.project.agenda.common.fixture.AuthFixture.SECRET_KEY;

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
}