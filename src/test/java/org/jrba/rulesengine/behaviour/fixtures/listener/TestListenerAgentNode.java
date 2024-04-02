package org.jrba.rulesengine.behaviour.fixtures.listener;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestListenerAgentNode extends AgentNode<TestListenerAgentProps> {

	@Override
	public void updateGUI(TestListenerAgentProps props) {

	}

	@Override
	public void saveMonitoringData(TestListenerAgentProps props) {

	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
