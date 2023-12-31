package com.yourrents.services.common.util.exception;

/*-
 * #%L
 * YourRents Service Common Util
 * %%
 * Copyright (C) 2023 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	protected final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<Object> dataNotFound(DataNotFoundException e, NativeWebRequest request) {
		logger.trace(e.getMessage(), e);
		return super.handleExceptionInternal(e,
				buildErrorResponse(e.getMessage(), e, request, HttpStatus.NOT_FOUND.value()),
				new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(DataConflictException.class)
	public ResponseEntity<Object> dataConflict(DataConflictException e, NativeWebRequest request) {
		logger.trace(e.getMessage(), e);
		return super.handleExceptionInternal(e,
				buildErrorResponse(e.getMessage(), e, request, HttpStatus.CONFLICT.value()),
				new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e, NativeWebRequest request) {
		logger.trace(e.getMessage(), e);
        return super.handleExceptionInternal(e,
                buildErrorResponse(e.getMessage(), e, request, HttpStatus.BAD_REQUEST.value()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

	/**
	 * Handle generic exceptions. Should be the last one to be declared.
	 * 
	 * This exception handler should never be invoked. If it is, it means that there is a bug in the code.
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exception(Exception e, NativeWebRequest request) {
		logger.error(e.getMessage(), e);
		return super.handleExceptionInternal(e,
				buildErrorResponse("Internal Server Error", e, request,
						HttpStatus.INTERNAL_SERVER_ERROR.value()),
				new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

    private ApiError buildErrorResponse(String message, Exception e, NativeWebRequest request, int status) {
        return new ApiError(message, e.getMessage(), status,
                ((HttpServletRequest)(request.getNativeRequest())).getRequestURI());
    }

}
