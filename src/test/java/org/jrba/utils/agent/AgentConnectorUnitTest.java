package org.jrba.utils.agent;

import static org.jrba.utils.agent.AgentConnector.connectAgentObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.jrba.agentmodel.domain.AbstractAgent;
import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AgentConnectorUnitTest {

	@Test
	@DisplayName("Test connect agent to agent node")
	void testConnectAgentNode() {
		var mockAgent = spy(AbstractAgent.class);
		var mockAgentNode = spy(AgentNode.class);

		assertNull(mockAgent.getAgentNode());

		connectAgentObject(mockAgent, mockAgentNode);
		assertEquals(mockAgentNode, mockAgent.getAgentNode());
	}

	@Test
	@DisplayName("Test connect agent to rules controller")
	void testConnectAgentRulesController() {
		var mockAgent = spy(AbstractAgent.class);
		var mockRulesController = spy(RulesController.class);

		doReturn("Test agent").when(mockAgent).getName();
		mockAgent.setProperties(new TestAgentPropsDefault("Test agent"));
		assertNull(mockAgent.getRulesController());

		connectAgentObject(mockAgent, mockRulesController);
		assertEquals(mockRulesController, mockAgent.getRulesController());
	}
}
