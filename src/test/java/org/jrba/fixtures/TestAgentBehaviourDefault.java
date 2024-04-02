package org.jrba.fixtures;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import jade.core.behaviours.OneShotBehaviour;

public class TestAgentBehaviourDefault extends OneShotBehaviour {

	private static final Logger logger = getLogger(TestAgentBehaviourDefault.class);

	private final TestAbstractAgentCustom agent;

	public TestAgentBehaviourDefault(final TestAbstractAgentCustom agent) {
		super(agent);
		this.agent = agent;
	}

	@Override
	public void action() {
		logger.info("Test behaviour executed.");
		agent.setLastExecutedBehaviour("TestBehaviour");
	}
}
