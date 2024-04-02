package org.jrba.rulesengine.behaviour.fixtures.periodic;

import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;

import java.util.HashSet;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.schedule.ScheduleOnce;
import org.jrba.rulesengine.behaviour.schedule.SchedulePeriodically;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.rules.RuleSetSelector;

import jade.core.behaviours.Behaviour;

public class TestPeriodicAgentInitialRule extends AgentBehaviourRule<TestPeriodicAgentProps, TestPeriodicAgentNode> {

	public TestPeriodicAgentInitialRule(
			final RulesController<TestPeriodicAgentProps, TestPeriodicAgentNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize set of behaviours that are to be added
	 */
	@Override
	protected Set<Behaviour> initializeBehaviours() {
		return new HashSet<>(
				Set.of(SchedulePeriodically.create(agent, new RuleSetFacts(0), "TEST_PERIODIC_RULE", controller)));
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(INITIALIZE_BEHAVIOURS_RULE,
				"initial behaviour rule TestPeriodicAgent",
				"initializes TestPeriodicAgent behaviours");
	}
}
