package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class MemberNotFoundException extends AgendaException {

    public static final String ERROR_MSG = "해당 유저가 존재하지 않습니다. id: %s";

    public MemberNotFoundException(String id) {
        super(String.format(ERROR_MSG, id), HttpStatus.BAD_REQUEST);
    }
}
