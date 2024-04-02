package org.jrba.rulesengine.behaviour.fixtures.schedule;

import static org.jrba.utils.yellowpages.YellowPagesRegister.search;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentProps;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.template.AgentScheduledRule;
import org.jrba.rulesengine.rule.template.AgentSearchRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.core.AID;

public class TestScheduleForAgentRule extends AgentScheduledRule<TestScheduleAgentProps, TestScheduleAgentNode> {

	public TestScheduleForAgentRule(final RulesController<TestScheduleAgentProps, TestScheduleAgentNode> controller) {
		super(controller);
	}

	@Override
	protected Date specifyTime(RuleSetFacts facts) {
		return Date.from(Instant.now().plus(3, ChronoUnit.SECONDS));
	}

	@Override
	protected void handleActionTrigger(RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("SCHEDULED_ACTION_TRIGGERED");
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_SCHEDULE_RULE",
				"scheduling action for TestScheduleAgent",
				"handle scheduled action TestScheduleAgent");
	}
}
