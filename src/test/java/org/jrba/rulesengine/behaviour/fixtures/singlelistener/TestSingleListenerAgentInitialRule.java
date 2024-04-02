package org.jrba.rulesengine.behaviour.fixtures.singlelistener;

import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;

import java.util.HashSet;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.listen.ListenForSingleMessage;
import org.jrba.rulesengine.behaviour.schedule.SchedulePeriodically;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.core.behaviours.Behaviour;

public class TestSingleListenerAgentInitialRule
		extends AgentBehaviourRule<TestSingleListenerAgentProps, TestSingleListenerAgentNode> {

	public TestSingleListenerAgentInitialRule(
			final RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize set of behaviours that are to be added
	 */
	@Override
	protected Set<Behaviour> initializeBehaviours() {
		return new HashSet<>(
				Set.of(ListenForSingleMessage.create(agent, new RuleSetFacts(0), "TEST_SINGLE_LISTENER_RULE", controller)));
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(INITIALIZE_BEHAVIOURS_RULE,
				"initial behaviour rule TestSingleListenerAgent",
				"initializes TestSingleListenerAgent behaviours");
	}
}
