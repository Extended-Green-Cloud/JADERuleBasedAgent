package org.jrba.agentmodel.behaviour;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JADESystemContext.class)
public class ListenForControllerObjectsIntegrationTest {

	@AgentContext(agentClass = "org.jrba.fixtures.TestAbstractAgentCustom", agentName = "TestAgent")
	public TestAbstractAgentCustom testAgent;

	@Test
	@DisplayName("Test ListenForControllerObjects initialization without initializer methods.")
	void testListenForControllerObjects() throws InterruptedException {
		final TestAgentNodeDefault testNode = new TestAgentNodeDefault();
		testAgent.putO2AObject(testNode, true);

		await().timeout(10, SECONDS)
				.untilAsserted(() -> assertEquals("TestBehaviour", testAgent.getLastExecutedBehaviour()));
		assertEquals(testAgent.getAgentNode(), testNode);
	}

}
