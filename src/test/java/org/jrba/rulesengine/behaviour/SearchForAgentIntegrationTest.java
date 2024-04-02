package org.jrba.rulesengine.behaviour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.jrba.utils.yellowpages.YellowPagesRegister.register;

import java.util.concurrent.TimeUnit;

import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.TestRuleSet;
import org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgent;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentProps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(JADESystemContext.class)
public class SearchForAgentIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(SearchForAgentIntegrationTest.class);

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent", agentName = "TestServiceAgent")
	public TestServiceAgent testServiceAgent;

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgent", agentName = "TestSearchAgent")
	public TestSearchAgent testSearchAgent;

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, new TestRuleSet());
	}

	@Test
	@DisplayName("Test search for agent when agent not present.")
	void testSearchAgentServiceWhenNotPresent() throws InterruptedException {
		final RulesController<TestSearchAgentProps, TestSearchAgentNode> testController = new RulesController<>();
		testSearchAgent.putO2AObject(testController, true);

		await().timeout(10, TimeUnit.SECONDS)
				.until(() -> testSearchAgent.getProperties().getLastExecutedBehaviour().equals("NO_RESULTS_EXECUTED"));
	}

	@Test
	@DisplayName("Test search for agent when agent is found.")
	void testSearchAgentServiceWhenFound() throws InterruptedException {
		register(testServiceAgent, testServiceAgent.getDefaultDF(), "TEST_SERVICE", "TEST_SERVICE_NAME");
		logger.info("Agent registered in DF.");

		final RulesController<TestSearchAgentProps, TestSearchAgentNode> testController = new RulesController<>();
		testSearchAgent.putO2AObject(testController, true);

		await().timeout(10, TimeUnit.SECONDS)
				.untilAsserted(() -> {
					assertThat(testSearchAgent.getProperties().getFoundResults()).contains(testServiceAgent.getAID());
					assertThat(testSearchAgent.getProperties().getLastExecutedBehaviour()).isEqualTo(
							"AGENT_FOUND_EXECUTED");
				});
	}
}
