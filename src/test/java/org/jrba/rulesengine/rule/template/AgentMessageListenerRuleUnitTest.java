package org.jrba.rulesengine.rule.template;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareMessageListenerRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGE_TYPE;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_SET_IDX;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_STEP;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_TYPE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_MESSAGE_LISTENER_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.MESSAGE_READER_PROCESS_CONTENT_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.MESSAGE_READER_READ_CONTENT_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.MESSAGE_READER_READ_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.LISTENER;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.CallForProposalRuleRest;
import org.jrba.rulesengine.rest.domain.MessageListenerRuleRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

class AgentMessageListenerRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentMessageListenerRule with all args constructor.")
	void testInitializeAgentPeriodicRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(testRulesController, testRuleSet,
				AID.class, testTemplate, 10, "TEST_HANDLER_RULE_TYPE");

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default message listener rule", testRule.getName());
		assertEquals("default implementation of a rule that listens and processes new messages received by an agent",
				testRule.getDescription());
		assertEquals(DEFAULT_MESSAGE_LISTENER_RULE, testRule.getRuleType());
		assertEquals(10, testRule.batchSize);
		assertEquals(AID.class, testRule.contentType);
		assertTrue(testRule.messageTemplate.match(MessageBuilder.builder(0, REQUEST).build()));
		assertEquals(testRuleSet, testRule.ruleSet);
		assertEquals("TEST_HANDLER_RULE_TYPE", testRule.handlerRuleType);
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentMessageListenerRule.ReadMessagesRule.class,
						AgentMessageListenerRule.ReadMessagesContentRule.class,
						AgentMessageListenerRule.HandleMessageRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentMessageListenerRule without content.")
	void testInitializeAgentPeriodicRuleWithControllerWithoutContent() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(testRulesController, testRuleSet,
				testTemplate, 10, "TEST_HANDLER_RULE_TYPE");

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default message listener rule", testRule.getName());
		assertEquals("default implementation of a rule that listens and processes new messages received by an agent",
				testRule.getDescription());
		assertEquals(DEFAULT_MESSAGE_LISTENER_RULE, testRule.getRuleType());
		assertEquals(10, testRule.batchSize);
		assertNull(testRule.contentType);
		assertTrue(testRule.messageTemplate.match(MessageBuilder.builder(0, REQUEST).build()));
		assertEquals(testRuleSet, testRule.ruleSet);
		assertEquals("TEST_HANDLER_RULE_TYPE", testRule.handlerRuleType);
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentMessageListenerRule.ReadMessagesRule.class,
						AgentMessageListenerRule.ReadMessagesContentRule.class,
						AgentMessageListenerRule.HandleMessageRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentMessageListenerRule with rule rest.")
	void testInitializeAgentPeriodicRuleWithControllerWithRuleRest() {
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(ruleRest, testRuleSet);

		verifyDefaultRuleRestFields(testRule);
		assertNotNull(testRule.expressionSelectRuleSetIdx);
		assertEquals(5, testRule.batchSize);
		assertEquals(Instant.class, testRule.contentType);
		assertFalse(testRule.messageTemplate.match(MessageBuilder.builder(0, REQUEST).build()));
		assertEquals(testRuleSet, testRule.ruleSet);
		assertEquals("TEST_REST_HANDLER", testRule.handlerRuleType);
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentMessageListenerRule.ReadMessagesRule.class,
						AgentMessageListenerRule.ReadMessagesContentRule.class,
						AgentMessageListenerRule.HandleMessageRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentMessageListenerRule with rule rest and incorrect class.")
	void testInitializeAgentPeriodicRuleWithControllerWithRuleRestIncorrectClass() {
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		ruleRest.setClassName("Incorrect Name");

		assertThatThrownBy(() -> new AgentMessageListenerRule<>(ruleRest, testRuleSet))
				.isInstanceOf(ClassCastException.class)
				.hasMessage("Content type class was not found!");
	}

	@Test
	@DisplayName("Test copy of AgentMessageListenerRule.")
	void testCopyOfAgentMessageListenerRule() {
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(ruleRest, testRuleSet);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentMessageListenerRule<?, ?> testRuleCopy = (AgentMessageListenerRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionSelectRuleSetIdx);
		assertEquals(5, testRuleCopy.batchSize);
		assertEquals(Instant.class, testRuleCopy.contentType);
		assertFalse(testRuleCopy.messageTemplate.match(MessageBuilder.builder(0, REQUEST).build()));
		assertEquals(testRuleSet, testRuleCopy.ruleSet);
		assertEquals("TEST_REST_HANDLER", testRuleCopy.handlerRuleType);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentMessageListenerRule.ReadMessagesRule.class,
						AgentMessageListenerRule.ReadMessagesContentRule.class,
						AgentMessageListenerRule.HandleMessageRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(ruleRest, testRuleSet);

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
	@DisplayName("Test get AgentMessageListenerRule rule type.")
	void testGetAgentMessageListenerRuleType() {
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(ruleRest, testRuleSet);

		assertEquals(LISTENER.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize ReadMessagesRule description.")
	void testInitializeReadMessagesRuleDescription() {
		final AgentMessageListenerRule<?, ?> testRule = initializeDefaultListenerWithController();
		final AgentMessageListenerRule<?, ?>.ReadMessagesRule readRule = selectReadMessageRule(testRule);

		final AgentRuleDescription description = readRule.initializeRuleDescription();

		assertEquals(DEFAULT_MESSAGE_LISTENER_RULE, description.ruleType());
		assertEquals(MESSAGE_READER_READ_STEP.getType(), description.stepType());
		assertEquals("default message listener rule - read messages", description.ruleName());
		assertEquals(
				"when new message event is triggerred, agent attempts to read messages corresponding to selected template",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize ReadMessagesContentRule description.")
	void testInitializeReadMessagesContentRuleDescription() {
		final AgentMessageListenerRule<?, ?> testRule = initializeDefaultListenerWithController();
		final AgentMessageListenerRule<?, ?>.ReadMessagesContentRule readRule = selectReadMessageContentRule(testRule);

		final AgentRuleDescription description = readRule.initializeRuleDescription();

		assertEquals(DEFAULT_MESSAGE_LISTENER_RULE, description.ruleType());
		assertEquals(MESSAGE_READER_READ_CONTENT_STEP.getType(), description.stepType());
		assertEquals("default message listener rule - read message content", description.ruleName());
		assertEquals("when new message matching given template is present, then agent reads its content",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleMessageRule description.")
	void testInitializeHandleMessageRuleDescription() {
		final AgentMessageListenerRule<?, ?> testRule = initializeDefaultListenerWithController();
		final AgentMessageListenerRule<?, ?>.HandleMessageRule handlerRule = selectHandleMessageRule(testRule);

		final AgentRuleDescription description = handlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_MESSAGE_LISTENER_RULE, description.ruleType());
		assertEquals(MESSAGE_READER_PROCESS_CONTENT_STEP.getType(), description.stepType());
		assertEquals("default message listener rule - handle message", description.ruleName());
		assertEquals("when agent reads message of given type, its handler is run",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of ReadMessagesContentRule for rest initialization.")
	void testExecuteReadMessagesContentRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final MessageListenerRuleRest ruleRest = prepareMessageListenerRuleRest();
		final RuleSet testRuleSet = prepareRuleSet();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(ruleRest, testRuleSet);
		testRule.connectToController(testRulesController);

		final AgentMessageListenerRule<?, ?>.ReadMessagesContentRule readRule =
				selectReadMessageContentRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(MESSAGE, prepareTestMessage());

		readRule.executeRule(testFacts);

		final Instant contentResult = testFacts.get(MESSAGE_CONTENT);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(contentResult).isEqualTo("2024-01-01T00:00:00.00Z");
		assertEquals(5, (Integer) testFacts.get(RULE_SET_IDX));
		assertEquals("", testFacts.get(MESSAGE_TYPE));
		assertEquals(MESSAGE_READER_PROCESS_CONTENT_STEP.getType(), testFacts.get(RULE_STEP));
	}

	@Test
	@DisplayName("Test execution of ReadMessagesContentRule for controller initialization.")
	void testExecuteReadMessagesContentRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(testRulesController, testRuleSet,
				Instant.class, testTemplate, 10, "TEST_HANDLER_RULE_TYPE");

		final AgentMessageListenerRule<?, ?>.ReadMessagesContentRule readRule =
				selectReadMessageContentRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		final ACLMessage testMessage = prepareTestMessage();
		testMessage.setConversationId("TEST_ID");
		testFacts.put(MESSAGE, testMessage);

		readRule.executeRule(testFacts);

		final Instant contentResult = testFacts.get(MESSAGE_CONTENT);

		assertNull(testRule.getInitialParameters());
		assertThat(contentResult).isEqualTo("2024-01-01T00:00:00.00Z");
		assertEquals(0, (Integer) testFacts.get(RULE_SET_IDX));
		assertEquals("TEST_ID", testFacts.get(MESSAGE_TYPE));
		assertEquals(MESSAGE_READER_PROCESS_CONTENT_STEP.getType(), testFacts.get(RULE_STEP));
	}

	@Test
	@DisplayName("Test evaluation of HandleMessageRule.")
	void testHandleMessageRuleEvaluation() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(testRulesController, testRuleSet,
				Instant.class, testTemplate, 10, "TEST_HANDLER_RULE_TYPE");

		final AgentMessageListenerRule<?, ?>.HandleMessageRule handlerRule = selectHandleMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		assertTrue(handlerRule.evaluateRule(testFacts));
	}

	@Test
	@DisplayName("Test execution of HandleMessageRule.")
	void testHandleMessageRuleExecution() {
		final RulesController<?, ?> testRulesController = spy(prepareRulesController());
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();
		final AgentMessageListenerRule<?, ?> testRule = new AgentMessageListenerRule<>(testRulesController, testRuleSet,
				Instant.class, testTemplate, 10, "TEST_HANDLER_RULE_TYPE");

		final AgentMessageListenerRule<?, ?>.HandleMessageRule handlerRule = selectHandleMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		handlerRule.execute(testFacts);

		verify(testRulesController).fire(argThat((facts) ->
				facts.asMap().containsKey(RULE_TYPE) &&
						facts.asMap().get(RULE_TYPE).equals("TEST_HANDLER_RULE_TYPE")));
	}

	private ACLMessage prepareTestMessage() {
		return MessageBuilder.builder(0, PROPOSE)
				.withObjectContent(parse("2024-01-01T00:00:00.00Z"))
				.build();
	}

	private AgentMessageListenerRule<?, ?> initializeDefaultListenerWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MessageTemplate.MatchAll();

		return new AgentMessageListenerRule<>(testRulesController, testRuleSet, AID.class, testTemplate, 10,
				"TEST_HANDLER_RULE_TYPE");
	}

	private AgentMessageListenerRule<?, ?>.ReadMessagesRule selectReadMessageRule(
			final AgentMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentMessageListenerRule.ReadMessagesRule.class))
				.findFirst()
				.map(AgentMessageListenerRule.ReadMessagesRule.class::cast)
				.orElseThrow();
	}

	private AgentMessageListenerRule<?, ?>.ReadMessagesContentRule selectReadMessageContentRule(
			final AgentMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentMessageListenerRule.ReadMessagesContentRule.class))
				.findFirst()
				.map(AgentMessageListenerRule.ReadMessagesContentRule.class::cast)
				.orElseThrow();
	}

	private AgentMessageListenerRule<?, ?>.HandleMessageRule selectHandleMessageRule(
			final AgentMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentMessageListenerRule.HandleMessageRule.class))
				.findFirst()
				.map(AgentMessageListenerRule.HandleMessageRule.class::cast)
				.orElseThrow();
	}
}
