package tcp.project.agenda.auth.exception;

import org.springframework.http.HttpStatus;
import tcp.project.agenda.common.exception.AgendaException;

public class NoSuchGradeException extends AgendaException {

    public static final String ERROR_MSG = "해당 등급이 존재하지 않습니다. 입력 값: %s";

    public NoSuchGradeException(String grade) {
        super(String.format(ERROR_MSG, grade), HttpStatus.BAD_REQUEST);
    }
}
