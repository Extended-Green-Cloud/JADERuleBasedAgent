package org.jrba.rulesengine;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.mockStatic;

import java.util.concurrent.ConcurrentHashMap;

import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.rules.RuleSetConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RulesControllerUnitTest {

	@Test
	@DisplayName("Test initialize RulesController.")
	void testInitializeRulesController() {
		final RulesController<?, ?> testController = new RulesController<>();

		assertEquals(0, testController.getLatestLongTermRuleSetIdx().get());
		assertEquals(0, testController.getLatestRuleSetIdx().get());
		assertTrue(testController.getRuleSets().isEmpty());
		assertNull(testController.getAgent());
		assertNull(testController.getAgentNode());
		assertNull(testController.getAgentProps());
		assertNull(testController.getBaseRuleSet());
	}

	@Test
	@DisplayName("Test set agent.")
	void testSetAgent() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> testController = new RulesController<>();
		final TestAbstractAgentCustom testAgent = new TestAbstractAgentCustom();
		final TestAgentNodeDefault testNode = new TestAgentNodeDefault();
		final TestAgentPropsDefault testProps = new TestAgentPropsDefault("Test name");

		testController.setAgent(testAgent, testProps, testNode, DEFAULT_RULE_SET);

		assertEquals(testAgent, testController.getAgent());
		assertEquals(testNode, testController.getAgentNode());
		assertEquals(testProps, testController.getAgentProps());
		assertEquals(DEFAULT_RULE_SET, testController.getBaseRuleSet());
		assertThat(testController.getRuleSets()).containsKey(0);
		assertThat(testController.getRuleSets().get(0))
				.usingRecursiveComparison()
				.ignoringFields("rulesController")
				.isEqualTo(ruleSet);
	}

	@Test
	@DisplayName("Test fire rule facts when rule set cannot be found.")
	void testFireWhenRuleSetNotFound() {
		final RulesController<?, ?> testController = new RulesController<>();
		final RuleSetFacts testFacts = new RuleSetFacts(0);

		assertTrue(testController.getRuleSets().isEmpty());
		assertDoesNotThrow(() -> testController.fire(testFacts));
	}

	@Test
	@DisplayName("Test fire rule facts when rule set found.")
	void testFireWhenRuleSetFound() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RulesController<?, ?> testController = prepareRulesController();
		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testController.fire(testFacts);

		assertThat(testController.getRuleSets()).hasSize(1);
		assertDoesNotThrow(() -> testController.fire(testFacts));
	}

	@Test
	@DisplayName("Test modified rule set.")
	void testAddModifiedRuleSet() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RuleSet modifiedRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("MODIFIED_RULE_SET", modifiedRuleSet);

		final RulesController<?, ?> testController = prepareRulesController();

		try (MockedStatic<RuleSetConstructor> constructorController = mockStatic(RuleSetConstructor.class)) {
			constructorController.when(() -> RuleSetConstructor.modifyBaseRuleSetWithName(DEFAULT_RULE_SET,
					"MODIFIED_RULE_SET", testController)).thenCallRealMethod();
			constructorController.when(() -> RuleSetConstructor.getRuleSetTemplate(any(), any()))
					.thenCallRealMethod();
			constructorController.when(() -> RuleSetConstructor.modifyRuleSetForName(any(), any()))
					.thenCallRealMethod();

			assertThat(testController.getRuleSets()).hasSize(1);
			testController.addModifiedRuleSet("MODIFIED_RULE_SET", 1);

			constructorController.verify(() -> RuleSetConstructor.modifyBaseRuleSetWithName(DEFAULT_RULE_SET,
					"MODIFIED_RULE_SET", testController));
			assertThat(testController.getRuleSets()).hasSize(2).containsKey(1);
			assertEquals(1, testController.getLatestLongTermRuleSetIdx().get());
			assertEquals(1, testController.getLatestRuleSetIdx().get());
		}
	}

	@Test
	@DisplayName("Test temporary modification of rule set.")
	void testTemporaryModificationOfRuleSet() {
		final RuleSet baseRuleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, baseRuleSet);

		final RuleSet modifiedRuleSet = prepareRuleSet();
		final RulesController<?, ?> testController = prepareRulesController();

		try (MockedStatic<RuleSetConstructor> constructorController = mockStatic(RuleSetConstructor.class)) {
			constructorController.when(() -> RuleSetConstructor.modifyRuleSetForName(any(), any()))
					.thenCallRealMethod();

			assertThat(testController.getRuleSets()).hasSize(1);
			testController.addModifiedTemporaryRuleSetFromCurrent(modifiedRuleSet, 1);

			constructorController.verify(() -> RuleSetConstructor.modifyRuleSetForName(
					assertArg((ruleSet) -> assertThat(ruleSet).usingRecursiveComparison()
							.ignoringFields("rulesController")
							.isEqualTo(baseRuleSet)),
					assertArg((ruleSet) -> assertThat(ruleSet).usingRecursiveComparison()
							.ignoringFields("rulesController")
							.isEqualTo(modifiedRuleSet))));
			assertThat(testController.getRuleSets()).hasSize(2).containsKeys(0, 1);
			assertEquals(0, testController.getLatestLongTermRuleSetIdx().get());
			assertEquals(1, testController.getLatestRuleSetIdx().get());
		}
	}

	@Test
	@DisplayName("Test add new rule set.")
	void testAddNewRuleSet() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RuleSet modifiedRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("MODIFIED_RULE_SET", modifiedRuleSet);

		final RulesController<?, ?> testController = prepareRulesController();

		try (MockedStatic<RuleSetConstructor> constructorController = mockStatic(RuleSetConstructor.class)) {
			constructorController.when(() -> RuleSetConstructor.getRuleSetTemplate(any(), any())).thenCallRealMethod();
			constructorController.when(() -> RuleSetConstructor.constructRuleSetWithName(any(), any()))
					.thenCallRealMethod();

			assertThat(testController.getRuleSets()).hasSize(1);
			testController.addNewRuleSet("MODIFIED_RULE_SET", 1);

			constructorController.verify(() ->
					RuleSetConstructor.constructRuleSetWithName("MODIFIED_RULE_SET", testController));
			assertThat(testController.getRuleSets()).hasSize(2).containsKeys(0, 1);
			assertEquals(1, testController.getLatestLongTermRuleSetIdx().get());
			assertEquals(1, testController.getLatestRuleSetIdx().get());
		}
	}

	@Test
	@DisplayName("Test remove rule set (index not matching).")
	void testRemoveRuleSetWhenIndexDontMatch() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RulesController<?, ?> testController = prepareRulesController();

		assertFalse(testController.removeRuleSet(new ConcurrentHashMap<>(), 0));
	}

	@Test
	@DisplayName("Test remove rule set (processes undergoing).")
	void testRemoveRuleSetWhenProcessesUndergoing() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RuleSet modifiedRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("MODIFIED_RULE_SET", modifiedRuleSet);

		final RulesController<?, ?> testController = prepareRulesController();
		testController.addNewRuleSet("MODIFIED_RULE_SET", 1);

		final ConcurrentHashMap<String, Integer> testProcessMap = new ConcurrentHashMap<>();
		testProcessMap.put("PROCESS_1_ID", 0);

		assertFalse(testController.removeRuleSet(testProcessMap, 0));
	}

	@Test
	@DisplayName("Test remove rule set.")
	void testRemoveRuleSet() {
		final RuleSet ruleSet = prepareRuleSet();
		getAvailableRuleSets().clear();
		getAvailableRuleSets().put(DEFAULT_RULE_SET, ruleSet);

		final RuleSet modifiedRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("MODIFIED_RULE_SET", modifiedRuleSet);

		final RulesController<?, ?> testController = prepareRulesController();
		testController.addNewRuleSet("MODIFIED_RULE_SET", 1);

		final ConcurrentHashMap<String, Integer> testProcessMap = new ConcurrentHashMap<>();
		testProcessMap.put("PROCESS_1_ID", 1);

		assertThat(testController.getRuleSets()).containsKey(0);
		assertTrue(testController.removeRuleSet(testProcessMap, 0));
		assertThat(testController.getRuleSets()).doesNotContainKey(0);
	}
}
