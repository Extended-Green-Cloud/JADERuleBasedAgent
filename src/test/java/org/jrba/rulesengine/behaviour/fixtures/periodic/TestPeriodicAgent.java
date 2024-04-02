package org.jrba.rulesengine.behaviour.fixtures.periodic;

import org.jrba.agentmodel.domain.AbstractAgent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestPeriodicAgent extends AbstractAgent<TestPeriodicAgentNode, TestPeriodicAgentProps> {

	public TestPeriodicAgent() {
	}

	@Override
	protected int getObjectsNumber() {
		return 1;
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		properties = new TestPeriodicAgentProps("TestPeriodicAgent");
	}

}
