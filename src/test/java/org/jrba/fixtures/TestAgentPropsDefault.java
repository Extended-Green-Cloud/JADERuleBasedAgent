package org.jrba.fixtures;

import org.jrba.agentmodel.domain.props.AgentProps;
import org.jrba.agentmodel.types.AgentType;

public class TestAgentPropsDefault extends AgentProps {

	public TestAgentPropsDefault(String agentName) {
		super(agentName);
	}

	public TestAgentPropsDefault(AgentType agentType, String agentName) {
		super(agentType, agentName);
	}

	public TestAgentPropsDefault(String agentType, String agentName) {
		super(agentType, agentName);
	}
}
