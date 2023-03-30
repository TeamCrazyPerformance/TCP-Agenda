package tcp.project.agenda.agenda.application.validator;

import org.springframework.util.StringUtils;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.common.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class AgendaUpdateValidator {
    public static final String REQUIRED = "required";

    public List<ValidationError> validate(AgendaUpdateRequest request) {
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
        return errors;
    }
}
