package org.jrba.agentmodel.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.jrba.utils.rules.RuleSetConstructor.constructRuleSetWithName;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jrba.agentmodel.behaviour.ListenForControllerObjects;
import org.jrba.fixtures.TestAbstractAgentDefault;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.utils.rules.RuleSetConstructor;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.MockedStatic;

class AbstractAgentUnitTest {

	@Test
	@DisplayName("Test AbstractAgent initialization.")
	void testAbstractAgentInitialization() {
		final TestAbstractAgentDefault testAgent = new TestAbstractAgentDefault();

		assertEquals(0, testAgent.getQueueSize());
		assertNull(testAgent.getAgentNode());
		assertNull(testAgent.getProperties());
		assertNull(testAgent.getRulesController());
	}

	@Test
	@DisplayName("Test AbstractAgent set up.")
	void testAbstractAgentSetUp() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());

		testAgent.setup();
		verify(testAgent).getArguments();
		verify(testAgent).initializeAgent(null);
		verify(testAgent).validateAgentArguments();
		verify(testAgent).runStartingBehaviours();
	}

	@Test
	@DisplayName("Test AbstractAgent take down.")
	void testAbstractAgentTakeDown() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		assertDoesNotThrow(testAgent::takeDown);
	}

	@Test
	@DisplayName("Test AbstractAgent set up RulesController with null agent node.")
	void testAbstractAgentSetUpRulesControllerNullAgentNode() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController = spy(RulesController.class);
		testAgent.setProperties(new TestAgentPropsDefault("Test Agent"));
		doReturn("Test Agent").when(testAgent).getName();

		try (MockedStatic<RuleSetConstructor> controllerConstructor = mockStatic(RuleSetConstructor.class)) {
			final RuleSet testRuleSet = mock(RuleSet.class);
			controllerConstructor.when(() -> constructRuleSetWithName("DEFAULT_RULE_SET", rulesController))
					.thenReturn(testRuleSet);

			testAgent.setRulesController(rulesController);
			assertEquals("Test Agent", testAgent.getProperties().getAgentName());
			assertNull(testAgent.getProperties().getAgentNode());
			assertEquals(rulesController, testAgent.getRulesController());
			assertEquals(testAgent, testAgent.getRulesController().getAgent());
			assertEquals(testAgent.properties, testAgent.getRulesController().getAgentProps());
			assertEquals("DEFAULT_RULE_SET", testAgent.getRulesController().getBaseRuleSet());
			assertThat(testAgent.getRulesController().getRuleSets()).containsEntry(0, testRuleSet);
			assertNull(testAgent.getRulesController().getAgentNode());
		}

		verify(testAgent).runInitialBehavioursForRuleSet();
	}

	@Test
	@DisplayName("Test AbstractAgent set up RulesController with null agent properties.")
	void testAbstractAgentSetUpRulesControllerNullAgentProps() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		final TestAgentNodeDefault testAgentNode = spy(new TestAgentNodeDefault());
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController = spy(RulesController.class);
		testAgent.setAgentNode(testAgentNode);
		doReturn("Test Agent").when(testAgent).getName();

		try (MockedStatic<RuleSetConstructor> controllerConstructor = mockStatic(RuleSetConstructor.class)) {
			final RuleSet testRuleSet = mock(RuleSet.class);
			controllerConstructor.when(() -> constructRuleSetWithName("DEFAULT_RULE_SET", rulesController))
					.thenReturn(testRuleSet);

			testAgent.setRulesController(rulesController);
			assertNull(testAgent.getProperties());
			assertEquals(testAgentNode, testAgent.getAgentNode());
			assertEquals(testAgentNode, testAgent.getRulesController().getAgentNode());
			assertEquals(rulesController, testAgent.getRulesController());
			assertEquals(testAgent, testAgent.getRulesController().getAgent());
			assertEquals("DEFAULT_RULE_SET", testAgent.getRulesController().getBaseRuleSet());
			assertThat(testAgent.getRulesController().getRuleSets()).containsEntry(0, testRuleSet);
			assertNull(testAgent.getRulesController().getAgentProps());
		}
		verify(testAgent).runInitialBehavioursForRuleSet();
	}

	@Test
	@DisplayName("Test AbstractAgent set up RulesController.")
	void testAbstractAgentSetUpRulesController() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		final TestAgentNodeDefault testAgentNode = spy(new TestAgentNodeDefault());
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController = spy(RulesController.class);
		testAgent.setProperties(new TestAgentPropsDefault("Test Agent"));
		testAgent.setAgentNode(testAgentNode);
		doReturn("Test Agent").when(testAgent).getName();

		try (MockedStatic<RuleSetConstructor> controllerConstructor = mockStatic(RuleSetConstructor.class)) {
			final RuleSet testRuleSet = mock(RuleSet.class);
			controllerConstructor.when(() -> constructRuleSetWithName("DEFAULT_RULE_SET", rulesController))
					.thenReturn(testRuleSet);

			testAgent.setRulesController(rulesController);
			assertEquals("Test Agent", testAgent.getProperties().getAgentName());
			assertEquals(testAgentNode, testAgent.getAgentNode());
			assertEquals(testAgentNode, testAgent.getProperties().getAgentNode());
			assertEquals(testAgentNode, testAgent.getRulesController().getAgentNode());
			assertEquals(rulesController, testAgent.getRulesController());
			assertEquals(testAgent, testAgent.getRulesController().getAgent());
			assertEquals(testAgent.properties, testAgent.getRulesController().getAgentProps());
			assertEquals("DEFAULT_RULE_SET", testAgent.getRulesController().getBaseRuleSet());
			assertThat(testAgent.getRulesController().getRuleSets()).containsEntry(0, testRuleSet);
		}

		verify(testAgent).runInitialBehavioursForRuleSet();
	}

	@Test
	@DisplayName("Test fire on facts without RulesControllerSet.")
	void testFireOnFactsNoRulesController() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		assertDoesNotThrow(() -> testAgent.fireOnFacts(null));
	}

	@Test
	@DisplayName("Test fire on facts.")
	void testFireOnFacts() {
		getAvailableRuleSets().clear();
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController = spy(RulesController.class);
		doReturn("Test Agent").when(testAgent).getName();
		doNothing().when(rulesController).fire(any());
		testAgent.setRulesController(rulesController);

		final RuleSetFacts mockFacts = new RuleSetFacts(0);

		testAgent.fireOnFacts(mockFacts);
		verify(rulesController).fire(mockFacts);
	}

	@Test
	@DisplayName("Test prepare starting behaviour.")
	void testPrepareStartingBehaviour() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		doReturn("Test Agent").when(testAgent).getName();
		assertTrue(testAgent.prepareStartingBehaviours().isEmpty());
	}

	@Test
	@DisplayName("Test run starting behaviour.")
	void testRunStartingBehaviour() {
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());

		testAgent.runStartingBehaviours();
		verify(testAgent).addBehaviour(argThat(ListenForControllerObjects.class::isInstance));
	}

	@Test
	@DisplayName("Test run initial behaviour.")
	void testRunInitialBehaviour() {
		getAvailableRuleSets().clear();
		final TestAbstractAgentDefault testAgent = spy(new TestAbstractAgentDefault());
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController =
				spy(new RulesController<>());
		doReturn("Test Agent").when(testAgent).getName();
		testAgent.setRulesController(rulesController);

		final ArgumentMatcher<RuleSetFacts> matchFacts = (facts) ->
				facts.asMap().containsKey("rule-type") && facts.asMap().containsValue("INITIALIZE_BEHAVIOURS_RULE");

		testAgent.runInitialBehavioursForRuleSet();
		verify(rulesController, times(2)).fire(argThat(matchFacts));
	}
}
