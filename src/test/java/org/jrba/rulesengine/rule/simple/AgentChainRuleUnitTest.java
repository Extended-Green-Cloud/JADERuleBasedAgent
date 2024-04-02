package org.jrba.rulesengine.rule.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareDefaultRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSetRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_CHAIN_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.CHAIN;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.CallForProposalRuleRest;
import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.rule.CommonRuleAssertions;
import org.jrba.rulesengine.rule.template.AgentCFPRule;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mvel2.MVEL;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class AgentChainRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentChainRule with controller and rule set.")
	void testInitializeAgentChainRuleFromRuleControllerAndRuleSet() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRulesController, ruleSet);

		CommonRuleAssertions.verifyRuleForRulesControllerFields(testRule);
		assertEquals("default chain rule", testRule.getName());
		assertEquals("default implementation of a rule that iteratively performs rules evaluation",
				testRule.getDescription());
		assertEquals(DEFAULT_CHAIN_RULE, testRule.getRuleType());
		assertEquals(ruleSet, testRule.getRuleSet());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentChainRule with controller and rule set and priority.")
	void testInitializeAgentChainRuleFromRuleControllerAndRuleSetAndPriority() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRulesController, 2, ruleSet);

		verifyRuleForRulesControllerFields(testRule, 2);
		assertEquals("default chain rule", testRule.getName());
		assertEquals("default implementation of a rule that iteratively performs rules evaluation",
				testRule.getDescription());
		assertEquals(DEFAULT_CHAIN_RULE, testRule.getRuleType());
		assertEquals(ruleSet, testRule.getRuleSet());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentChainRule with RuleRest.")
	void testInitializeAgentChainRuleFromRuleRest() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRuleRest, ruleSet);

		verifyDefaultRuleRestFields(testRule);
		assertEquals(ruleSet, testRule.getRuleSet());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test copy of AgentChainRule.")
	void testCopyOfAgentChainRule() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRuleRest, ruleSet);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentChainRule<?, ?> testRuleCopy = (AgentChainRule<?, ?>) testRule.copy();

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
		assertThat(testRuleCopy.getRuleSet())
				.usingRecursiveComparison()
				.isEqualTo(ruleSet);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertNull(testRuleCopy.getStepRules());
	}

	@Test
	@DisplayName("Test get AgentChainRule rule type.")
	void testGetAgentChainRuleRuleType() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<TestAgentPropsDefault, TestAgentNodeDefault> testRule =
				new AgentChainRule<>(testRuleRest, ruleSet);

		assertEquals(CHAIN.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test execute without initial parameters.")
	void testExecuteWithoutInitialParam() {
		final RulesController<?, ?> testRulesController = spy(prepareRulesController());
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRulesController, ruleSet);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			final RuleSetFacts testFacts = new RuleSetFacts(0);

			assertDoesNotThrow(() -> testRule.execute(testFacts));
			mvlMock.verifyNoInteractions();
			verify(testRulesController).fire(testFacts);
		}
	}

	@Test
	@DisplayName("Test execute with initial parameters.")
	void testExecuteWithInitialParam() {
		final RuleRest testRuleRest = prepareDefaultRuleRest();
		final RulesController<?, ?> testRulesController = spy(prepareRulesController());
		final RuleSet ruleSet = new RuleSet(prepareRuleSetRest());
		final AgentChainRule<?, ?> testRule = new AgentChainRule<>(testRuleRest, ruleSet);
		testRule.connectToController(testRulesController);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			final RuleSetFacts testFacts = new RuleSetFacts(0);

			assertDoesNotThrow(() -> testRule.execute(testFacts));
			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(
					() -> MVEL.executeExpression(testRule.getExecuteExpression(), testRule.getInitialParameters()));
			verify(testRulesController).fire(testFacts);
		}
	}
}
