package org.jrba.rulesengine.behaviour.fixtures;

import static org.jrba.utils.yellowpages.YellowPagesRegister.register;

import org.jrba.agentmodel.domain.AbstractAgent;
import org.jrba.environment.websocket.GuiWebSocketServer;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestServiceAgent extends AbstractAgent<TestAgentNodeDefault, TestAgentPropsDefault> {

	private static final Logger logger = LoggerFactory.getLogger(TestServiceAgent.class);

	public TestServiceAgent() {
	}

	@Override
	protected void initializeAgent(Object[] arguments) {
		register(this, this.getDefaultDF(), "TEST_SERVICE", "TEST_SERVICE_NAME");
		logger.info("Agent registered in DF.");
	}
}
