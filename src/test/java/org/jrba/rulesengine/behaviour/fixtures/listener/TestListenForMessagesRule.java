package org.jrba.rulesengine.behaviour.fixtures.listener;

import static jade.lang.acl.MessageTemplate.MatchContent;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.template.AgentMessageListenerRule;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TestListenForMessagesRule extends AgentMessageListenerRule<TestListenerAgentProps, TestListenerAgentNode> {

	public TestListenForMessagesRule(RulesController<TestListenerAgentProps, TestListenerAgentNode> controller,
			RuleSet ruleSet) {
		super(controller, ruleSet, MatchContent("123"), 1, "TEST_LISTENER_HANDLER_RULE");
	}


	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_LISTENER_RULE",
				"listening for messages sent to TestListenerAgent",
				"handle received messages");
	}
}
