package org.jrba.rulesengine.constants;

/**
 * Class with constants describing available fact types
 */
public class FactTypeConstants {

	/**
	 * Identifier of a fact specifying rule type.
	 */
	public static final String RULE_TYPE = "rule-type";

	// RULE TYPE FACT
	/**
	 * Identifier of a fact specifying rule step type.
	 */
	public static final String RULE_STEP = "rule-step";
	/**
	 * Identifier of a fact specifying input data.
	 */
	public static final String INPUT_DATA = "input-data";

	// DATA FACT
	/**
	 * Identifier of a fact specifying results.
	 */
	public static final String RESULT = "result";

	// RESULT FACT
	/**
	 * Identifier of a fact specifying a collection of agents.
	 */
	public static final String AGENTS = "agents";

	// AGENTS FACTS
	/**
	 * Identifier of a fact specifying a single agent.
	 */
	public static final String AGENT = "agent";
	/**
	 * Identifier of a fact specifying index of the rule set.
	 */
	public static final String RULE_SET_IDX = "rule-set-idx";

	// RULE SET FACT
	/**
	 * Identifier of a fact specifying type of the rule set.
	 */
	public static final String RULE_SET_TYPE = "rule-set-type";
	/**
	 * Identifier of a fact specifying ambiguous resources.
	 */
	public static final String RESOURCES = "resources";

	// RESOURCES FACTS
	/**
	 * Identifier of a fact specifying time of the event's trigger.
	 */
	public static final String EVENT_TIME = "event-time";

	// EVENTS FACTS
	/**
	 * Identifier of a fact specifying duration of the event.
	 */
	public static final String EVENT_DURATION = "event-duration";
	/**
	 * Identifier of a fact specifying event's cause.
	 */
	public static final String EVENT_CAUSE = "event-cause";
	/**
	 * Identifier of a fact specifying whether the event has finished.
	 */
	public static final String EVENT_IS_FINISHED = "event-is-finished";
	/**
	 * Identifier of a fact specifying event.
	 */
	public static final String EVENT = "event";
	/**
	 * Identifier of a fact specifying time error associated with an event.
	 */
	public static final String SET_EVENT_ERROR = "set-event-error";
	/**
	 * Identifier of a fact specifying behaviour's trigger time.
	 */
	public static final String TRIGGER_TIME = "trigger-time";

	// BEHAVIOUR FACTS
	/**
	 * Identifier of a fact specifying behaviour's trigger period.
	 */
	public static final String TRIGGER_PERIOD = "trigger-period";
	/**
	 * Identifier of a fact specifying agents, which added their services to DF.
	 */
	public static final String SUBSCRIPTION_ADDED_AGENTS = "subscription-added-agents";

	// SUBSCRIPTION FACTS
	/**
	 * Identifier of a fact specifying agents, which removed their services from DF.
	 */
	public static final String SUBSCRIPTION_REMOVED_AGENTS = "subscription-removed-agents";
	/**
	 * Identifier of a fact specifying a collection of messages.
	 */
	public static final String MESSAGES = "messages";

	// MESSAGE FACTS
	/**
	 * Identifier of a fact specifying a single message.
	 */
	public static final String MESSAGE = "message";
	/**
	 * Identifier of a fact specifying a message template.
	 */
	public static final String MESSAGE_TEMPLATE = "message-template";
	/**
	 * Identifier of a fact specifying message delivery expiration time.
	 */
	public static final String MESSAGE_EXPIRATION = "message-expiration";
	/**
	 * Identifier of a fact specifying message type.
	 */
	public static final String MESSAGE_TYPE = "message-type";
	/**
	 * Identifier of a fact specifying message content.
	 */
	public static final String MESSAGE_CONTENT = "message-content";
	/**
	 * Identifier of a fact specifying original message (depending on the context).
	 */
	public static final String ORIGINAL_MESSAGE = "original-message";
	/**
	 * Identifier of a fact specifying received message.
	 */
	public static final String RECEIVED_MESSAGE = "received-message";
	/**
	 * Identifier of a fact specifying created CFP message.
	 */
	public static final String CFP_CREATE_MESSAGE = "cfp-create-message";

	// CFP BEHAVIOUR FACTS
	/**
	 * Identifier of a fact specifying best CFP response message.
	 */
	public static final String CFP_BEST_MESSAGE = "cfp-best-message";
	/**
	 * Identifier of a fact specifying CFP response to reject.
	 */
	public static final String CFP_REJECT_MESSAGE = "cfp-reject-message";
	/**
	 * Identifier of a fact specifying received CFP proposals.
	 */
	public static final String CFP_RECEIVED_PROPOSALS = "cfp-received-proposals";
	/**
	 * Identifier of a fact specifying newly received CFP proposal.
	 */
	public static final String CFP_NEW_PROPOSAL = "cfp-new-proposal";
	/**
	 * Identifier of a fact specifying CFP comparison result.
	 */
	public static final String CFP_RESULT = "cfp-result";
	/**
	 * Identifier of a fact specifying sent proposal message.
	 */
	public static final String PROPOSAL_CREATE_MESSAGE = "proposal-create-message";

	// PROPOSAL BEHAVIOUR FACTS
	/**
	 * Identifier of a fact specifying received ACCEPT_PROPOSAL response.
	 */
	public static final String PROPOSAL_ACCEPT_MESSAGE = "proposal-accept-message";
	/**
	 * Identifier of a fact specifying received REJECT_PROPOSAL response.
	 */
	public static final String PROPOSAL_REJECT_MESSAGE = "proposal-reject-message";
	/**
	 * Identifier of a fact specifying sent request message.
	 */
	public static final String REQUEST_CREATE_MESSAGE = "request-create-message";

	// REQUEST BEHAVIOUR FACTS
	/**
	 * Identifier of a fact specifying received INFORM message.
	 */
	public static final String REQUEST_INFORM_MESSAGE = "request-inform-message";
	/**
	 * Identifier of a fact specifying received REFUSE message.
	 */
	public static final String REQUEST_REFUSE_MESSAGE = "request-refuse-message";
	/**
	 * Identifier of a fact specifying received FAILURE message.
	 */
	public static final String REQUEST_FAILURE_MESSAGE = "request-failure-message";
	/**
	 * Identifier of a fact specifying received INFORM messages.
	 */
	public static final String REQUEST_INFORM_RESULTS_MESSAGES = "request-inform-results-messages";
	/**
	 * Identifier of a fact specifying received FAILURE messages.
	 */
	public static final String REQUEST_FAILURE_RESULTS_MESSAGES = "request-failure-results-messages";

	// SUBSCRIPTION BEHAVIOUR FACTS
	/**
	 * Identifier of a fact specifying sent subscription message.
	 */
	public static final String SUBSCRIPTION_CREATE_MESSAGE = "subscription-create-message";
}
