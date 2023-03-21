package tcp.project.agenda.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class AgendaException extends RuntimeException {

    public static final String ERROR_MSG = "예상치 못한 예외입니다.";

    private final String errorMsg;
    private final HttpStatus status;

    public AgendaException() {
        this.errorMsg = ERROR_MSG;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
