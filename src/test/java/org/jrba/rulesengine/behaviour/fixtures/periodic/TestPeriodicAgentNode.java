package org.jrba.rulesengine.behaviour.fixtures.periodic;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestPeriodicAgentNode extends AgentNode<TestPeriodicAgentProps> {

	@Override
	public void updateGUI(TestPeriodicAgentProps props) {

	}

	@Override
	public void saveMonitoringData(TestPeriodicAgentProps props) {

	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
