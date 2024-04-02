package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRequestRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.fixtures.TestRulesFixtures.prepareScheduledRuleRest;
import static org.jrba.rulesengine.constants.FactTypeConstants.TRIGGER_TIME;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_SCHEDULE_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.SCHEDULED_EXECUTE_ACTION_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.SCHEDULED_SELECT_TIME_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.SCHEDULED;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.RequestRuleRest;
import org.jrba.rulesengine.rest.domain.ScheduledRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

class AgentScheduleRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentScheduledRule with controller.")
	void testInitializeAgentScheduledRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default scheduled rule", testRule.getName());
		assertEquals("default implementation of a rule that is executed at a scheduled time",
				testRule.getDescription());
		assertEquals(DEFAULT_SCHEDULE_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentScheduledRule.SpecifyExecutionTimeRule.class,
						AgentScheduledRule.HandleActionTriggerRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentScheduledRule with rule rest.")
	void testInitializeAgentScheduledRuleWithRuleRest() {
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentScheduledRule.SpecifyExecutionTimeRule.class,
						AgentScheduledRule.HandleActionTriggerRule.class);
		assertNotNull(testRule.expressionEvaluateBeforeTrigger);
		assertNotNull(testRule.expressionSpecifyTime);
		assertNotNull(testRule.expressionHandleActionTrigger);
	}

	@Test
	@DisplayName("Test copy of AgentScheduledRule.")
	void testCopyOfAgentScheduledRule() {
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentScheduledRule<?, ?> testRuleCopy = (AgentScheduledRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionEvaluateBeforeTrigger);
		assertNotNull(testRuleCopy.expressionSpecifyTime);
		assertNotNull(testRuleCopy.expressionHandleActionTrigger);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentScheduledRule.SpecifyExecutionTimeRule.class,
						AgentScheduledRule.HandleActionTriggerRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);

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
	@DisplayName("Test get AgentScheduledRule rule type.")
	void testGetAgentScheduledRuleRuleType() {
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);

		assertEquals(SCHEDULED.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize SpecifyExecutionTimeRule description.")
	void testInitializeSpecifyExecutionTimeRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);
		final AgentScheduledRule<?, ?>.SpecifyExecutionTimeRule timeRule = selectSpecifyExecutionTimeRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_SCHEDULE_RULE, description.ruleType());
		assertEquals(SCHEDULED_SELECT_TIME_STEP.getType(), description.stepType());
		assertEquals("default scheduled rule - specify action execution time", description.ruleName());
		assertEquals("rule performed when behaviour execution time is to be selected", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleActionTriggerRule description.")
	void testInitializeHandleActionTriggerRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);
		final AgentScheduledRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final AgentRuleDescription description = handlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_SCHEDULE_RULE, description.ruleType());
		assertEquals(SCHEDULED_EXECUTE_ACTION_STEP.getType(), description.stepType());
		assertEquals("default scheduled rule - execute action", description.ruleName());
		assertEquals("rule that executes action at specific time", description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of SpecifyExecutionTimeRule for rest initialization.")
	void testExecuteSpecifyExecutionTimeRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentScheduledRule<?, ?>.SpecifyExecutionTimeRule timeRule = selectSpecifyExecutionTimeRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		timeRule.executeRule(testFacts);

		final Date result = testFacts.get(TRIGGER_TIME);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(result).isEqualTo(Instant.parse("2024-01-01T00:00:00.00Z"));
	}

	@Test
	@DisplayName("Test execution of SpecifyExecutionTimeRule for controller initialization.")
	void testExecuteSpecifyExecutionTimeRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);
		final AgentScheduledRule<?, ?>.SpecifyExecutionTimeRule timeRule = selectSpecifyExecutionTimeRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		timeRule.executeRule(testFacts);

		final Date result = testFacts.get(TRIGGER_TIME);

		assertNull(testRule.getInitialParameters());
		assertThat(result).isCloseTo(Instant.now(), 500);
	}

	@Test
	@DisplayName("Test evaluate of HandleActionTriggerRule for rest initialization.")
	void testEvaluateHandleActionTriggerRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentScheduledRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);
		final RuleSetFacts testFacts = new RuleSetFacts(0);

		final boolean result = handlerRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleActionTriggerRule for controller initialization.")
	void testEvaluateHandleActionTriggerRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);
		final AgentScheduledRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		final boolean result = handlerRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	@Test
	@DisplayName("Test execution of HandleActionTriggerRule for rest initialization.")
	void testExecuteHandleActionTriggerRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ScheduledRuleRest ruleRest = prepareScheduledRuleRest();
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentScheduledRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);
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
		final AgentScheduledRule<?, ?> testRule = new AgentScheduledRule<>(testRulesController);
		final AgentScheduledRule<?, ?>.HandleActionTriggerRule handlerRule = selectHandleActionTriggerRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handlerRule.executeRule(testFacts);

			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	private AgentScheduledRule<?, ?>.SpecifyExecutionTimeRule selectSpecifyExecutionTimeRule(
			final AgentScheduledRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentScheduledRule.SpecifyExecutionTimeRule.class))
				.findFirst()
				.map(AgentScheduledRule.SpecifyExecutionTimeRule.class::cast)
				.orElseThrow();
	}

	private AgentScheduledRule<?, ?>.HandleActionTriggerRule selectHandleActionTriggerRule(
			final AgentScheduledRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentScheduledRule.HandleActionTriggerRule.class))
				.findFirst()
				.map(AgentScheduledRule.HandleActionTriggerRule.class::cast)
				.orElseThrow();
	}
}
