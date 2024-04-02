package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.preparePeriodicRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.TRIGGER_PERIOD;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_PERIODIC_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.PERIODIC_EXECUTE_ACTION_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.PERIODIC_SELECT_PERIOD_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.PERIODIC;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.CallForProposalRuleRest;
import org.jrba.rulesengine.rest.domain.PeriodicRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

class AgentPeriodicRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentPeriodicRule with controller.")
	void testInitializeAgentPeriodicRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default periodic rule", testRule.getName());
		assertEquals("default implementation of a rule that is being periodically evaluated",
				testRule.getDescription());
		assertEquals(DEFAULT_PERIODIC_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentPeriodicRule.SpecifyPeriodRule.class,
						AgentPeriodicRule.HandleActionTriggerRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentPeriodicRule with rule rest.")
	void testInitializeAgentPeriodicRuleWithRuleRest() {
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentPeriodicRule.SpecifyPeriodRule.class,
						AgentPeriodicRule.HandleActionTriggerRule.class);
		assertNotNull(testRule.expressionSpecifyPeriod);
		assertNotNull(testRule.expressionEvaluateBeforeTrigger);
		assertNotNull(testRule.expressionHandleActionTrigger);
	}

	@Test
	@DisplayName("Test copy of AgentPeriodicRule.")
	void testCopyOfAgentPeriodicRule() {
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentPeriodicRule<?, ?> testRuleCopy = (AgentPeriodicRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionSpecifyPeriod);
		assertNotNull(testRuleCopy.expressionEvaluateBeforeTrigger);
		assertNotNull(testRuleCopy.expressionHandleActionTrigger);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentPeriodicRule.SpecifyPeriodRule.class,
						AgentPeriodicRule.HandleActionTriggerRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);

		testRule.connectToController(testRulesController);

		verifyDefaultRulesControllerConnection(testRule, testRulesController);
		assertThatCollection(testRule.getRules()).allSatisfy((rule) -> {
			assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
			assertInstanceOf(TestAbstractAgentCustom.class, testRule.getAgent());
			assertInstanceOf(TestAgentPropsDefault.class, testRule.getAgentProps());
			assertInstanceOf(TestAgentNodeDefault.class, testRule.getAgentNode());
		});
	}

	@Test
	@DisplayName("Test get AgentPeriodicRule rule type.")
	void testGetAgentPeriodicRuleType() {
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);

		assertEquals(PERIODIC.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize SpecifyPeriodRule description.")
	void testInitializeSpecifyPeriodRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);
		final AgentPeriodicRule<?, ?>.SpecifyPeriodRule periodRule = selectSpecifyPeriodRule(testRule);

		final AgentRuleDescription description = periodRule.initializeRuleDescription();

		assertEquals(DEFAULT_PERIODIC_RULE, description.ruleType());
		assertEquals(PERIODIC_SELECT_PERIOD_STEP.getType(), description.stepType());
		assertEquals("default periodic rule - specify action period", description.ruleName());
		assertEquals("rule performed when behaviour period is to be selected", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleActionTriggerRule description.")
	void testInitializeHandleActionTriggerRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);
		final AgentPeriodicRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final AgentRuleDescription description = handlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_PERIODIC_RULE, description.ruleType());
		assertEquals(PERIODIC_EXECUTE_ACTION_STEP.getType(), description.stepType());
		assertEquals("default periodic rule - execute action", description.ruleName());
		assertEquals("rule that executes action after specified period of time has passed",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of SpecifyPeriodRule for rest initialization.")
	void testExecuteSpecifyPeriodRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentPeriodicRule<?, ?>.SpecifyPeriodRule periodRule = selectSpecifyPeriodRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		periodRule.executeRule(testFacts);

		final long result = testFacts.get(TRIGGER_PERIOD);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertEquals(10, result);
	}

	@Test
	@DisplayName("Test execution of SpecifyPeriodRule for controller initialization.")
	void testExecuteSpecifyPeriodRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);
		final AgentPeriodicRule<?, ?>.SpecifyPeriodRule periodRule = selectSpecifyPeriodRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		periodRule.executeRule(testFacts);

		final long result = testFacts.get(TRIGGER_PERIOD);

		assertNull(testRule.getInitialParameters());
		assertEquals(0, result);
	}

	@Test
	@DisplayName("Test execution of HandleActionTriggerRule for rest initialization.")
	void testExecuteHandleActionTriggerRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentPeriodicRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handlerRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleActionTrigger,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleActionTriggerRule for controller initialization.")
	void testExecuteHandleActionTriggerRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);
		final AgentPeriodicRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> handlerRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test evaluate of HandleActionTriggerRule for rest initialization.")
	void testEvaluateHandleActionTriggerRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final PeriodicRuleRest ruleRest = preparePeriodicRuleRest();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentPeriodicRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handlerRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleActionTriggerRule for controller initialization.")
	void testEvaluateHandleActionTriggerRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentPeriodicRule<?, ?> testRule = new AgentPeriodicRule<>(testRulesController);
		final AgentPeriodicRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handlerRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	private AgentPeriodicRule<?, ?>.SpecifyPeriodRule selectSpecifyPeriodRule(final AgentPeriodicRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentPeriodicRule.SpecifyPeriodRule.class))
				.findFirst()
				.map(AgentPeriodicRule.SpecifyPeriodRule.class::cast)
				.orElseThrow();
	}

	private AgentPeriodicRule<?, ?>.HandleActionTriggerRule selectHandleActionTriggerRule(
			final AgentPeriodicRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentPeriodicRule.HandleActionTriggerRule.class))
				.findFirst()
				.map(AgentPeriodicRule.HandleActionTriggerRule.class::cast)
				.orElseThrow();
	}
}
