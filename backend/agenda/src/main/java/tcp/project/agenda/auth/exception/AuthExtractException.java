package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class AuthExtractException extends AgendaException {

    public static final String ERROR_MSG = "올바르지 않은 인증 헤더입니다.";

    public AuthExtractException() {
        super(ERROR_MSG, HttpStatus.UNAUTHORIZED);
    }
}
