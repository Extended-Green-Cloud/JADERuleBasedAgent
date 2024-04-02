package org.jrba.agentmodel.domain;

import static org.jrba.environment.types.EventTypeEnum.BASIC_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Instant;
import java.util.stream.Stream;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.domain.ExternalEvent;
import org.jrba.fixtures.TestAgentNodeCustom;
import org.jrba.fixtures.TestExternalEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.jrba.fixtures.TestAgentNodeDefault;

class AgentNodeUnitTest {

	private static Stream<Arguments> equalsAndHashData() {
		return Stream.of(
				arguments(
						new TestAgentNodeDefault(),
						new TestAgentNodeDefault(),
						true,
						true),
				arguments(
						new TestAgentNodeDefault("agent name 1", "agent type 1"),
						new TestAgentNodeDefault("agent name 1", "agent type 2"),
						false,
						false),
				arguments(
						new TestAgentNodeDefault("agent name", "agent type"),
						new TestAgentNodeDefault("agent name 2", "agent type 2"),
						false,
						false),
				arguments(
						new TestAgentNodeDefault("agent name", "agent type"),
						new TestAgentNodeCustom("agent name", "agent type"),
						false,
						true)
		);
	}

	@Test
	@DisplayName("Test basic AgentNode initialization.")
	void testAgentNodeInitialization() {
		final AgentNode agentNode = new TestAgentNodeDefault();

		assertEquals("Test name", agentNode.getAgentName());
		assertEquals("Test agent", agentNode.getAgentType());
		assertNull(agentNode.getMainWebSocket());
		assertEquals(0, agentNode.getEventsQueue().size());
	}

	@Test
	@DisplayName("Test add event to queue.")
	void testAddEventToQueue() {
		final AgentNode agentNode = new TestAgentNodeDefault();

		assertEquals("Test name", agentNode.getAgentName());
		assertEquals("Test agent", agentNode.getAgentType());
		assertNull(agentNode.getMainWebSocket());
		assertEquals(0, agentNode.getEventsQueue().size());

		agentNode.addEvent(new TestExternalEvent(Instant.now()));
		assertEquals(1, agentNode.getEventsQueue().size());

		final ExternalEvent testEvent = (ExternalEvent) agentNode.getEventsQueue().peek();
		assertNotNull(testEvent);
		assertEquals("Test agent", testEvent.getAgentName());
		assertEquals(BASIC_EVENT, testEvent.getEventType());
	}

	@Test
	@DisplayName("Test custom GUI method.")
	void testAgentNodeCustomUpdateGUI() {
		final TestAgentNodeCustom agentNode = new TestAgentNodeCustom();

		assertEquals("Test custom name", agentNode.getAgentName());
		assertEquals("Test custom agent", agentNode.getAgentType());
		assertEquals("", agentNode.getLastResult());

		agentNode.updateGUI(null);
		assertEquals("update GUI", agentNode.getLastResult());
	}

	@Test
	@DisplayName("Test AgentNode custom save monitoring data.")
	void testAgentNodeCustomSaveMonitoringData() {
		final TestAgentNodeCustom agentNode = new TestAgentNodeCustom();

		assertEquals("Test custom name", agentNode.getAgentName());
		assertEquals("Test custom agent", agentNode.getAgentType());
		assertEquals("", agentNode.getLastResult());

		agentNode.saveMonitoringData(null);
		assertEquals("save monitoring data", agentNode.getLastResult());
	}

	@ParameterizedTest
	@MethodSource("equalsAndHashData")
	void testAgentNodeEqualsAndHash(final AgentNode node1, final AgentNode node2,
			final boolean resultEquals, final boolean resultHash) {
		assertEquals(resultEquals, node1.equals(node2) && node2.equals(node1));
		assertEquals(resultHash, node1.hashCode() == node2.hashCode());
	}
}
