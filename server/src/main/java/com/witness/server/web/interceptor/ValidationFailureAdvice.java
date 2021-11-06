package com.witness.server.web.interceptor;

import com.witness.server.enumeration.ServerError;
import com.witness.server.service.TimeService;
import com.witness.server.web.interceptor.ValidationFailureAdvice.ValidationErrorsHolder.ValidationError;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Advice for REST controllers that registers exception handlers for when a validation performed by Spring fails. More specifically, this
 * type registers handlers for when an {@link MethodArgumentNotValidException} or {@link ConstraintViolationException} is propagated beyond the
 * scope of a controller. It transforms the validation data (e.g. what fields caused the violation for which reason(s)) into a
 * {@link ValidationErrorsHolder} object which is then sent as response body to the causing request, with status code {@link HttpStatus#BAD_REQUEST}.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ValidationFailureAdvice {
  private final MessageSource messageSource;
  private final TimeService timeService;

  @Autowired
  public ValidationFailureAdvice(MessageSource messageSource, TimeService timeService) {
    this.messageSource = messageSource;
    this.timeService = timeService;
  }

  /**
   * Validation errors with a {@link Valid} annotation as root cause result in {@link MethodArgumentNotValidException} objects.
   *
   * @param ex information about the validation failure
   * @return a {@link ValidationErrorsHolder} instance representing information about the validation failure that are relevant to the requester
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ValidationErrorsHolder methodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return buildValidationFailureResponse(() -> {
      var validationResult = ex.getBindingResult();
      var fieldErrors = validationResult.getFieldErrors();

      return fieldErrors.stream()
          .map(fieldError -> {
            var errorMessage = messageSource.getMessage(fieldError, Locale.ROOT);
            return new ValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getRejectedValue(),
                !StringUtils.hasText(errorMessage) ? fieldError.getDefaultMessage() : errorMessage);
          })
          .collect(Collectors.toList());
    }, ex);
  }

  /**
   * Validation errors with a {@link Validated} annotation or a JPA provider (e.g. Hibernate)as root cause result in
   * {@link ConstraintViolationException} objects.
   *
   * @param ex information about the validation failure
   * @return a {@link ValidationErrorsHolder} instance representing information about the validation failure that are relevant to the requester
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  public ValidationErrorsHolder constraintViolationException(ConstraintViolationException ex) {
    return buildValidationFailureResponse(() -> ex.getConstraintViolations().stream()
            .map(violation -> new ValidationError(
                violation.getRootBeanClass().getSimpleName(),
                violation.getPropertyPath().toString(),
                violation.getInvalidValue(),
                violation.getMessage()))
            .collect(Collectors.toList()),
        ex);
  }

  /**
   * Handles a validation error by providing a {@link ValidationErrorsHolder} object with message and timestamp. The error collection is delegated
   * to the {@code errorCollector} parameter.
   *
   * @param errorCollector a supplier that returns a collection of {@link ValidationError} instances which constitute the errors represented by
   *                       the {@link ValidationErrorsHolder} instance being created
   * @return a {@link ValidationErrorsHolder} with the current timestamp, a message indicating a validation error and the errors provided
   *     by {@code errorCollector}
   */
  private ValidationErrorsHolder buildValidationFailureResponse(Supplier<Collection<? extends ValidationError>> errorCollector, Throwable cause) {
    var errors = new ValidationErrorsHolder(timeService.getCurrentTime());
    errors.setErrors(errorCollector.get());

    log.error("Input validation failed with %d errors: %s".formatted(errors.getValidationErrors().size(), errors), cause);
    return errors;
  }

  @Data
  static class ValidationErrorsHolder {
    private final int status;
    private final String error;
    private final ServerError errorKey;

    private String message;
    private final ZonedDateTime timestamp;
    private final List<ValidationError> validationErrors;

    ValidationErrorsHolder(ZonedDateTime timestamp) {
      this.timestamp = timestamp;

      this.status = HttpStatus.BAD_REQUEST.value();
      this.error = HttpStatus.BAD_REQUEST.getReasonPhrase();
      this.errorKey = ServerError.VALIDATION_ERROR;
      this.validationErrors = new ArrayList<>();
    }

    public void setErrors(Collection<? extends ValidationError> errors) {
      validationErrors.clear();
      validationErrors.addAll(errors);
      message = "There were validation errors: " + getValidationErrorsRepresentation();
    }

    @Override
    public String toString() {
      return "{%s}".formatted(getValidationErrorsRepresentation());
    }

    private String getValidationErrorsRepresentation() {
      var errorRepresentation = new StringBuilder();
      for (var fieldError : this.validationErrors) {
        errorRepresentation.append("[");
        errorRepresentation.append(fieldError.getPropertyPath()).append(": ").append(fieldError.getMessage());
        errorRepresentation.append(" (value='").append(fieldError.getInvalidValue()).append("')");
        errorRepresentation.append("], ");
      }
      var errorString = errorRepresentation.toString();
      return errorString.substring(0, errorString.length() - 2);
    }

    @Data
    @AllArgsConstructor
    static class ValidationError {
      private final String rootBean;
      private final String propertyPath;
      private final Object invalidValue;
      private final String message;
    }
  }
}
