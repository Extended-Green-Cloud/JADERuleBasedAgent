package org.jrba.rulesengine.behaviour;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.jrba.utils.yellowpages.YellowPagesRegister.deregister;

import java.util.concurrent.TimeUnit;

import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.TestRuleSet;
import org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgent;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgentProps;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JADESystemContext.class)
public class ListenForMessagesIntegrationTest {

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.TestServiceAgent", agentName = "TestServiceAgent")
	public TestServiceAgent testServiceAgent;

	@AgentContext(agentClass = "org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgent", agentName = "TestListenerAgent")
	public TestListenerAgent testListenerAgent;

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, new TestRuleSet());
	}

	@AfterEach
	void clear() {
		deregister(testServiceAgent, testServiceAgent.getDefaultDF(), "TEST_SERVICE", "TEST_SERVICE_NAME");
	}

	@Test
	@DisplayName("Test listen for messages when messages received.")
	void testListenForMessagesWhenReceived() throws InterruptedException {
		final RulesController<TestListenerAgentProps, TestListenerAgentNode> testController = new RulesController<>();
		testListenerAgent.putO2AObject(testController, true);

		testServiceAgent.send(MessageBuilder.builder(0, PROPOSE)
				.withStringContent("123")
				.withReceivers(testListenerAgent.getAID()).build());

		await().timeout(5, TimeUnit.SECONDS)
				.until(() -> testListenerAgent.getProperties().getLastExecutedBehaviour().equals("HANDLE_RECEIVED_MESSAGES"));
		assertThat(testListenerAgent.getProperties().getMessages()).hasSize(1);
	}

}
