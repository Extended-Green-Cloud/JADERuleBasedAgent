package org.jrba.rulesengine.rule.combined;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCombinedRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_COMBINED_RULE;
import static org.jrba.rulesengine.enums.rulecombinationtype.AgentCombinedRuleTypeEnum.EXECUTE_ALL;
import static org.jrba.rulesengine.enums.rulecombinationtype.AgentCombinedRuleTypeEnum.EXECUTE_FIRST;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.BASIC;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.COMBINED;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum;
import org.jrba.rulesengine.rest.domain.BehaviourRuleRest;
import org.jrba.rulesengine.rest.domain.CombinedRuleRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRule;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AgentCombinedRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentCombinedRule with controller.")
	void testInitializeAgentCombinedRuleFromRuleController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(testRulesController, EXECUTE_FIRST);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default combination rule", testRule.getName());
		assertEquals("default implementation of a rule that consists of multiple nested rules",
				testRule.getDescription());
		assertEquals(DEFAULT_COMBINED_RULE, testRule.getRuleType());
		assertEquals(EXECUTE_FIRST.getType(), testRule.getCombinationType());
		assertTrue(testRule.getRulesToCombine().isEmpty());
		assertNull(testRule.getRuleSet());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentCombinedRule with controller and priority.")
	void testInitializeAgentCombinedRuleFromRuleControllerAndPriority() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(testRulesController, EXECUTE_FIRST, 2);

		verifyRuleForRulesControllerFields(testRule, 2);
		assertEquals("default combination rule", testRule.getName());
		assertEquals("default implementation of a rule that consists of multiple nested rules",
				testRule.getDescription());
		assertEquals(DEFAULT_COMBINED_RULE, testRule.getRuleType());
		assertEquals(EXECUTE_FIRST.getType(), testRule.getCombinationType());
		assertTrue(testRule.getRulesToCombine().isEmpty());
		assertNull(testRule.getRuleSet());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentCombinedRule with controller and rule set.")
	void testInitializeAgentCombinedRuleFromRuleControllerAndRuleSet() {
		final RuleSet ruleSet = prepareRuleSet();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(testRulesController, ruleSet, EXECUTE_FIRST);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default combination rule", testRule.getName());
		assertEquals("default implementation of a rule that consists of multiple nested rules",
				testRule.getDescription());
		assertEquals(DEFAULT_COMBINED_RULE, testRule.getRuleType());
		assertEquals(EXECUTE_FIRST.getType(), testRule.getCombinationType());
		assertEquals(ruleSet, testRule.getRuleSet());
		assertTrue(testRule.getRulesToCombine().isEmpty());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentCombinedRule with rule rest.")
	void testInitializeAgentCombinedRuleFromRuleRest() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);

		verifyDefaultRuleRestFields(testRule);
		assertEquals(ruleSet, testRule.getRuleSet());
		assertEquals(EXECUTE_FIRST.getType(), testRule.getCombinationType());
		assertThat(testRule.rulesToCombine)
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentBehaviourRule.class, AgentBasicRule.class);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test copy of AgentCombinedRule.")
	void testCopyOfAgentCombinedRule() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentCombinedRule<?, ?> testRuleCopy = (AgentCombinedRule<?, ?>) testRule.copy();

		assertTrue(testRuleCopy.isRuleStep());
		assertEquals("DEFAULT_NAME", testRuleCopy.getName());
		assertEquals("Example description.", testRuleCopy.getDescription());
		assertEquals("DEFAULT_RULE_TYPE", testRuleCopy.getRuleType());
		assertEquals("DEFAULT_RULE_SUB_TYPE", testRuleCopy.getSubRuleType());
		assertEquals(REQUEST_CREATE_STEP.getType(), testRuleCopy.getStepType());
		assertThat(testRuleCopy.getInitialParameters()).containsEntry("exampleMap", new HashMap<>());
		assertEquals(1, testRuleCopy.getPriority());
		assertEquals(AgentTypeEnum.BASIC.getName(), testRuleCopy.getAgentType());
		assertNotNull(testRuleCopy.getExecuteExpression());
		assertNotNull(testRuleCopy.getEvaluateExpression());
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertNull(testRuleCopy.getStepRules());
		assertThat(testRuleCopy.getRuleSet())
				.usingRecursiveComparison()
				.isEqualTo(ruleSet);
		assertEquals(EXECUTE_FIRST.getType(), testRuleCopy.getCombinationType());
		assertThat(testRuleCopy.rulesToCombine)
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentBehaviourRule.class, AgentBasicRule.class);
		assertNull(testRuleCopy.getStepRules());
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);

		testRule.connectToController(testRulesController);

		verifyDefaultRulesControllerConnection(testRule, testRulesController);
		assertThatCollection(testRule.getRulesToCombine()).allSatisfy((rule) ->
				verifyDefaultRulesControllerConnection((AgentBasicRule<?, ?>) rule, testRulesController));

	}

	@Test
	@DisplayName("Test get AgentCombinedRule rule type.")
	void testGetAgentCombinedRuleRuleType() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);

		assertEquals(COMBINED.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test getting rules of EXECUTE_FIRST.")
	void testGetRulesExecuteFirst() {
		final RuleSet ruleSet = prepareRuleSet();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule =
				spy(new AgentCombinedRule<>(testRulesController, ruleSet, EXECUTE_FIRST));

		final List<AgentRule> resultRules = testRule.getRules();

		assertThat(resultRules).hasSize(1);
		assertThat(resultRules.getFirst()).isInstanceOfSatisfying(AgentCombinedRule.AgentExecuteFirstCombinedRule.class,
				(rule) -> {
					assertEquals(DEFAULT_COMBINED_RULE, rule.getRuleType());
					assertEquals(BASIC.getType(), rule.getAgentRuleType());
					assertEquals(testRule.initializeRuleDescription(), rule.initializeRuleDescription());
					assertNull(rule.getSubRuleType());
					assertEquals("default combination rule", rule.getName());
					assertEquals("default implementation of a rule that consists of multiple nested rules",
							rule.getDescription());
					assertEquals(AgentRuleTypeEnum.BASIC.name(), rule.getAgentType());
					assertFalse(rule.isRuleStep());
					assertNull(rule.getStepType());
				});

		final AgentCombinedRule<?, ?>.AgentExecuteFirstCombinedRule agentExecuteFirstCombinedRule =
				(AgentCombinedRule<?, ?>.AgentExecuteFirstCombinedRule) resultRules.getFirst();
		final RuleSetFacts facts = new RuleSetFacts(0);

		agentExecuteFirstCombinedRule.evaluateRule(facts);
		verify(testRule, times(2)).evaluateRule(facts);
		verify(testRule).executeRule(facts);
	}

	@Test
	@DisplayName("Test getting rules of EXECUTE_ALL.")
	void testGetRulesExecuteAll() {
		final RuleSet ruleSet = prepareRuleSet();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule =
				spy(new AgentCombinedRule<>(testRulesController, ruleSet, EXECUTE_ALL));

		final List<AgentRule> resultRules = testRule.getRules();

		assertThat(resultRules).hasSize(1);
		assertThat(resultRules.getFirst()).isInstanceOfSatisfying(AgentCombinedRule.AgentExecuteAllCombinedRule.class,
				(rule) -> {
					assertEquals(DEFAULT_COMBINED_RULE, rule.getRuleType());
					assertEquals(BASIC.getType(), rule.getAgentRuleType());
					assertEquals(testRule.initializeRuleDescription(), rule.initializeRuleDescription());
					assertNull(rule.getSubRuleType());
					assertEquals("default combination rule", rule.getName());
					assertEquals("default implementation of a rule that consists of multiple nested rules",
							rule.getDescription());
					assertEquals(AgentRuleTypeEnum.BASIC.name(), rule.getAgentType());
					assertFalse(rule.isRuleStep());
					assertNull(rule.getStepType());
				});

		final AgentCombinedRule<?, ?>.AgentExecuteAllCombinedRule agentExecuteFirstCombinedRule =
				(AgentCombinedRule<?, ?>.AgentExecuteAllCombinedRule) resultRules.getFirst();
		final RuleSetFacts facts = new RuleSetFacts(0);

		agentExecuteFirstCombinedRule.evaluateRule(facts);
		verify(testRule, times(3)).evaluateRule(facts);
		verify(testRule).executeRule(facts);
	}

	@Test
	@DisplayName("Test getting rules of other combined rule type.")
	void testGetRulesOther() {
		final RuleSet ruleSet = prepareRuleSet();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(testRulesController, ruleSet,
				() -> "TEST_NAME");

		assertTrue(testRule.getRules().isEmpty());
	}

	@Test
	@DisplayName("Test getting nested rules.")
	void testGetNestedRules() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);

		final List<String> result = testRule.getNestedRules();

		assertThat(result)
				.hasSize(2)
				.containsAll(List.of("DEFAULT_RULE_SUB_TYPE", "DEFAULT_RULE_SUB_TYPE"));
	}

	@Test
	@DisplayName("Test getting nested rules.")
	void testDefaultEvaluateRule() {
		final RuleSet ruleSet = prepareRuleSet();
		final CombinedRuleRest combinedRuleRest = prepareCombinedRuleRest();
		final AgentCombinedRule<?, ?> testRule = new AgentCombinedRule<>(combinedRuleRest, ruleSet);

		assertTrue(testRule.evaluateRule(new RuleSetFacts(0)));
	}
}
