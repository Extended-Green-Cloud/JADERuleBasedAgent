package org.jrba.fixtures;

import java.time.Instant;
import java.util.Map;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.domain.ExternalEvent;
import org.jrba.environment.types.EventType;
import org.jrba.environment.types.EventTypeEnum;

public class TestExternalEvent extends ExternalEvent {

	public TestExternalEvent(String agentName, EventType eventType, Instant occurrenceTime) {
		super(agentName, eventType, occurrenceTime);
	}

	public TestExternalEvent(final Instant occurrenceTime) {
		super("Test agent", EventTypeEnum.BASIC_EVENT, occurrenceTime);
	}

	@Override
	public <T extends AgentNode> void trigger(Map<String, T> agentNodes) {
		agentNodes.put("New test node", (T) new TestAgentNodeDefault());
	}
}
