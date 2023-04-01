package tcp.project.agenda.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tcp.project.agenda.auth.exception.ExpiredTokenException;

@Slf4j
@RestControllerAdvice
public class AgendaControllerAdvice {

    public static final String EXPIRED_TOKEN_ERROR_MSG = "EXPIRED TOKEN";

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ErrorResponse> expiredTokenException(ExpiredTokenException e) {
        log.error("e.getErrorMsg() = {}", e.getErrorMsg());
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErrorResponse(EXPIRED_TOKEN_ERROR_MSG));
    }

    @ExceptionHandler(AgendaException.class)
    public ResponseEntity<Void> agendaException(AgendaException e) {
        log.error("e.getErrorMsg() = {}", e.getErrorMsg());
        return ResponseEntity
                .status(e.getStatus())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unExpectedException(Exception e) {
        log.error("e = ", e.getCause());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
