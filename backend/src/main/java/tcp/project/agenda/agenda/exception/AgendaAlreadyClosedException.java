package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class AgendaAlreadyClosedException extends AgendaException {

    public static final String ERROR_MSG = "이미 마감된 안건입니다.";

    public AgendaAlreadyClosedException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
