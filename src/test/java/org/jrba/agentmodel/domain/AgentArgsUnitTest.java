package org.jrba.agentmodel.domain;


import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.jrba.fixtures.TestAgentArgsDefault;

class AgentArgsUnitTest {

	@Test
	@DisplayName("Test AgentArgs initialized object array.")
	void testAgentArgsInitialObjectArray() {
		final TestAgentArgsDefault testAgentArgs = new TestAgentArgsDefault();

		assertEquals(2, testAgentArgs.getObjectArray().length);
		assertTrue(asList(testAgentArgs.getObjectArray()).contains("Test agent"));
	}
}
