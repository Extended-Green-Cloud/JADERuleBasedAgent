package org.jrba.exception;

/**
 * Exception thrown when system cannot find a rule set with given name
 */
public class CannotFindRuleSetException extends RuntimeException {

	public static final String NO_RULE_SET_FOUND = "Couldn't find a rule set with given name.";

	public CannotFindRuleSetException() {
		super(NO_RULE_SET_FOUND);
	}
}
