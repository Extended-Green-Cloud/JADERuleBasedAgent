package org.jrba.agentmodel.domain;

import static org.jrba.agentmodel.types.AgentTypeEnum.BASIC;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.jrba.agentmodel.domain.props.AgentProps;
import org.jrba.fixtures.TestAgentNodeCustom;
import org.jrba.fixtures.TestAgentPropsDefault2;
import org.jrba.fixtures.TestAgentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;

class AgentPropsUnitTest {

	private static final String AGENT_NAME = "Test name";
	private static final String AGENT_TYPE = "Test type";

	private static Stream<Arguments> equalsAndHashData() {
		return Stream.of(
				arguments(
						new TestAgentPropsDefault("agent name"),
						new TestAgentPropsDefault("agent name"),
						true,
						true),
				arguments(
						new TestAgentPropsDefault("agent type 1", "agent name 1"),
						new TestAgentPropsDefault("agent type 2", "agent type 1"),
						false,
						false),
				arguments(
						new TestAgentPropsDefault("agent type", "agent name"),
						new TestAgentPropsDefault("agent type 1", "agent name 1"),
						false,
						false),
				arguments(
						new TestAgentPropsDefault("agent type", "agent name"),
						new TestAgentPropsDefault2("agent type", "agent name"),
						false,
						true)
		);
	}

	@Test
	@DisplayName("Test constructor with only agent name.")
	void testAgentPropsInitializationWithOnlyName() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);

		assertEquals(AGENT_NAME, testAgentProps.getAgentName());
		assertEquals(BASIC.getName(), testAgentProps.getAgentType());
		assertNull(testAgentProps.getAgentNode());
		assertTrue(testAgentProps.getSystemKnowledge().isEmpty());
		assertTrue(testAgentProps.getAgentKnowledge().asMap().isEmpty());
	}

	@Test
	@DisplayName("Test constructor with agent name and String type.")
	void testAgentPropsInitializationNameAndStringType() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_TYPE, AGENT_NAME);

		assertEquals(AGENT_NAME, testAgentProps.getAgentName());
		assertEquals(AGENT_TYPE, testAgentProps.getAgentType());
		assertNull(testAgentProps.getAgentNode());
		assertTrue(testAgentProps.getSystemKnowledge().isEmpty());
		assertTrue(testAgentProps.getAgentKnowledge().asMap().isEmpty());
	}

	@Test
	@DisplayName("Test constructor with agent name and String type.")
	void testAgentPropsInitializationNameAndType() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(TestAgentType.TEST_AGENT_TYPE, AGENT_NAME);

		assertEquals(AGENT_NAME, testAgentProps.getAgentName());
		assertEquals("Test agent enum", testAgentProps.getAgentType());
		assertNull(testAgentProps.getAgentNode());
		assertTrue(testAgentProps.getSystemKnowledge().isEmpty());
		assertTrue(testAgentProps.getAgentKnowledge().asMap().isEmpty());
	}

	@Test
	@DisplayName("Test update GUI when no agent node set.")
	void testUpdateGUIWhenNoNodeSet() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);

		assertNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::updateGUI);
	}

	@Test
	@DisplayName("Test update GUI when no update GUI implemented.")
	void testUpdateGUIWhenNoNodeUpdateGUIImplemented() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);

		testAgentProps.setAgentNode(new TestAgentNodeDefault());
		assertNotNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::updateGUI);
	}

	@Test
	@DisplayName("Test update GUI.")
	void testUpdateGUI() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);
		final TestAgentNodeCustom testAgentNodeCustom = new TestAgentNodeCustom();

		testAgentProps.setAgentNode(testAgentNodeCustom);
		assertNotNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::updateGUI);
		assertEquals("update GUI", testAgentNodeCustom.getLastResult());
	}

	@Test
	@DisplayName("Test save monitoring when no agent node set.")
	void testSaveMonitoringWhenNoNodeSet() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);

		assertNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::saveMonitoringData);
	}

	@Test
	@DisplayName("Test save monitoring when no update GUI implemented.")
	void testSaveMonitoringWhenNoNodeUpdateGUIImplemented() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);

		testAgentProps.setAgentNode(new TestAgentNodeDefault());
		assertNotNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::saveMonitoringData);
	}

	@Test
	@DisplayName("Test save monitoring.")
	void testSaveMonitoring() {
		final TestAgentPropsDefault testAgentProps = new TestAgentPropsDefault(AGENT_NAME);
		final TestAgentNodeCustom testAgentNodeCustom = new TestAgentNodeCustom();

		testAgentProps.setAgentNode(testAgentNodeCustom);
		assertNotNull(testAgentProps.getAgentNode());
		assertDoesNotThrow(testAgentProps::saveMonitoringData);
		assertEquals("save monitoring data", testAgentNodeCustom.getLastResult());
	}

	@ParameterizedTest
	@MethodSource("equalsAndHashData")
	void testAgentNodeEqualsAndHash(final AgentProps props1, final AgentProps props2,
			final boolean resultEquals, final boolean resultHash) {
		assertEquals(resultEquals, props1.equals(props2) && props2.equals(props1));
		assertEquals(resultHash, props1.hashCode() == props2.hashCode());
	}
}
