package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.fixtures.TestRulesFixtures.prepareSingleMessageRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareSubscriptionRuleRest;
import static org.jrba.rulesengine.constants.FactTypeConstants.SUBSCRIPTION_ADDED_AGENTS;
import static org.jrba.rulesengine.constants.FactTypeConstants.SUBSCRIPTION_CREATE_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.SUBSCRIPTION_REMOVED_AGENTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.ADDED_AGENTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.REMOVED_AGENTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_SUBSCRIPTION_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.SUBSCRIPTION_CREATE_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.SUBSCRIPTION_HANDLE_AGENTS_RESPONSE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.SUBSCRIPTION;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRulesControllerConnection;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.SingleMessageListenerRuleRest;
import org.jrba.rulesengine.rest.domain.SubscriptionRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class AgentSubscriptionRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentSubscriptionRule with controller.")
	void testInitializeAgentSubscriptionRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default subscription rule", testRule.getName());
		assertEquals("default implementation of a rule that handle (de-)registration of new agent services",
				testRule.getDescription());
		assertEquals(DEFAULT_SUBSCRIPTION_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSubscriptionRule.CreateSubscriptionRule.class,
						AgentSubscriptionRule.HandleDFInformMessage.class);
	}

	@Test
	@DisplayName("Test initialize AgentSubscriptionRule with rule rest.")
	void testInitializeAgentSubscriptionRuleWithRuleRest() {
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSubscriptionRule.CreateSubscriptionRule.class,
						AgentSubscriptionRule.HandleDFInformMessage.class);
		assertNotNull(testRule.expressionCreateSubscriptionMessage);
		assertNotNull(testRule.expressionHandleRemovedAgents);
		assertNotNull(testRule.expressionHandleAddedAgents);
	}

	@Test
	@DisplayName("Test copy of AgentSubscriptionRule.")
	void testCopyOfAgentSubscriptionRule() {
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentSubscriptionRule<?, ?> testRuleCopy = (AgentSubscriptionRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionCreateSubscriptionMessage);
		assertNotNull(testRuleCopy.expressionHandleAddedAgents);
		assertNotNull(testRuleCopy.expressionHandleRemovedAgents);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(2)
				.hasExactlyElementsOfTypes(AgentSubscriptionRule.CreateSubscriptionRule.class,
						AgentSubscriptionRule.HandleDFInformMessage.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);

		testRule.connectToController(testRulesController);

		verifyDefaultRulesControllerConnection(testRule, testRulesController);
		assertThatCollection(testRule.getStepRules()).allSatisfy((rule) -> {
			assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
			assertInstanceOf(TestAbstractAgentCustom.class, testRule.getAgent());
			assertInstanceOf(TestAgentPropsDefault.class, testRule.getAgentProps());
			assertInstanceOf(TestAgentNodeDefault.class, testRule.getAgentNode());
		});
	}

	@Test
	@DisplayName("Test get AgentSubscriptionRule rule type.")
	void testGetAgentSubscriptionRuleRuleType() {
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);

		assertEquals(SUBSCRIPTION.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test default message creation.")
	void testCreateSubscriptionMessage() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(testRulesController);

		final ACLMessage result = testRule.createSubscriptionMessage(new RuleSetFacts(0));

		assertEquals(ACLMessage.SUBSCRIBE, result.getPerformative());
	}

	@Test
	@DisplayName("Test initialize CreateSubscriptionRule description.")
	void testInitializeCreateSubscriptionRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(testRulesController);
		final AgentSubscriptionRule<?, ?>.CreateSubscriptionRule subscribeRule = selectCreateSubscriptionRule(testRule);

		final AgentRuleDescription description = subscribeRule.initializeRuleDescription();

		assertEquals(DEFAULT_SUBSCRIPTION_RULE, description.ruleType());
		assertEquals(SUBSCRIPTION_CREATE_STEP.getType(), description.stepType());
		assertEquals("default subscription rule - create subscription message", description.ruleName());
		assertEquals("when agent initiate DF subscription, it creates subscription message",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of CreateSubscriptionRule when initialization was with rule rest.")
	void testExecuteCreateSubscriptionRuleForRestInitialization() {
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSubscriptionRule<?, ?>.CreateSubscriptionRule subscribeRule = selectCreateSubscriptionRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		subscribeRule.executeRule(testFacts);
		final ACLMessage createdMessage = testFacts.get(SUBSCRIPTION_CREATE_MESSAGE);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(createdMessage).matches(msg -> msg.getContent().equals("123"));
	}

	@Test
	@DisplayName("Test execution of CreateSubscriptionRule when initialization was with rule controller.")
	void testExecuteCreateSubscriptionRuleForControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(testRulesController);
		final AgentSubscriptionRule<?, ?>.CreateSubscriptionRule subscribeRule = selectCreateSubscriptionRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		subscribeRule.executeRule(testFacts);
		final ACLMessage createdMessage = testFacts.get(SUBSCRIPTION_CREATE_MESSAGE);

		assertNull(testRule.getInitialParameters());
		assertNull(createdMessage.getContent());
	}

	@Test
	@DisplayName("Test execution of HandleDFInformMessage when initialization was with rule rest.")
	void testExecuteHandleDFInformMessageForRestInitialization() {
		final SubscriptionRuleRest ruleRest = prepareSubscriptionRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = new AgentSubscriptionRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSubscriptionRule<?, ?>.HandleDFInformMessage handlerRule = selectHandleDFInformMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(SUBSCRIPTION_ADDED_AGENTS, prepareAddedAgents());
		testFacts.put(SUBSCRIPTION_REMOVED_AGENTS, prepareRemovedAgents());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handlerRule.executeRule(testFacts);

			mvlMock.verify(() ->
					MVEL.executeExpression(testRule.expressionHandleAddedAgents, testRule.getInitialParameters()));
			mvlMock.verify(() ->
					MVEL.executeExpression(testRule.expressionHandleRemovedAgents, testRule.getInitialParameters()));
			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertNull(testRule.getInitialParameters().get(ADDED_AGENTS));
			assertNull(testRule.getInitialParameters().get(REMOVED_AGENTS));
		}
	}

	@Test
	@DisplayName("Test execution of HandleDFInformMessage when initialization was with rule controller.")
	void testExecuteHandleDFInformMessageForControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = spy(new AgentSubscriptionRule<>(testRulesController));
		final AgentSubscriptionRule<?, ?>.HandleDFInformMessage handlerRule = selectHandleDFInformMessageRule(testRule);

		final Map<AID, Boolean> addedAgents = prepareAddedAgents();
		final Map<AID, Boolean> removedAgents = prepareRemovedAgents();
		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(SUBSCRIPTION_ADDED_AGENTS, addedAgents);
		testFacts.put(SUBSCRIPTION_REMOVED_AGENTS, removedAgents);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			handlerRule.executeRule(testFacts);

			mvlMock.verifyNoInteractions();
			assertNull(testRule.getInitialParameters());
		}
	}

	@Test
	@DisplayName("Test initialize HandleDFInformMessage description.")
	void testInitializeHandleDFInformMessageRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSubscriptionRule<?, ?> testRule = spy(new AgentSubscriptionRule<>(testRulesController));
		final AgentSubscriptionRule<?, ?>.HandleDFInformMessage handlerRule = selectHandleDFInformMessageRule(testRule);

		final AgentRuleDescription description = handlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_SUBSCRIPTION_RULE, description.ruleType());
		assertEquals(SUBSCRIPTION_HANDLE_AGENTS_RESPONSE_STEP.getType(), description.stepType());
		assertEquals("default subscription rule - handle changes in subscribed service", description.ruleName());
		assertEquals("when DF sends information about changes in subscribed service, agent executes default handlers",
				description.ruleDescription());
	}

	private AgentSubscriptionRule<?, ?>.CreateSubscriptionRule selectCreateSubscriptionRule(
			final AgentSubscriptionRule<?, ?> testRule) {
		return testRule.getStepRules().stream()
				.filter(rule -> rule.getClass().equals(AgentSubscriptionRule.CreateSubscriptionRule.class))
				.findFirst()
				.map(AgentSubscriptionRule.CreateSubscriptionRule.class::cast)
				.orElseThrow();
	}

	private AgentSubscriptionRule<?, ?>.HandleDFInformMessage selectHandleDFInformMessageRule(
			final AgentSubscriptionRule<?, ?> testRule) {
		return testRule.getStepRules().stream()
				.filter(rule -> rule.getClass().equals(AgentSubscriptionRule.HandleDFInformMessage.class))
				.findFirst()
				.map(AgentSubscriptionRule.HandleDFInformMessage.class::cast)
				.orElseThrow();
	}

	private Map<AID, Boolean> prepareAddedAgents() {
		return Map.of(
				new AID("TestAgent1", AID.ISGUID), true,
				new AID("TestAgent2", AID.ISGUID), false
		);
	}

	private Map<AID, Boolean> prepareRemovedAgents() {
		return Map.of(
				new AID("TestAgent3", AID.ISGUID), true,
				new AID("TestAgent4", AID.ISGUID), false
		);
	}
}
