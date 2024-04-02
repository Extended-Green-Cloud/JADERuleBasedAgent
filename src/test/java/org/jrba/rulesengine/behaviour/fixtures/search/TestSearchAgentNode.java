package org.jrba.rulesengine.behaviour.fixtures.search;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestSearchAgentNode extends AgentNode<TestSearchAgentProps> {

	@Override
	public void updateGUI(TestSearchAgentProps props) {

	}

	@Override
	public void saveMonitoringData(TestSearchAgentProps props) {

	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
