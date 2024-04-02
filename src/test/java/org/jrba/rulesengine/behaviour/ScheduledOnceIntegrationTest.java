package org.jrba.rulesengine.behaviour;

import static org.awaitility.Awaitility.await;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;

import java.util.concurrent.TimeUnit;

import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.jrba.integration.jade.RunRMA;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.TestRuleSet;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgent;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgentProps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JADESystemContext.class)
public class ScheduledOnceIntegrationTest {

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgent", agentName = "TestScheduleAgent")
	public TestScheduleAgent testScheduleAgent;

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, new TestRuleSet());
	}

	@Test
	@DisplayName("Test scheduling behaviour.")
	void testSchedulingBehaviour() throws InterruptedException {
		final RulesController<TestScheduleAgentProps, TestScheduleAgentNode> testController = new RulesController<>();
		testScheduleAgent.putO2AObject(testController, true);

		await().timeout(10, TimeUnit.SECONDS)
				.until(() -> testScheduleAgent.getProperties().getLastExecutedBehaviour()
						.equals("SCHEDULED_ACTION_TRIGGERED"));
	}
}
