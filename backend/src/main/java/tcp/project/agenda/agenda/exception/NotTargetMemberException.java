package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class NotTargetMemberException extends AgendaException {

    public static final String ERROR_MSG = "해당 안건 투표 대상자가 아닙니다.";

    public NotTargetMemberException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
