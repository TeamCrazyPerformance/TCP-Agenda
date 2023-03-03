package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class NotAuthorizedMemberException extends AgendaException {

    public static final String ERROR_MSG = "접근 권한이 없습니다.";

    public NotAuthorizedMemberException() {
        super(ERROR_MSG, HttpStatus.UNAUTHORIZED);
    }
}
