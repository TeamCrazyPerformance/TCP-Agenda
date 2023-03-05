package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class InvalidClosedAgendaTimeException extends AgendaException {

    public static final String ERROR_MSG = "마감 시간이 투표가 만들어지는 시간보다 빠를 수 없습니다.";

    public InvalidClosedAgendaTimeException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
