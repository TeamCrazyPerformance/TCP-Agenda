package tcp.project.agenda.agenda.application;

import org.springframework.util.StringUtils;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.common.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class AgendaCreateValidator {
    public static final String REQUIRED = "required";

    public List<ValidationError> validate(AgendaCreateRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        if (!StringUtils.hasText(request.getTitle())) {
            errors.add(new ValidationError("title", REQUIRED));
        }
        if (!StringUtils.hasText(request.getTarget())) {
            errors.add(new ValidationError("target", REQUIRED));
        }
        if (request.getClosedAt() == null) {
            errors.add(new ValidationError("closedAt", REQUIRED));
        }
        if (request.getSelectList() == null) {
            errors.add(new ValidationError("selectList", REQUIRED));
        }
        return errors;
    }
}
