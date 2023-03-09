package tcp.project.agenda.common.fixture;

import tcp.project.agenda.auth.application.dto.LoginRequest;
import tcp.project.agenda.auth.ui.dto.TokenResponse;

import static tcp.project.agenda.common.fixture.MemberFixture.REGULAR_PASSWORD;
import static tcp.project.agenda.common.fixture.MemberFixture.REGULAR_UNIV_ID;

public class AuthFixture {

    public static final String SECRET_KEY = "secret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-key";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String INVALID_TOKEN = "invalidToken";
    public static LoginRequest getRegularMemberLoginRequest() {
        return new LoginRequest(REGULAR_UNIV_ID, REGULAR_PASSWORD);
    }

    public static LoginRequest getNotExistMemberLoginRequest() {
        return new LoginRequest("1234", "1234");
    }

    public static LoginRequest getInvalidPasswordLoginRequest() {
        return new LoginRequest(REGULAR_UNIV_ID, "invalid password");
    }

    public static TokenResponse getTokenResponse() {
        return new TokenResponse(ACCESS_TOKEN);
    }
}
