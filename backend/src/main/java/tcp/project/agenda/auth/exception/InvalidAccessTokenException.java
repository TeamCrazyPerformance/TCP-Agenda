package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class InvalidAccessTokenException extends AgendaException {

    public static final String ERROR_MSG = "올바르지 않은 토큰입니다.";

    public InvalidAccessTokenException() {
        super(ERROR_MSG, HttpStatus.UNAUTHORIZED);
    }
}
