package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class AgendaItemNotFoundException extends AgendaException {

    public static final String ERROR_MSG = "해당 투표 항목이 존재하지 않습니다.";

    public AgendaItemNotFoundException() {
        super(String.format(ERROR_MSG), HttpStatus.BAD_REQUEST);
    }
}
