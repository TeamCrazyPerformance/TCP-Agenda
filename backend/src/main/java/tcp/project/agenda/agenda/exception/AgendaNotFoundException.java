package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class AgendaNotFoundException extends AgendaException {

    public static final String ERROR_MSG = "해당 안건이 존재하지 않습니다. id = %d";

    public AgendaNotFoundException(Long agendaId) {
        super(String.format(ERROR_MSG, agendaId), HttpStatus.BAD_REQUEST);
    }
}
