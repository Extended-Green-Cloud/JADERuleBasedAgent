package org.jrba.rulesengine.behaviour.fixtures.schedule;

import org.jrba.agentmodel.domain.AbstractAgent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestScheduleAgent extends AbstractAgent<TestScheduleAgentNode, TestScheduleAgentProps> {

	public TestScheduleAgent() {
	}

	@Override
	protected int getObjectsNumber() {
		return 1;
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		properties = new TestScheduleAgentProps("TestScheduleAgent");
	}

}
