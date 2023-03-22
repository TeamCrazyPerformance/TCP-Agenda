package tcp.project.agenda.auth.ui;

import org.springframework.util.StringUtils;
import tcp.project.agenda.auth.exception.AuthExtractException;

public class AuthorizationExtractor {

    private static final String TOKEN_TYPE = "Bearer";
    private static final int TOKEN_INDEX = 1;
    private static final int TOKEN_TYPE_INDEX = 0;

    public static String extract(String authHeader) {
        validateAuthHeader(authHeader);
        String[] authContents = authHeader.split(" ");
        validateAuthContents(authContents);

        return authContents[TOKEN_INDEX];
    }

    private static void validateAuthContents(String[] authContents) {
        if (!(authContents.length == 2 && TOKEN_TYPE.equals(authContents[TOKEN_TYPE_INDEX]))) {
            throw new AuthExtractException();
        }
    }

    private static void validateAuthHeader(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            throw new AuthExtractException();
        }
    }
}
