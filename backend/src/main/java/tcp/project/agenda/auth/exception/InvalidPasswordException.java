package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class InvalidPasswordException extends AgendaException {

    public static final String ERROR_MSG = "비밀번호가 올바르지 않습니다. id: %s, password: %s";

    public InvalidPasswordException(String id, String password) {
        super(String.format(ERROR_MSG, id, password), HttpStatus.BAD_REQUEST);
    }
}
