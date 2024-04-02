package org.jrba.rulesengine.behaviour.fixtures.schedule;

import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;

import java.util.HashSet;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.schedule.ScheduleOnce;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.rules.RuleSetSelector;

import jade.core.behaviours.Behaviour;

public class TestScheduleAgentInitialRule extends AgentBehaviourRule<TestScheduleAgentProps, TestScheduleAgentNode> {

	public TestScheduleAgentInitialRule(
			final RulesController<TestScheduleAgentProps, TestScheduleAgentNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize set of behaviours that are to be added
	 */
	@Override
	protected Set<Behaviour> initializeBehaviours() {
		return new HashSet<>(
				Set.of(ScheduleOnce.create(agent, new RuleSetFacts(0), "TEST_SCHEDULE_RULE", controller,
						RuleSetSelector.SELECT_LATEST)));
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(INITIALIZE_BEHAVIOURS_RULE,
				"initial behaviour rule TestScheduleAgent",
				"initializes TestScheduleAgent behaviours");
	}
}
