package org.jrba.rulesengine.behaviour.fixtures.schedule;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.websocket.GuiWebSocketClient;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentProps;

public class TestScheduleAgentNode extends AgentNode<TestScheduleAgentProps> {

	@Override
	public void updateGUI(TestScheduleAgentProps props) {

	}

	@Override
	public void saveMonitoringData(TestScheduleAgentProps props) {

	}

	@Override
	public GuiWebSocketClient initializeSocket(String url) {
		return null;
	}
}
