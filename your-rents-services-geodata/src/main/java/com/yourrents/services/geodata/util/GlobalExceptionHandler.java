package com.yourrents.services.geodata.util;

import com.yourrents.services.geodata.exception.DataNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException e, NativeWebRequest request) {
        return super.handleExceptionInternal(e,
                buildErrorResponse("Resource not found", e, request, HttpStatus.NOT_FOUND.value()),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<Object> dataNotFound(DataNotFoundException e, NativeWebRequest request) {
		return super.handleExceptionInternal(e,
				buildErrorResponse(e.getMessage(), e, request, HttpStatus.NOT_FOUND.value()),
				new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

    private ApiError buildErrorResponse(String message, Exception e, NativeWebRequest request, int status) {
        return new ApiError(message, e.getMessage(), status,
                ((HttpServletRequest)(request.getNativeRequest())).getRequestURI());
    }

}