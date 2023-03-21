package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class AlreadyVoteException extends AgendaException {

    public static final String ERROR_MSG = "이미 투표한 안건입니다. agendaId = %d";

    public AlreadyVoteException(Long agendaId) {
        super(String.format(ERROR_MSG, agendaId), HttpStatus.BAD_REQUEST);
    }
}
