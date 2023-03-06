package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class NotMemberAgendaException extends AgendaException {

    public static final String ERROR_MSG = "해당 안건의 작성자가 아닙니다. agendaId = %d, memberId = %d";

    public NotMemberAgendaException(Long id, Long memberId) {
        super(String.format(ERROR_MSG, id, memberId), HttpStatus.BAD_REQUEST);
    }
}
