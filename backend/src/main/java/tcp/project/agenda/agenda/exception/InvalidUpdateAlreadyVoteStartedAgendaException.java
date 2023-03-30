package tcp.project.agenda.agenda.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class InvalidUpdateAlreadyVoteStartedAgendaException extends AgendaException {

    public static final String ERROR_MSG = "투표가 시작된 안건은 내용, 대상 수정이 불가능 합니다.";

    public InvalidUpdateAlreadyVoteStartedAgendaException() {
        super(ERROR_MSG, HttpStatus.BAD_REQUEST);
    }
}
