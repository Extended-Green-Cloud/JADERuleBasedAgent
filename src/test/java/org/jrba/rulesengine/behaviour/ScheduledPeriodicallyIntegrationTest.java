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
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgent;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgentProps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JADESystemContext.class)
public class ScheduledPeriodicallyIntegrationTest {

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgent", agentName = "TestPeriodicAgent")
	public TestPeriodicAgent testPeriodicAgent;

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, new TestRuleSet());
	}

	@Test
	@DisplayName("Test periodically scheduling behaviour.")
	void testPeriodicallySchedulingBehaviour() throws InterruptedException {
		final RulesController<TestPeriodicAgentProps, TestPeriodicAgentNode> testController = new RulesController<>();
		testPeriodicAgent.putO2AObject(testController, true);

		await().timeout(4, TimeUnit.SECONDS)
				.until(() -> testPeriodicAgent.getProperties().getLastExecutedBehaviour()
						.equals("PERIODIC_ACTION_TRIGGERED"));

		await().timeout(4, TimeUnit.SECONDS)
				.until(() -> testPeriodicAgent.getProperties().getLastExecutedBehaviour()
						.equals("PERIODIC_ACTION_STOP"));
	}
}
