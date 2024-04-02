package org.jrba.agentmodel.behaviour;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.List;

import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentBehaviourDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class ListenForControllerObjectsUnitTest {

	@Spy
	private TestAbstractAgentCustom testAgent;

	@Test
	@DisplayName("Test ListenForControllerObjects initialization without initializer methods.")
	void testListenForControllerObjectsWithout() {
		final ListenForControllerObjects testBehaviour = new ListenForControllerObjects(testAgent,
				List.of(new TestAgentBehaviourDefault(testAgent)), 0,
				testAgent.getCustomInitializer());

		testBehaviour.action();

		await().timeout(10, SECONDS)
				.untilAsserted(() -> assertEquals("CustomInitialization", testAgent.getLastExecutedBehaviour()));
		assertNull(testAgent.getAgentNode());
		assertNull(testAgent.getRulesController());
	}
}
