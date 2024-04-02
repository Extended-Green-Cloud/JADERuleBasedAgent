package org.jrba.rulesengine.constants;

/**
 * Class with constants describing available fact types
 */
public class MVELParameterConstants {

	// RULES CONTROLLER PARAMETERS
	public static final String RULES_CONTROLLER = "controller";

	// LOGGER PARAMETERS
	public static final String LOGGER = "logger";

	// AGENT PARAMETERS
	public static final String AGENT = "agent";
	public static final String AGENTS = "agents";
	public static final String AGENT_PROPS = "agentProps";
	public static final String AGENT_NODE = "agentNode";

	// FACTS PARAMETERS
	public static final String FACTS = "facts";

	// CFP RULE PARAMETERS
	public static final String ALL_PROPOSALS = "allProposals";
	public static final String BEST_PROPOSAL = "bestProposal";
	public static final String NEW_PROPOSAL = "newProposal";
	public static final String PROPOSAL_TO_REJECT = "proposalToReject";

	// SUBSCRIPTION RULE PARAMETERS
	public static final String ADDED_AGENTS = "addedAgents";
	public static final String REMOVED_AGENTS = "removedAgents";

	// PROPOSAL RULE PARAMETERS
	public static final String ACCEPT_MESSAGE = "acceptMessage";
	public static final String REJECT_MESSAGE = "rejectMessage";

	// REQUEST RULE PARAMETERS
	public static final String INFORM = "inform";
	public static final String REFUSE = "refuse";
	public static final String FAILURE = "failure";
	public static final String INFORM_RESULTS = "informResults";
	public static final String FAILURE_RESULTS = "failureResults";

	// SINGLE MESSAGE RULE PARAMETERS
	public static final String MESSAGE = "message";
}
