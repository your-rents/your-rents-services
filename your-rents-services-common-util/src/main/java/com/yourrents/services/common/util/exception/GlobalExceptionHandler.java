package com.yourrents.services.common.util.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	protected final Log logger = LogFactory.getLog(getClass());


	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> sQLException(Exception e, NativeWebRequest request) {
		logger.error(e.getMessage(), e);
		return super.handleExceptionInternal(e,
				buildErrorResponse("Internal Server Error", e, request,
						HttpStatus.INTERNAL_SERVER_ERROR.value()),
				new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<Object> dataNotFound(DataNotFoundException e, NativeWebRequest request) {
		logger.error(e.getMessage(), e);
		return super.handleExceptionInternal(e,
				buildErrorResponse(e.getMessage(), e, request, HttpStatus.NOT_FOUND.value()),
				new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e, NativeWebRequest request) {
		logger.error(e.getMessage(), e);
        return super.handleExceptionInternal(e,
                buildErrorResponse(e.getMessage(), e, request, HttpStatus.BAD_REQUEST.value()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ApiError buildErrorResponse(String message, Exception e, NativeWebRequest request, int status) {
        return new ApiError(message, e.getMessage(), status,
                ((HttpServletRequest)(request.getNativeRequest())).getRequestURI());
    }

}