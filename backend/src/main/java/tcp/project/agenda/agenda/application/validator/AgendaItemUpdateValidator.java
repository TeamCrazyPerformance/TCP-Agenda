package tcp.project.agenda.agenda.application.validator;

import tcp.project.agenda.agenda.application.dto.AgendaItemUpdateRequest;
import tcp.project.agenda.common.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class AgendaItemUpdateValidator {
    public static final String REQUIRED = "required";

    public List<ValidationError> validate(AgendaItemUpdateRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        if (request.getSelectList() == null) {
            errors.add(new ValidationError("selectList", REQUIRED));
        }
        return errors;
    }
}
