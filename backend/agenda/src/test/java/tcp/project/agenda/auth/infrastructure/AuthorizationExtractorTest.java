package tcp.project.agenda.auth.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tcp.project.agenda.auth.exception.AuthExtractException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationExtractorTest {

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"Bearrer jwt-token", "Bearer"})
    @DisplayName("추출 실패시 예외가 발생해야 함")
    void extractNothingEmpty(String token) {
        assertThatThrownBy(() -> AuthorizationExtractor.extract(token))
                .isInstanceOf(AuthExtractException.class);
    }

    @Test
    @DisplayName("올바른 토큰이 넘어왔을 경우 토큰 타입을 제외한 토큰을 리턴해야 함")
    void extract() {
        assertThat(AuthorizationExtractor.extract("Bearer jwt-token")).isEqualTo("jwt-token");
    }
}