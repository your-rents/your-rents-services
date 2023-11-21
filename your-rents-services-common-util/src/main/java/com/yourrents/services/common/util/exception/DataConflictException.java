package com.yourrents.services.common.util.exception;

/**
 * Thrown to indicate a conflict with the current state of the database.
 */
public class DataConflictException extends RuntimeException {

	public DataConflictException(String message) {
		super(message);
	}

	public DataConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}
