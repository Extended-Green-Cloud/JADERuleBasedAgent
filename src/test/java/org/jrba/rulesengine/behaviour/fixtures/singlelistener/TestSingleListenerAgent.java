package org.jrba.rulesengine.behaviour.fixtures.singlelistener;

import org.jrba.agentmodel.domain.AbstractAgent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestSingleListenerAgent extends AbstractAgent<TestSingleListenerAgentNode, TestSingleListenerAgentProps> {

	public TestSingleListenerAgent() {
	}

	@Override
	protected int getObjectsNumber() {
		return 1;
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		properties = new TestSingleListenerAgentProps("TestSingleListenerAgent");
	}

}
