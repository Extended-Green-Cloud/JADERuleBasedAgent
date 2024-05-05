package org.jrba.rulesengine.rule.template;

import static jade.lang.acl.ACLMessage.CFP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.fixtures.TestRulesFixtures.prepareSingleMessageRuleRest;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGE_EXPIRATION;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGE_TEMPLATE;
import static org.jrba.rulesengine.constants.FactTypeConstants.RECEIVED_MESSAGE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.MESSAGE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_SINGLE_MESSAGE_LISTENER_RULE;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.SINGLE_MESSAGE_READER_CREATE_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.SINGLE_MESSAGE_READER_HANDLE_MESSAGE_STEP;
import static org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum.LISTENER_SINGLE;
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

import java.util.HashMap;
import java.util.Optional;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.SingleMessageListenerRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

class AgentSingleMessageListenerRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentSingleMessageListenerRule with controller.")
	void testInitializeAgentSingleMessageListenerRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default single-time message listener rule", testRule.getName());
		assertEquals("default implementation of a rule that listens for a single message matching a given template",
				testRule.getDescription());
		assertEquals(DEFAULT_SINGLE_MESSAGE_LISTENER_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSingleMessageListenerRule.CreateSingleMessageListenerRule.class,
						AgentSingleMessageListenerRule.HandleReceivedMessageRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentSingleMessageListenerRule with rule rest.")
	void testInitializeAgentSingleMessageListenerRuleWithRuleRest() {
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSingleMessageListenerRule.CreateSingleMessageListenerRule.class,
						AgentSingleMessageListenerRule.HandleReceivedMessageRule.class);
		assertNotNull(testRule.expressionConstructMessageTemplate);
		assertNotNull(testRule.expressionSpecifyExpirationTime);
		assertNotNull(testRule.expressionHandleMessageProcessing);
		assertNotNull(testRule.expressionHandleMessageNotReceived);
	}

	@Test
	@DisplayName("Test copy of AgentSingleMessageListenerRule.")
	void testCopyOfAgentSingleMessageListenerRule() {
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentSingleMessageListenerRule<?, ?> testRuleCopy = (AgentSingleMessageListenerRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionConstructMessageTemplate);
		assertNotNull(testRuleCopy.expressionSpecifyExpirationTime);
		assertNotNull(testRuleCopy.expressionHandleMessageProcessing);
		assertNotNull(testRuleCopy.expressionHandleMessageNotReceived);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSingleMessageListenerRule.CreateSingleMessageListenerRule.class,
						AgentSingleMessageListenerRule.HandleReceivedMessageRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);

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
	@DisplayName("Test get AgentSingleMessageListenerRule rule type.")
	void testGetAgentSingleMessageListenerRuleRuleType() {
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);

		assertEquals(LISTENER_SINGLE.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize CreateSingleMessageListenerRule description.")
	void testInitializeCreateSingleMessageListenerRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);
		final AgentSingleMessageListenerRule<?, ?>.CreateSingleMessageListenerRule messageRule =
				selectCreateSingleMessageRule(testRule);

		final AgentRuleDescription description = messageRule.initializeRuleDescription();

		assertEquals(DEFAULT_SINGLE_MESSAGE_LISTENER_RULE, description.ruleType());
		assertEquals(SINGLE_MESSAGE_READER_CREATE_STEP.getType(), description.stepType());
		assertEquals("default single-time message listener rule - initialization of behaviour", description.ruleName());
		assertEquals("rule constructs message template and specifies expiration duration",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleReceivedMessageRule description.")
	void testInitializeHandleReceivedMessageRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);
		final AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule receiveMessageRule =
				selectHandleReceiveMessageRule(testRule);

		final AgentRuleDescription description = receiveMessageRule.initializeRuleDescription();

		assertEquals(DEFAULT_SINGLE_MESSAGE_LISTENER_RULE, description.ruleType());
		assertEquals(SINGLE_MESSAGE_READER_HANDLE_MESSAGE_STEP.getType(), description.stepType());
		assertEquals("default single-time message listener rule - handling received message", description.ruleName());
		assertEquals("rule triggers method which handles received message", description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of CreateSingleMessageListenerRule when initialization was with rule rest.")
	void testExecuteCreateSingleMessageListenerRuleForRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSingleMessageListenerRule<?, ?>.CreateSingleMessageListenerRule messageRule =
				selectCreateSingleMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		messageRule.executeRule(testFacts);
		final MessageTemplate messageTemplate = testFacts.get(MESSAGE_TEMPLATE);
		final long expirationDuration = testFacts.get(MESSAGE_EXPIRATION);

		final ACLMessage messageShouldNotMatch = MessageBuilder.builder(0, CFP).build();
		final ACLMessage messageShouldMatch = MessageBuilder.builder(0, CFP)
				.withStringContent("123")
				.build();

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertTrue(messageTemplate.match(messageShouldMatch));
		assertFalse(messageTemplate.match(messageShouldNotMatch));
		assertEquals(10, expirationDuration);
	}

	@Test
	@DisplayName("Test execution of CreateSingleMessageListenerRule when initialization was with controller.")
	void testExecuteCreateSingleMessageListenerRuleForControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);
		final AgentSingleMessageListenerRule<?, ?>.CreateSingleMessageListenerRule messageRule =
				selectCreateSingleMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		messageRule.executeRule(testFacts);
		final MessageTemplate messageTemplate = testFacts.get(MESSAGE_TEMPLATE);
		final long expirationDuration = testFacts.get(MESSAGE_EXPIRATION);

		final ACLMessage messageShouldMatch = MessageBuilder.builder(0, CFP).build();
		final ACLMessage messageShouldMatch2 = MessageBuilder.builder(0, CFP)
				.withStringContent("123")
				.build();

		assertNull(testRule.getInitialParameters());
		assertTrue(messageTemplate.match(messageShouldMatch));
		assertTrue(messageTemplate.match(messageShouldMatch2));
		assertEquals(0, expirationDuration);
	}

	@Test
	@DisplayName("Test execution of HandleReceivedMessageRule when initialization was with rule rest and message received.")
	void testExecuteHandleReceivedMessageRuleForRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule receiveMessageRule =
				selectHandleReceiveMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final ACLMessage message = MessageBuilder.builder(0, CFP).build();
		testFacts.put(RECEIVED_MESSAGE, Optional.of(message));

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			receiveMessageRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(MESSAGE));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleMessageProcessing,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleReceivedMessageRule when initialization was with rule rest and message not received.")
	void testExecuteHandleReceivedMessageRuleForRestInitializationWithoutMessage() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SingleMessageListenerRuleRest ruleRest = prepareSingleMessageRuleRest();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule receiveMessageRule =
				selectHandleReceiveMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(RECEIVED_MESSAGE, Optional.empty());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			receiveMessageRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleMessageNotReceived,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleReceivedMessageRule when initialization was with controller and message received.")
	void testExecuteHandleReceivedMessageRuleForControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);
		final AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule receiveMessageRule =
				selectHandleReceiveMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final ACLMessage message = MessageBuilder.builder(0, CFP).build();
		testFacts.put(RECEIVED_MESSAGE, Optional.of(message));

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			receiveMessageRule.executeRule(testFacts);

			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleReceivedMessageRule when initialization was with controller and message not received.")
	void testExecuteHandleReceivedMessageForControllerInitializationWithoutMessage() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSingleMessageListenerRule<?, ?> testRule = new AgentSingleMessageListenerRule<>(testRulesController);
		final AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule receiveMessageRule =
				selectHandleReceiveMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(RECEIVED_MESSAGE, Optional.empty());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			receiveMessageRule.executeRule(testFacts);

			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	private AgentSingleMessageListenerRule<?, ?>.CreateSingleMessageListenerRule selectCreateSingleMessageRule(
			final AgentSingleMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule ->
						rule.getClass().equals(AgentSingleMessageListenerRule.CreateSingleMessageListenerRule.class))
				.findFirst()
				.map(AgentSingleMessageListenerRule.CreateSingleMessageListenerRule.class::cast)
				.orElseThrow();
	}

	private AgentSingleMessageListenerRule<?, ?>.HandleReceivedMessageRule selectHandleReceiveMessageRule(
			final AgentSingleMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule ->
						rule.getClass().equals(AgentSingleMessageListenerRule.HandleReceivedMessageRule.class))
				.findFirst()
				.map(AgentSingleMessageListenerRule.HandleReceivedMessageRule.class::cast)
				.orElseThrow();
	}
}
