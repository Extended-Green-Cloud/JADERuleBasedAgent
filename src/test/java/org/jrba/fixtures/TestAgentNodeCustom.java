package org.jrba.fixtures;

import java.net.URI;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.agentmodel.domain.props.AgentProps;
import org.jrba.environment.websocket.GuiWebSocketClient;

public class TestAgentNodeCustom extends AgentNode {

	private String lastResult;

	public TestAgentNodeCustom() {
		this.agentName = "Test custom name";
		this.agentType = "Test custom agent";
		this.lastResult = "";
	}

	public TestAgentNodeCustom(final String name, final String agentType) {
		super(name, agentType);
	}

	@Override
	public void updateGUI(AgentProps props) {
		this.lastResult = "update GUI";
	}

	@Override
	public void saveMonitoringData(AgentProps props) {
		this.lastResult = "save monitoring data";
	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return new GuiWebSocketClient(URI.create(url));
	}

	public String getLastResult() {
		return lastResult;
	}
}
