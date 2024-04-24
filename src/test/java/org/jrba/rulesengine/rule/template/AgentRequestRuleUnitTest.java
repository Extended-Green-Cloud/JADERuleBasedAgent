package org.jrba.rulesengine.rule.template;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRequestRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.REQUEST_CREATE_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.REQUEST_FAILURE_RESULTS_MESSAGES;
import static org.jrba.rulesengine.constants.FactTypeConstants.REQUEST_INFORM_RESULTS_MESSAGES;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FAILURE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FAILURE_RESULTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.INFORM;
import static org.jrba.rulesengine.constants.MVELParameterConstants.INFORM_RESULTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.REFUSE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_REQUEST_RULE;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_HANDLE_ALL_RESULTS_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_HANDLE_FAILURE_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_HANDLE_INFORM_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_HANDLE_REFUSE_STEP;
import static org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum.REQUEST;
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
import org.jrba.rulesengine.rest.domain.RequestRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.lang.acl.ACLMessage;

class AgentRequestRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentRequestRule with controller.")
	void testInitializeAgentRequestRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default request rule", testRule.getName());
		assertEquals("default implementation of a rule that handles each step of FIPA REQUEST protocol",
				testRule.getDescription());
		assertEquals(DEFAULT_REQUEST_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(5)
				.hasExactlyElementsOfTypes(AgentRequestRule.CreateRequestMessageRule.class,
						AgentRequestRule.HandleInformRule.class,
						AgentRequestRule.HandleRefuseRule.class,
						AgentRequestRule.HandleFailureRule.class,
						AgentRequestRule.HandleAllResponsesRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentRequestRule with rule rest.")
	void testInitializeAgentRequestRuleWithRuleRest() {
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(5)
				.hasExactlyElementsOfTypes(AgentRequestRule.CreateRequestMessageRule.class,
						AgentRequestRule.HandleInformRule.class,
						AgentRequestRule.HandleRefuseRule.class,
						AgentRequestRule.HandleFailureRule.class,
						AgentRequestRule.HandleAllResponsesRule.class);
		assertNotNull(testRule.expressionCreateRequestMessage);
		assertNotNull(testRule.expressionEvaluateBeforeForAll);
		assertNotNull(testRule.expressionHandleAllResults);
		assertNotNull(testRule.expressionHandleInform);
		assertNotNull(testRule.expressionHandleRefuse);
		assertNotNull(testRule.expressionHandleFailure);
	}

	@Test
	@DisplayName("Test copy of AgentRequestRule.")
	void testCopyOfAgentRequestRule() {
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?> testRuleCopy = (AgentRequestRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionCreateRequestMessage);
		assertNotNull(testRuleCopy.expressionEvaluateBeforeForAll);
		assertNotNull(testRuleCopy.expressionHandleAllResults);
		assertNotNull(testRuleCopy.expressionHandleInform);
		assertNotNull(testRuleCopy.expressionHandleRefuse);
		assertNotNull(testRuleCopy.expressionHandleFailure);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(5)
				.hasExactlyElementsOfTypes(AgentRequestRule.CreateRequestMessageRule.class,
						AgentRequestRule.HandleInformRule.class,
						AgentRequestRule.HandleRefuseRule.class,
						AgentRequestRule.HandleFailureRule.class,
						AgentRequestRule.HandleAllResponsesRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);

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
	@DisplayName("Test get AgentRequestRule rule type.")
	void testGetAgentRequestRuleRuleType() {
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);

		assertEquals(REQUEST.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize CreateRequestMessageRule description.")
	void testInitializeCreateRequestMessageRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.CreateRequestMessageRule timeRule = selectCreateRequestMessageRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_REQUEST_RULE, description.ruleType());
		assertEquals(REQUEST_CREATE_STEP.getType(), description.stepType());
		assertEquals("default request rule - create request message", description.ruleName());
		assertEquals("rule performed a when request message sent to other agents is to be created",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleInformRule description.")
	void testInitializeHandleInformRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleInformRule timeRule = selectHandleInformRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_REQUEST_RULE, description.ruleType());
		assertEquals(REQUEST_HANDLE_INFORM_STEP.getType(), description.stepType());
		assertEquals("default request rule - handle inform message", description.ruleName());
		assertEquals("rule that handles case when INFORM message is received", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleRefuseRule description.")
	void testInitializeHandleRefuseRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleRefuseRule timeRule = selectHandleRefuseRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_REQUEST_RULE, description.ruleType());
		assertEquals(REQUEST_HANDLE_REFUSE_STEP.getType(), description.stepType());
		assertEquals("default request rule - handle refuse message", description.ruleName());
		assertEquals("rule that handles case when REFUSE message is received", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleFailureRule description.")
	void testInitializeHandleFailureRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleFailureRule timeRule = selectHandleFailureRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_REQUEST_RULE, description.ruleType());
		assertEquals(REQUEST_HANDLE_FAILURE_STEP.getType(), description.stepType());
		assertEquals("default request rule - handle failure message", description.ruleName());
		assertEquals("rule that handles case when FAILURE message is received", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleAllResponsesRule description.")
	void testInitializeHandleAllResponsesRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleAllResponsesRule timeRule = selectHandleAllResponseRule(testRule);

		final AgentRuleDescription description = timeRule.initializeRuleDescription();

		assertEquals(DEFAULT_REQUEST_RULE, description.ruleType());
		assertEquals(REQUEST_HANDLE_ALL_RESULTS_STEP.getType(), description.stepType());
		assertEquals("default request rule - handle all messages", description.ruleName());
		assertEquals("rule that handles case when all INFORM and FAILURE messages are received",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of CreateRequestMessageRule for rest initialization.")
	void testExecuteCreateRequestMessageRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.CreateRequestMessageRule createRule = selectCreateRequestMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(REQUEST_CREATE_MESSAGE);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(result).satisfies(aclMessage -> {
			assertEquals("123", aclMessage.getContent());
			assertEquals(ACLMessage.REQUEST, aclMessage.getPerformative());
		});
	}

	@Test
	@DisplayName("Test execution of CreateRequestMessageRule for controller initialization.")
	void testExecuteCreateRequestMessageRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.CreateRequestMessageRule createRule = selectCreateRequestMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(REQUEST_CREATE_MESSAGE);

		assertNull(testRule.getInitialParameters());
		assertNull(result.getContent());
		assertEquals(ACLMessage.REQUEST, result.getPerformative());
	}

	@Test
	@DisplayName("Test execution of HandleInformRule for rest initialization.")
	void testExecuteHandleInformRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleInformRule handleInformRule = selectHandleInformRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handleInformRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(INFORM));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleInform,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleInformRule for controller initialization.")
	void testExecuteHandleInformRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleInformRule handleInformRule = selectHandleInformRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> handleInformRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleRefuseRule for rest initialization.")
	void testExecuteHandleRefuseRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleRefuseRule handleRefuseRule = selectHandleRefuseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handleRefuseRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(REFUSE));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleRefuse,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleRefuseRule for controller initialization.")
	void testExecuteHandleRefuseRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleRefuseRule handleRefuseRule = selectHandleRefuseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> handleRefuseRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleFailureRule for rest initialization.")
	void testExecuteHandleFailureRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleFailureRule handleFailureRule = selectHandleFailureRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handleFailureRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(FAILURE));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleFailure,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleFailureRule for controller initialization.")
	void testExecuteHandleFailureRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleFailureRule handleFailureRule = selectHandleFailureRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> handleFailureRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleAllResponsesRule for rest initialization.")
	void testExecuteHandleAllResponsesRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleAllResponsesRule handleAllRule = selectHandleAllResponseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(REQUEST_INFORM_RESULTS_MESSAGES, emptyList());
		testFacts.put(REQUEST_FAILURE_RESULTS_MESSAGES, emptyList());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handleAllRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(INFORM_RESULTS));
			assertFalse(testRule.getInitialParameters().containsKey(FAILURE_RESULTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleAllResults,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleAllResponsesRule for controller initialization.")
	void testExecuteHandleAllResponsesRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleAllResponsesRule handleAllRule = selectHandleAllResponseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(REQUEST_INFORM_RESULTS_MESSAGES, emptyList());
		testFacts.put(REQUEST_FAILURE_RESULTS_MESSAGES, emptyList());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> handleAllRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test evaluate of HandleInformRule for rest initialization.")
	void testEvaluateHandleInformRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleInformRule handleInformRule = selectHandleInformRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleInformRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleInformRule for controller initialization.")
	void testEvaluateHandleInformRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleInformRule handleInformRule = selectHandleInformRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleInformRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleRefuseRule for rest initialization.")
	void testEvaluateHandleRefuseRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleRefuseRule handleRefuseRule = selectHandleRefuseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleRefuseRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleRefuseRule for controller initialization.")
	void testEvaluateHandleRefuseRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleRefuseRule handleRefuseRule = selectHandleRefuseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleRefuseRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleFailureRule for rest initialization.")
	void testEvaluateHandleFailureRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleFailureRule handleFailureRule = selectHandleFailureRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleFailureRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleFailureRule for controller initialization.")
	void testEvaluateHandleFailureRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleFailureRule handleFailureRule = selectHandleFailureRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleFailureRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleAllResponsesRule for rest initialization.")
	void testEvaluateHandleAllResponsesRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RequestRuleRest ruleRest = prepareRequestRuleRest();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentRequestRule<?, ?>.HandleAllResponsesRule handleAllRule = selectHandleAllResponseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleAllRule.evaluateRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(result);
	}

	@Test
	@DisplayName("Test evaluate of HandleAllResponsesRule for controller initialization.")
	void testEvaluateHandleAllResponsesRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentRequestRule<?, ?> testRule = new AgentRequestRule<>(testRulesController);
		final AgentRequestRule<?, ?>.HandleAllResponsesRule handleAllRule = selectHandleAllResponseRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final boolean result = handleAllRule.evaluateRule(testFacts);

		assertNull(testRule.getInitialParameters());
		assertTrue(result);
	}

	private AgentRequestRule<?, ?>.CreateRequestMessageRule selectCreateRequestMessageRule(
			final AgentRequestRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentRequestRule.CreateRequestMessageRule.class))
				.findFirst()
				.map(AgentRequestRule.CreateRequestMessageRule.class::cast)
				.orElseThrow();
	}

	private AgentRequestRule<?, ?>.HandleInformRule selectHandleInformRule(final AgentRequestRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentRequestRule.HandleInformRule.class))
				.findFirst()
				.map(AgentRequestRule.HandleInformRule.class::cast)
				.orElseThrow();
	}

	private AgentRequestRule<?, ?>.HandleRefuseRule selectHandleRefuseRule(final AgentRequestRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentRequestRule.HandleRefuseRule.class))
				.findFirst()
				.map(AgentRequestRule.HandleRefuseRule.class::cast)
				.orElseThrow();
	}

	private AgentRequestRule<?, ?>.HandleFailureRule selectHandleFailureRule(final AgentRequestRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentRequestRule.HandleFailureRule.class))
				.findFirst()
				.map(AgentRequestRule.HandleFailureRule.class::cast)
				.orElseThrow();
	}

	private AgentRequestRule<?, ?>.HandleAllResponsesRule selectHandleAllResponseRule(
			final AgentRequestRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentRequestRule.HandleAllResponsesRule.class))
				.findFirst()
				.map(AgentRequestRule.HandleAllResponsesRule.class::cast)
				.orElseThrow();
	}
}
