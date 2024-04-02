package org.jrba.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.fixtures.TestEventType;
import org.jrba.fixtures.TestExternalEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.jrba.fixtures.TestAgentNodeDefault;

class ExternalEventUnitTest {

	private static final String AGENT_NAME = "Test name";
	private static final Instant INSTANT = Instant.parse("2024-01-01T12:00:00.00Z");

	private static TestExternalEvent externalEvent;

	@BeforeAll
	static void beforeAll() {
		externalEvent = new TestExternalEvent(AGENT_NAME, TestEventType.TEST_EVENT_TYPE, INSTANT);
	}

	@Test
	@DisplayName("Test external event constructor.")
	void testExternalEventInitialization() {
		assertEquals(AGENT_NAME, externalEvent.getAgentName());
		Assertions.assertEquals(TestEventType.TEST_EVENT_TYPE, externalEvent.getEventType());
		assertEquals(INSTANT, externalEvent.getOccurrenceTime());
	}

	@Test
	@DisplayName("Test triggering event.")
	void testEventTriggering() {
		final Map<String, AgentNode> agentNodeMap = new HashMap<>();

		// Trigger
		externalEvent.trigger(agentNodeMap);

		// After trigger
		assertEquals(1, agentNodeMap.size());
		assertTrue(agentNodeMap.containsKey("New test node"));
		assertTrue(agentNodeMap.containsValue(new TestAgentNodeDefault()));
	}
}
