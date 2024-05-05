package org.jrba.rulesengine.ruleset;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSetRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSetWithDifferentTypes;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_STEP;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_TYPE;
import static org.jrba.rulesengine.mvel.MVELRuleMapper.getRuleForType;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.CFP_CREATE_STEP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Consumer;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRule;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RuleSetUnitTest {

	@Test
	@DisplayName("Test initialize RuleSet from rule set rest.")
	void testInitializeRuleSetFromRest() {
		final RuleSetRest ruleSetRest = prepareRuleSetRest();
		final RuleSet testRuleSet = new RuleSet(ruleSetRest);

		assertNull(testRuleSet.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, testRuleSet.getRulesEngine());
		assertEquals("TestRuleSet", testRuleSet.getName());
		assertThatCollection(testRuleSet.getAgentRules())
				.hasSize(2)
				.hasOnlyElementsOfTypes(AgentBehaviourRule.class, AgentBasicRule.class);
		assertFalse(testRuleSet.isCallInitializeRules());
	}

	@Test
	@DisplayName("Test initialize RuleSet from rule set.")
	void testInitializeRuleSetFromRuleSet() {
		final RuleSet ruleSetRest = prepareRuleSet();
		ruleSetRest.rulesController = prepareRulesController();
		final RuleSet testRuleSet = new RuleSet(ruleSetRest);

		assertEquals(ruleSetRest.getRulesController(), testRuleSet.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, testRuleSet.getRulesEngine());
		assertEquals("TestRuleSet", testRuleSet.getName());
		assertThat(testRuleSet.getAgentRules()).containsAll(ruleSetRest.getAgentRules());
		assertFalse(testRuleSet.isCallInitializeRules());
	}

	@Test
	@DisplayName("Test initialize RuleSet from name.")
	void testInitializeRuleSetFromName() {
		final RuleSet testRuleSet = new RuleSet("TestSet");

		assertNull(testRuleSet.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, testRuleSet.getRulesEngine());
		assertEquals("TestSet", testRuleSet.getName());
		assertThatCollection(testRuleSet.getAgentRules()).isEmpty();
		assertTrue(testRuleSet.isCallInitializeRules());
	}

	@Test
	@DisplayName("Test initialize RuleSet from rule set and controller without rules initialization.")
	void testInitializeRuleSetFromRuleSetAndControllerNoInitialization() {
		final RuleSet ruleSetRest = prepareRuleSet();
		final RulesController<?, ?> rulesController = prepareRulesController();
		final RuleSet testRuleSet = new RuleSet(ruleSetRest, rulesController);

		assertEquals(rulesController, testRuleSet.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, testRuleSet.getRulesEngine());
		assertEquals("TestRuleSet", testRuleSet.getName());
		assertThat(testRuleSet.getAgentRules()).containsAll(ruleSetRest.getAgentRules());
		assertFalse(testRuleSet.isCallInitializeRules());
		assertThatCollection(testRuleSet.getAgentRules())
				.allMatch((rule) -> ((AgentBasicRule<?, ?>) rule).getController().equals(rulesController));
	}

	@Test
	@DisplayName("Test initialize RuleSet from rule set and controller with rules initialization.")
	void testInitializeRuleSetFromRuleSetAndControllerWithInitialization() {
		final RuleSet ruleSetRest = spy(new RuleSet("TestSet"));
		final RulesController<?, ?> rulesController = prepareRulesController();
		final List<AgentRule> testRules = List.of(getRuleForType(prepareCallForProposalRuleRest(), null));
		when(ruleSetRest.initializeRules(rulesController)).thenReturn(testRules);

		RuleSet testRuleSet = new RuleSet(ruleSetRest, rulesController);

		assertEquals(rulesController, testRuleSet.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, testRuleSet.getRulesEngine());
		assertEquals("TestSet", testRuleSet.getName());
		assertThat(testRuleSet.getAgentRules()).containsAll(testRules);

		when(ruleSetRest.initializeRules(rulesController)).thenCallRealMethod();
		testRuleSet = new RuleSet(ruleSetRest, rulesController);
		assertThat(testRuleSet.getAgentRules()).isEmpty();
	}

	@Test
	@DisplayName("Test fir RuleSet.")
	void testFireRuleSet() {
		final RuleSet ruleSetRest = prepareRuleSetWithDifferentTypes();
		final RulesController<?, ?> rulesController = prepareRulesController();
		final RuleSet testRuleSet = new RuleSet(ruleSetRest, rulesController);
		final RulesEngine testEngine = mock(DefaultRulesEngine.class);
		testRuleSet.rulesEngine = testEngine;

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(RULE_TYPE, "TEST_TYPE_1");
		testFacts.put(RULE_STEP, CFP_CREATE_STEP.getType());

		testRuleSet.fireRuleSet(testFacts);

		final Consumer<Rules> matchRules = (rules) ->
				assertThatCollection(stream(rules.spliterator(), false).toList())
						.hasSize(3)
						.anySatisfy((rule) -> assertInstanceOf(AgentBehaviourRule.class, rule))
						.anySatisfy(
								(rule) -> assertEquals(CFP_CREATE_STEP.getType(), ((AgentRule) rule).getStepType()));

		verify(testEngine).fire(assertArg(matchRules), eq(testFacts));
	}
}
