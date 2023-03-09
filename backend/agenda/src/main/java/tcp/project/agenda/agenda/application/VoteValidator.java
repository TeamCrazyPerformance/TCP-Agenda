package tcp.project.agenda.agenda.application;

import tcp.project.agenda.agenda.application.dto.VoteRequest;
import tcp.project.agenda.common.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class VoteValidator {
    public static final String REQUIRED = "required";

    public List<ValidationError> validate(VoteRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        if (request.getSelectList() == null) {
            errors.add(new ValidationError("selectList", REQUIRED));
        }
        return errors;
    }
}
