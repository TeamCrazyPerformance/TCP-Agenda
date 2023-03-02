package tcp.project.agenda.common.fixture;

import tcp.project.agenda.auth.application.LoginRequest;

import static tcp.project.agenda.common.fixture.MemberFixture.REGULAR_PASSWORD;
import static tcp.project.agenda.common.fixture.MemberFixture.REGULAR_UNIV_ID;

public class AuthFixture {

    public static final String SECRET_KEY = "secret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-keysecret-key";

    public static LoginRequest getRegularMemberLoginRequest() {
        return new LoginRequest(REGULAR_UNIV_ID, REGULAR_PASSWORD);
    }

    public static LoginRequest getNotExistMemberLoginRequest() {
        return new LoginRequest("1234", "1234");
    }

    public static LoginRequest getInvalidPasswordLoginRequest() {
        return new LoginRequest(REGULAR_UNIV_ID, "invalid password");
    }
}
