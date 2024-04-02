package org.jrba.rulesengine.behaviour.fixtures.listener;

import static java.util.Objects.nonNull;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGES;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

public class TestListenForMessagesHandlerRule extends AgentBasicRule<TestListenerAgentProps, TestListenerAgentNode> {

	public TestListenForMessagesHandlerRule(
			RulesController<TestListenerAgentProps, TestListenerAgentNode> rulesController) {
		super(rulesController);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("HANDLE_RECEIVED_MESSAGES");

		if (nonNull(facts.get(MESSAGES))) {
			agentProps.setMessages(facts.get(MESSAGES));
		}
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_LISTENER_HANDLER_RULE",
				"handle messages received by TestListenerAgent",
				"handle received messages");
	}
}
