package org.jrba.exception;

/**
 * Exception thrown when the system cannot read a file
 */
public class InvalidFileException extends RuntimeException {

	public InvalidFileException(final String message) {
		super(message);
	}

}
