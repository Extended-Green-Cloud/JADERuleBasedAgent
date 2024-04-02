package org.jrba.exception;

/**
 * Exception thrown when JADE agent controller couldn't be created
 */
public class JadeControllerException extends RuntimeException {

	public JadeControllerException(String message, Exception exception) {
		super(message, exception);
	}
}
