package tcp.project.agenda.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AgendaControllerAdvice {

    @ExceptionHandler(AgendaException.class)
    public ResponseEntity<Void> agendaException(AgendaException e) {
        log.error("e.getErrorMsg() = {}", e.getErrorMsg());
        return ResponseEntity
                .status(e.getStatus())
                .build();
    }
}
