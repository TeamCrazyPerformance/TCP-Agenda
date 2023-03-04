package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class InvalidTitleException extends AgendaException {

    public static final String ERROR_MSG = "안건 제목은 null일 수 없습니다.";

    public InvalidTitleException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
