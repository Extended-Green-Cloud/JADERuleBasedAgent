package org.jrba.rulesengine.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareDefaultRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRequestRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_TYPE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.BASIC_RULE;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum.BASIC;
import static org.jrba.rulesengine.mvel.MVELRuleMapper.getRuleForType;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mockStatic;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

class AgentBasicRuleUnitTest {

	private static Stream<Arguments> prepareParametersEvaluateRule() {
		final RuleSetFacts ruleSetFacts = new RuleSetFacts(0);
		ruleSetFacts.put(RULE_TYPE, "DEFAULT_RULE_TYPE");

		return Stream.of(arguments(ruleSetFacts, true), arguments(new RuleSetFacts(0), false));
	}

	private static Stream<Arguments> prepareParametersEvaluate() {
		final RuleSetFacts ruleSetFacts = new RuleSetFacts(0);
		ruleSetFacts.put(RULE_TYPE, BASIC_RULE);

		return Stream.of(arguments(ruleSetFacts, true), arguments(new RuleSetFacts(0), false));
	}

	@Test
	@DisplayName("Test initialization of AgentBasicRule from RuleRest.")
	void testInitializeAgentBasicRuleFromRuleRest() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRuleRest);
		verifyDefaultRuleRestFields(testRule);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialization of AgentBasicRule from RulesController.")
	void testInitializeAgentBasicRuleFromRuleController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRulesController);

		CommonRuleAssertions.verifyRuleForRulesControllerFields(testRule);
		assertEquals("basic agent rule", testRule.getName());
		assertEquals("default rule definition", testRule.getDescription());
		assertEquals(BASIC_RULE, testRule.ruleType);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialization of AgentBasicRule from RulesController with priority.")
	void testInitializeAgentBasicRuleFromRuleControllerAndPriority() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRulesController, 2);

		verifyRuleForRulesControllerFields(testRule, 2);
		assertEquals("basic agent rule", testRule.getName());
		assertEquals("default rule definition", testRule.getDescription());
		assertEquals("BASIC_RULE", testRule.ruleType);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialization of AgentBasicRule from RulesController being null.")
	void testInitializeAgentBasicRuleFromNullRuleController() {
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>((RulesController<?, ?>) null);

		assertEquals("basic agent rule", testRule.getName());
		assertEquals("default rule definition", testRule.getDescription());
		assertEquals(BASIC_RULE, testRule.ruleType);
		assertEquals(2147483646, testRule.getPriority());
		assertFalse(testRule.isRuleStep);
		assertNull(testRule.subRuleType);
		assertNull(testRule.agentType);
		assertNull(testRule.stepType);
		assertNull(testRule.initialParameters);
		assertNull(testRule.executeExpression);
		assertNull(testRule.evaluateExpression);
		assertNull(testRule.controller);
		assertNull(testRule.agent);
		assertNull(testRule.agentProps);
		assertNull(testRule.agentNode);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test copy of AgentBasicRule.")
	void testCopyOfAgentBasicRule() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRuleRest);
		testRule.connectToController(testRulesController);

		final AgentRule rule1 = getRuleForType(prepareBehaviourRuleRest(), null);
		final AgentRule rule2 = getRuleForType(prepareRequestRuleRest(), null);
		testRule.stepRules = List.of(rule1, rule2);

		final AgentBasicRule<?, ?> testRuleCopy = (AgentBasicRule<?, ?>) testRule.copy();

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
		assertEquals(testRulesController, testRuleCopy.controller);
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.agent);
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.agentProps);
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.agentNode);
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getStepRules())
				.hasSize(2)
				.allSatisfy((rule) -> {
					assertNotSame(rule, rule1);
					assertNotSame(rule, rule2);
				})
				.anySatisfy((rule) -> assertThat(rule).usingRecursiveComparison().isEqualTo(rule1))
				.anySatisfy((rule) -> assertThat(rule).usingRecursiveComparison().isEqualTo(rule2));
	}

	@Test
	@DisplayName("Test connectToController without initial parameters.")
	void testConnectToControllerWithoutInitialParameters() {
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>((RulesController<?, ?>) null);

		assertNull(testRule.controller);
		assertNull(testRule.agent);
		assertNull(testRule.agentProps);
		assertNull(testRule.agentNode);
		assertNull(testRule.initialParameters);

		testRule.connectToController(prepareRulesController());

		assertNull(testRule.initialParameters);
		assertEquals(AgentTypeEnum.BASIC.getName(), testRule.agentType);
		assertInstanceOf(TestAbstractAgentCustom.class, testRule.agent);
		assertInstanceOf(TestAgentPropsDefault.class, testRule.agentProps);
		assertInstanceOf(TestAgentNodeDefault.class, testRule.agentNode);
	}

	@Test
	@DisplayName("Test connectToController with initial parameters.")
	void testConnectToControllerWithInitialParameters() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final AgentBasicRule<TestAgentPropsDefault, TestAgentNodeDefault> testRule = new AgentBasicRule<>(testRuleRest);
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> testRulesController = prepareRulesController();

		assertNull(testRule.controller);
		assertNull(testRule.agent);
		assertNull(testRule.agentProps);
		assertNull(testRule.agentNode);
		assertNotNull(testRule.initialParameters);

		testRule.connectToController(testRulesController);
		verifyDefaultRulesControllerConnection(testRule, testRulesController);
	}

	@ParameterizedTest
	@MethodSource("prepareParametersEvaluateRule")
	@DisplayName("Test evaluate rule.")
	void testEvaluateRule(final RuleSetFacts facts, final boolean result) {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRuleRest);
		assertEquals(result, testRule.evaluateRule(facts));
	}

	@ParameterizedTest
	@MethodSource("prepareParametersEvaluate")
	@DisplayName("Test evaluate.")
	void testEvaluate(final RuleSetFacts facts, final boolean result) {
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>((RulesController<?, ?>) null);
		assertEquals(result, testRule.evaluate(facts));
	}

	@Test
	@DisplayName("Test evaluate with REST initialization.")
	void testEvaluateWithREST() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRuleRest);
		assertTrue(testRule.evaluate(null));
	}

	@Test
	@DisplayName("Test execute without initial parameters.")
	void testExecuteWithoutInitialParam() {
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>((RulesController<?, ?>) null);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> testRule.execute(null));
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execute with initial parameters.")
	void testExecuteWithInitialParam() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>(testRuleRest);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> testRule.execute(null));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.executeExpression, testRule.initialParameters));
		}
	}

	@Test
	@DisplayName("Test get AgentBasicRule rule type.")
	void testGetAgentBasicRuleRuleType() {
		final AgentBasicRule<?, ?> testRule = new AgentBasicRule<>((RulesController<?, ?>) null);
		assertEquals(BASIC.getType(), testRule.getAgentRuleType());
	}
}
