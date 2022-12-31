package com.witness.server.web.infrastructure;

import com.witness.server.exception.ServerException;
import com.witness.server.exception.ServerRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * Extends the default implementation of {@link ErrorAttributes}, being {@link DefaultErrorAttributes}. Therefore, it provides access to error
 * attributes, logs them and presents them to the user in the form of a customized error map.
 * </p>
 * <p>
 * This class extends {@link DefaultErrorAttributes} in the following methods:
 * <ul>
 *   <li>
 *     In the method {@link DefaultErrorAttributes#resolveException(HttpServletRequest, HttpServletResponse, Object, Exception)}, a log message with
 *     severity ERROR is issued containing the HTTP request method as well as path, including the message and stacktrace of the causing exception.
 *   </li>
 *   <li>
 *     In the method {@link DefaultErrorAttributes#getErrorAttributes(WebRequest, ErrorAttributeOptions)}, the resulting map of error attributes
 *     is extended by the value of the {@link ServerException#getErrorKey()} or {@link ServerRuntimeException#getErrorKey()} property if the
 *     {@link Throwable} returned {@link DefaultErrorAttributes#getError(WebRequest)} having been invoked on the {@link WebRequest} argument is
 *     an instance of {@link ServerException} or {@link ServerRuntimeException}, respectively.
 *   </li>
 * </ul>
 * </p>
 */
@Component
@Slf4j
public class ExtendedControllerErrorCollector extends DefaultErrorAttributes {

  @Override
  public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    log.error("Resolving exception at %s %s".formatted(request.getMethod(), request.getRequestURI()), ex);
    return super.resolveException(request, response, handler, ex);
  }

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
    var errorAttributes = super.getErrorAttributes(webRequest, options);

    var errorCause = getError(webRequest);
    if (errorCause instanceof ServerException) {
      errorAttributes.putIfAbsent("errorKey", ((ServerException) errorCause).getErrorKey());
    } else if (errorCause instanceof ServerRuntimeException) {
      errorAttributes.putIfAbsent("errorKey", ((ServerRuntimeException) errorCause).getErrorKey());
    }

    return errorAttributes;
  }
}
