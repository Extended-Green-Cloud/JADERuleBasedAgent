package org.jrba.fixtures;

import org.jrba.agentmodel.types.AgentType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestAgentType implements AgentType {

	TEST_AGENT_TYPE("Test agent enum");

	final String name;
}
