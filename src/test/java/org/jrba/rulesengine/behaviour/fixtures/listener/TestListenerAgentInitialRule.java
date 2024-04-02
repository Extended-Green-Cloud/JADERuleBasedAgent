package org.jrba.rulesengine.behaviour.fixtures.listener;

import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;

import java.util.HashSet;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.listen.ListenForMessages;
import org.jrba.rulesengine.behaviour.listen.ListenForSingleMessage;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.core.behaviours.Behaviour;

public class TestListenerAgentInitialRule
		extends AgentBehaviourRule<TestListenerAgentProps, TestListenerAgentNode> {

	public TestListenerAgentInitialRule(
			final RulesController<TestListenerAgentProps, TestListenerAgentNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize set of behaviours that are to be added
	 */
	@Override
	protected Set<Behaviour> initializeBehaviours() {
		return new HashSet<>(Set.of(ListenForMessages.create(agent, "TEST_LISTENER_RULE", controller, true)));
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(INITIALIZE_BEHAVIOURS_RULE,
				"initial behaviour rule TestListenerAgent",
				"initializes TestListenerAgent behaviours");
	}
}
