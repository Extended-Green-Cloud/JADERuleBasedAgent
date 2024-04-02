package org.jrba.fixtures;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestAgentNodeDefault extends AgentNode<TestAgentPropsDefault> {

	public TestAgentNodeDefault() {
		this.agentName = "Test name";
		this.agentType = "Test agent";
	}

	public TestAgentNodeDefault(final String name, final String agentType) {
		super(name, agentType);
	}

	@Override
	public void updateGUI(TestAgentPropsDefault props) {
	}

	@Override
	public void saveMonitoringData(TestAgentPropsDefault props) {
	}


	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
