package org.jrba.rulesengine.behaviour.fixtures.search;

import static org.jrba.utils.yellowpages.YellowPagesRegister.register;
import static org.slf4j.LoggerFactory.getLogger;

import org.jrba.agentmodel.domain.AbstractAgent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestSearchAgent extends AbstractAgent<TestSearchAgentNode, TestSearchAgentProps> {

	public TestSearchAgent() {
	}

	@Override
	protected int getObjectsNumber() {
		return 1;
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		properties = new TestSearchAgentProps("TestSearchAgent");
	}


}
