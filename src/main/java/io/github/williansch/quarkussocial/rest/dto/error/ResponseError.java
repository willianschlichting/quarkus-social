package io.github.williansch.quarkussocial.rest.dto.error;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import lombok.Data;

@Data
public class ResponseError {

    public static final Integer UNPROCESSABLE_ENTITY_STATS = 422;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromViolations(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errors = violations
        .stream()
        .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
        .collect(Collectors.toList());

        return new ResponseError("Validation error", errors);
    }

    private String message;
    private Collection<FieldError> errors;

    public Response withStatusCode(int code) {
        return Response.status(422).entity(this).build();
    }
    
}
