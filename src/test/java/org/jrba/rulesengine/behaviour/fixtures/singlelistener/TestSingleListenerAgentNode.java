package org.jrba.rulesengine.behaviour.fixtures.singlelistener;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestSingleListenerAgentNode extends AgentNode<TestSingleListenerAgentProps> {

	@Override
	public void updateGUI(TestSingleListenerAgentProps props) {

	}

	@Override
	public void saveMonitoringData(TestSingleListenerAgentProps props) {

	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
