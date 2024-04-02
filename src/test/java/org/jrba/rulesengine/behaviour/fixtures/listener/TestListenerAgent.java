package org.jrba.rulesengine.behaviour.fixtures.listener;

import org.jrba.agentmodel.domain.AbstractAgent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestListenerAgent extends AbstractAgent<TestListenerAgentNode, TestListenerAgentProps> {

	public TestListenerAgent() {
	}

	@Override
	protected int getObjectsNumber() {
		return 1;
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		properties = new TestListenerAgentProps("TestSingleListenerAgent");
	}

}
