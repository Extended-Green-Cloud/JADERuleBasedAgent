package org.jrba.rulesengine.behaviour;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static org.awaitility.Awaitility.await;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.TestRuleSet;
import org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgent;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgentProps;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JADESystemContext.class)
public class ListenForSingleMessageIntegrationTest {

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent", agentName = "TestServiceAgent")
	public TestServiceAgent testServiceAgent;

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgent", agentName = "TestSingleListenerAgent")
	public TestSingleListenerAgent testListenerAgent;

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, new TestRuleSet());
	}

	@Test
	@DisplayName("Test listen for message when message not received.")
	void testListenForMessageWhenNotReceived() throws InterruptedException {
		final RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode> testController = new RulesController<>();
		testListenerAgent.putO2AObject(testController, true);

		await().timeout(10, TimeUnit.SECONDS)
				.until(() -> testListenerAgent.getProperties().getLastExecutedBehaviour()
						.equals("HANDLE_NOT_RECEIVED"));
		assertNull(testListenerAgent.getProperties().getMessage());
	}

	@Test
	@DisplayName("Test listen for message when message received.")
	void testListenForMessageWhenReceived() throws InterruptedException {
		final RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode> testController = new RulesController<>();
		testListenerAgent.putO2AObject(testController, true);

		testServiceAgent.send(MessageBuilder.builder(0, PROPOSE)
				.withStringContent("123")
				.withReceivers(testListenerAgent.getAID()).build());

		await().timeout(10, TimeUnit.SECONDS)
				.until(() -> testListenerAgent.getProperties().getLastExecutedBehaviour().equals("HANDLE_RECEIVED"));
		assertNotNull(testListenerAgent.getProperties().getMessage());
	}

}
