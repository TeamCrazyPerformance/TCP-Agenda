package tcp.project.agenda.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends AgendaException {

    public static final String ERROR_MSG = "요청 검증에 실패했습니다. \n---실패 원인---\n%s";

    public ValidationException(List<ValidationError> errors) {
        super(String.format(ERROR_MSG, errors.toString()), HttpStatus.BAD_REQUEST);
    }
}
