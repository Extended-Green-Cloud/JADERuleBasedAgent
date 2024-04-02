package org.jrba.rulesengine.behaviour.fixtures.periodic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.template.AgentPeriodicRule;
import org.jrba.rulesengine.rule.template.AgentScheduledRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

public class TestSchedulePeriodicallyForAgentRule
		extends AgentPeriodicRule<TestPeriodicAgentProps, TestPeriodicAgentNode> {

	public TestSchedulePeriodicallyForAgentRule(
			final RulesController<TestPeriodicAgentProps, TestPeriodicAgentNode> controller) {
		super(controller);
	}

	@Override
	protected long specifyPeriod() {
		return 3000;
	}

	@Override
	protected void handleActionTrigger(RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("PERIODIC_ACTION_TRIGGERED");

		if (agentProps.getCounter().incrementAndGet() == 2) {
			agentProps.setLastExecutedBehaviour("PERIODIC_ACTION_STOP");
		}
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_PERIODIC_RULE",
				"scheduling periodically action for TestPeriodicAgent",
				"handle scheduled periodic action TestPeriodicAgent");
	}
}
