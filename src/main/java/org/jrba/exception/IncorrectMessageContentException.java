package org.jrba.exception;

/**
 * Exception thrown when content of messages exchanged between agents is incorrect
 */
public class IncorrectMessageContentException extends RuntimeException {

	public static final String INCORRECT_MESSAGE_FORMAT = "The provided message content has incorrect format";

	public IncorrectMessageContentException() {
		super(INCORRECT_MESSAGE_FORMAT);
	}

}
