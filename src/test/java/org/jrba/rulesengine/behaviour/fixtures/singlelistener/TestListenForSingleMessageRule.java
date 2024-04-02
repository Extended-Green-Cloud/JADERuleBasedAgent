package org.jrba.rulesengine.behaviour.fixtures.singlelistener;

import static jade.lang.acl.MessageTemplate.MatchContent;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.template.AgentSingleMessageListenerRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TestListenForSingleMessageRule
		extends AgentSingleMessageListenerRule<TestSingleListenerAgentProps, TestSingleListenerAgentNode> {

	public TestListenForSingleMessageRule(
			final RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode> controller) {
		super(controller);
	}

	@Override
	protected MessageTemplate constructMessageTemplate(RuleSetFacts facts) {
		return MatchContent("123");
	}

	@Override
	protected long specifyExpirationTime(RuleSetFacts facts) {
		return 4000;
	}

	@Override
	protected void handleMessageProcessing(ACLMessage message, RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("HANDLE_RECEIVED");
		agentProps.setMessage(message);
	}

	@Override
	protected void handleMessageNotReceived(RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("HANDLE_NOT_RECEIVED");
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_SINGLE_LISTENER_RULE",
				"listening for single message sent to TestSingleListenerAgent",
				"handle received message");
	}
}
