package org.jrba.rulesengine.constants;

/**
 * Class with constants describing available fact types
 */
public class FactTypeConstants {

	// RULE TYPE FACT
	public static final String RULE_TYPE = "rule-type";
	public static final String RULE_STEP = "rule-step";

	// DATA FACT
	public static final String INPUT_DATA = "input-data";

	// RESULT FACT
	public static final String RESULT = "result";

	// AGENTS FACTS
	public static final String AGENTS = "agents";
	public static final String AGENT = "agent";

	// RULE SET FACT
	public static final String RULE_SET_IDX = "rule-set-idx";
	public static final String RULE_SET_TYPE = "rule-set-type";

	// RESOURCES FACTS
	public static final String RESOURCES = "resources";

	// EVENTS FACTS
	public static final String EVENT_TIME = "event-time";
	public static final String EVENT_DURATION = "event-duration";
	public static final String EVENT_CAUSE = "event-cause";
	public static final String EVENT_IS_FINISHED = "event-is-finished";
	public static final String EVENT = "event";
	public static final String SET_EVENT_ERROR = "set-event-error";

	// BEHAVIOUR FACTS
	public static final String TRIGGER_TIME = "trigger-time";
	public static final String TRIGGER_PERIOD = "trigger-period";
	public static final String SUBSCRIPTION_ADDED_AGENTS = "subscription-added-agents";
	public static final String SUBSCRIPTION_REMOVED_AGENTS = "subscription-removed-agents";

	// MESSAGE FACTS
	public static final String MESSAGES = "messages";
	public static final String MESSAGE = "message";
	public static final String MESSAGE_TEMPLATE = "message-template";
	public static final String MESSAGE_EXPIRATION = "message-expiration";
	public static final String MESSAGE_TYPE = "message-type";
	public static final String MESSAGE_CONTENT = "message-content";
	public static final String ORIGINAL_MESSAGE = "original-message";
	public static final String RECEIVED_MESSAGE = "received-message";

	// CFP BEHAVIOUR FACTS
	public static final String CFP_CREATE_MESSAGE = "cfp-create-message";
	public static final String CFP_BEST_MESSAGE = "cfp-best-message";
	public static final String CFP_REJECT_MESSAGE = "cfp-reject-message";
	public static final String CFP_RECEIVED_PROPOSALS = "cfp-received-proposals";
	public static final String CFP_NEW_PROPOSAL = "cfp-new-proposal";
	public static final String CFP_RESULT = "cfp-result";

	// PROPOSAL BEHAVIOUR FACTS
	public static final String PROPOSAL_CREATE_MESSAGE = "proposal-create-message";
	public static final String PROPOSAL_ACCEPT_MESSAGE = "proposal-accept-message";
	public static final String PROPOSAL_REJECT_MESSAGE = "proposal-reject-message";

	// REQUEST BEHAVIOUR FACTS
	public static final String REQUEST_CREATE_MESSAGE = "request-create-message";
	public static final String REQUEST_INFORM_MESSAGE = "request-inform-message";
	public static final String REQUEST_REFUSE_MESSAGE = "request-refuse-message";
	public static final String REQUEST_FAILURE_MESSAGE = "request-failure-message";
	public static final String REQUEST_INFORM_RESULTS_MESSAGES = "request-inform-results-messages";
	public static final String REQUEST_FAILURE_RESULTS_MESSAGES = "request-failure-results-messages";

	// SUBSCRIPTION BEHAVIOUR FACTS
	public static final String SUBSCRIPTION_CREATE_MESSAGE = "subscription-create-message";
}
