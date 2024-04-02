package org.jrba.agentmodel.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.fixtures.TestAgentNodeCustom;
import org.jrba.integration.websocket.WebsocketContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WebsocketContext.class)
class AgentNodeIntegrationTest {

	@Test
	@DisplayName("Test AgentNode initialization connected to socket.")
	void testCustomAgentNodeInitialization() throws InterruptedException {
		final TestAgentNodeCustom agentNode = new TestAgentNodeCustom();
		agentNode.connectSocket("ws://localhost:8080/");

		assertEquals("Test custom name", agentNode.getAgentName());
		assertEquals("Test custom agent", agentNode.getAgentType());
		assertNotNull(agentNode.getMainWebSocket());
		assertTrue(agentNode.getMainWebSocket().isOpen());
		assertEquals(0, agentNode.getEventsQueue().size());
	}

}
