package org.jrba.fixtures;

import org.jrba.agentmodel.domain.args.AgentArgs;

public class TestAgentArgsDefault implements AgentArgs {

	@Override
	public String getName() {
		return "Test agent";
	}

	@Override
	public Object[] getObjectArray() {
		return new Object[] { getName(), "Test Parameter" };
	}
}
