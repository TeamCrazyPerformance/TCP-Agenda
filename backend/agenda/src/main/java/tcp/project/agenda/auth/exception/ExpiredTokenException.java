package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class ExpiredTokenException extends AgendaException {

    public static final String ERROR_MSG = "만료된 토큰입니다.";

    public ExpiredTokenException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
