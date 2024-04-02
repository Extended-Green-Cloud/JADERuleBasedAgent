package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.immutables.value.internal.$guava$.collect.$ImmutableList.of;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareDefaultRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRequestRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_BEST_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_CREATE_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_NEW_PROPOSAL;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_RECEIVED_PROPOSALS;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_REJECT_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.CFP_RESULT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.ALL_PROPOSALS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.BEST_PROPOSAL;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.NEW_PROPOSAL;
import static org.jrba.rulesengine.constants.MVELParameterConstants.PROPOSAL_TO_REJECT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_CFP_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_COMPARE_MESSAGES_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_CREATE_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_HANDLE_NO_AVAILABLE_AGENTS_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_HANDLE_NO_RESPONSES_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_HANDLE_REJECT_PROPOSAL_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.CFP_HANDLE_SELECTED_PROPOSAL_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.CFP;
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
import static org.mockito.Mockito.mockStatic;

import java.util.HashMap;
import java.util.List;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.CallForProposalRuleRest;
import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRule;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.lang.acl.ACLMessage;

class AgentCFPRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentCFPRule with controller.")
	void testInitializeAgentCFPRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default Call For Proposal rule", testRule.getName());
		assertEquals("default implementation of a rule that executes Call For Proposal FIPA protocol",
				testRule.getDescription());
		assertEquals(DEFAULT_CFP_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(6)
				.hasExactlyElementsOfTypes(AgentCFPRule.CreateCFPRule.class,
						AgentCFPRule.CompareCFPMessageRule.class,
						AgentCFPRule.HandleRejectProposalRule.class,
						AgentCFPRule.HandleNoProposalsRule.class,
						AgentCFPRule.HandleNoResponsesRule.class,
						AgentCFPRule.HandleProposalsRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentCFPRule with rule rest.")
	void testInitializeAgentCFPRuleWithRuleRest() {
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(6)
				.hasExactlyElementsOfTypes(AgentCFPRule.CreateCFPRule.class,
						AgentCFPRule.CompareCFPMessageRule.class,
						AgentCFPRule.HandleRejectProposalRule.class,
						AgentCFPRule.HandleNoProposalsRule.class,
						AgentCFPRule.HandleNoResponsesRule.class,
						AgentCFPRule.HandleProposalsRule.class);
		assertNotNull(testRule.expressionCreateCFP);
		assertNotNull(testRule.expressionCompareProposals);
		assertNotNull(testRule.expressionHandleNoProposals);
		assertNotNull(testRule.expressionHandleProposals);
		assertNotNull(testRule.expressionHandleRejectProposal);
		assertNotNull(testRule.expressionHandleNoResponses);
	}

	@Test
	@DisplayName("Test copy of AgentCFPRule.")
	void testCopyOfAgentCFPRule() {
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?> testRuleCopy = (AgentCFPRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionCreateCFP);
		assertNotNull(testRuleCopy.expressionCompareProposals);
		assertNotNull(testRuleCopy.expressionHandleNoProposals);
		assertNotNull(testRuleCopy.expressionHandleProposals);
		assertNotNull(testRuleCopy.expressionHandleRejectProposal);
		assertNotNull(testRuleCopy.expressionHandleNoResponses);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(6)
				.hasExactlyElementsOfTypes(AgentCFPRule.CreateCFPRule.class,
						AgentCFPRule.CompareCFPMessageRule.class,
						AgentCFPRule.HandleRejectProposalRule.class,
						AgentCFPRule.HandleNoProposalsRule.class,
						AgentCFPRule.HandleNoResponsesRule.class,
						AgentCFPRule.HandleProposalsRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);

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
	@DisplayName("Test get AgentCFPRule rule type.")
	void testGetAgentCFPRuleType() {
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);

		assertEquals(CFP.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize CreateCFPRule description.")
	void testInitializeCreateCFPRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.CreateCFPRule createRule = selectCreateCFPRule(testRule);

		final AgentRuleDescription description = createRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_CREATE_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - create CFP message", description.ruleName());
		assertEquals("when agent initiate RMA lookup, it creates CFP", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize CompareCFPMessageRule description.")
	void testInitializeCompareCFPMessageRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.CompareCFPMessageRule compareRule = selectCompareCFPMessageRule(testRule);

		final AgentRuleDescription description = compareRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_COMPARE_MESSAGES_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - compare received proposal message",
				description.ruleName());
		assertEquals("when agent receives new proposal message, it compares it with current best proposal",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleRejectProposalRule description.")
	void testInitializeHandleRejectProposalRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleRejectProposalRule rejectHandlerRule = selectHandleRejectProposalRule(testRule);

		final AgentRuleDescription description = rejectHandlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_HANDLE_REJECT_PROPOSAL_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - reject received proposal", description.ruleName());
		assertEquals("rule executed when received proposal is to be rejected", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleNoResponsesRule description.")
	void testInitializeHandleNoResponsesRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleNoResponsesRule noResponseHandlerRule = selectHandleNoResponsesRule(testRule);

		final AgentRuleDescription description = noResponseHandlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_HANDLE_NO_RESPONSES_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - no responses received", description.ruleName());
		assertEquals("rule executed when there are 0 responses to CFP", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleNoProposalsRule description.")
	void testInitializeHandleNoProposalsRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleNoProposalsRule noProposalHandlerRule = selectHandleNoProposalsRule(testRule);

		final AgentRuleDescription description = noProposalHandlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_HANDLE_NO_AVAILABLE_AGENTS_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - no proposals received", description.ruleName());
		assertEquals("rule executed when there are 0 proposals to CFP", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleProposalsRule description.")
	void testInitializeHandleProposalsRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleProposalsRule proposalHandlerRule = selectHandleProposalsRule(testRule);

		final AgentRuleDescription description = proposalHandlerRule.initializeRuleDescription();

		assertEquals(DEFAULT_CFP_RULE, description.ruleType());
		assertEquals(CFP_HANDLE_SELECTED_PROPOSAL_STEP.getType(), description.stepType());
		assertEquals("default Call For Proposal rule - handle proposals", description.ruleName());
		assertEquals("rule executed when there are some proposals to CFP", description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of CreateCFPRule for rest initialization.")
	void testExecuteCreateCFPRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.CreateCFPRule createRule = selectCreateCFPRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(CFP_CREATE_MESSAGE);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertEquals("123", result.getContent());
	}

	@Test
	@DisplayName("Test execution of CreateCFPRule for controller initialization.")
	void testExecuteCreateCFPRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.CreateCFPRule createRule = selectCreateCFPRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(CFP_CREATE_MESSAGE);

		assertNull(testRule.getInitialParameters());
		assertEquals(ACLMessage.CFP, result.getPerformative());
		assertNull(result.getContent());
	}

	@Test
	@DisplayName("Test execution of CompareCFPMessageRule for rest initialization.")
	void testExecuteCompareCFPMessageRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.CompareCFPMessageRule compareRule = selectCompareCFPMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_BEST_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());
		testFacts.put(CFP_NEW_PROPOSAL, MessageBuilder.builder(0, ACLMessage.CFP).build());

		compareRule.executeRule(testFacts);

		final int result = testFacts.get(CFP_RESULT);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(testFacts.asMap().containsKey(BEST_PROPOSAL));
		assertFalse(testFacts.asMap().containsKey(NEW_PROPOSAL));
		assertEquals(1, result);
	}

	@Test
	@DisplayName("Test execution of CompareCFPMessageRule for controller initialization.")
	void testExecuteCompareCFPMessageRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.CompareCFPMessageRule compareRule = selectCompareCFPMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_BEST_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());
		testFacts.put(CFP_NEW_PROPOSAL, MessageBuilder.builder(0, ACLMessage.CFP).build());

		compareRule.executeRule(testFacts);

		final int result = testFacts.get(CFP_RESULT);

		assertNull(testRule.getInitialParameters());
		assertEquals(0, result);
	}

	@Test
	@DisplayName("Test execution of HandleRejectProposalRule for rest initialization.")
	void testExecuteHandleRejectProposalRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.HandleRejectProposalRule rejectRule = selectHandleRejectProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_REJECT_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			rejectRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(PROPOSAL_TO_REJECT));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleRejectProposal,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleRejectProposalRule for controller initialization.")
	void testExecuteHandleRejectProposalRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleRejectProposalRule rejectRule = selectHandleRejectProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_REJECT_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> rejectRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleNoResponsesRule for rest initialization.")
	void testExecuteHandleNoResponsesRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.HandleNoResponsesRule noResponsesRule = selectHandleNoResponsesRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			noResponsesRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleNoResponses,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleNoResponsesRule for controller initialization.")
	void testExecuteHandleNoResponsesRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleNoResponsesRule noResponsesRule = selectHandleNoResponsesRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> noResponsesRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleNoProposalsRule for rest initialization.")
	void testExecuteHandleNoProposalsRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.HandleNoProposalsRule noProposalsRule = selectHandleNoProposalsRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			noProposalsRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleNoProposals,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleNoProposalsRule for controller initialization.")
	void testExecuteHandleNoProposalsRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleNoProposalsRule noProposalsRule = selectHandleNoProposalsRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> noProposalsRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleProposalsRule for rest initialization.")
	void testExecuteHandleProposalsRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final CallForProposalRuleRest ruleRest = prepareCallForProposalRuleRest();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentCFPRule<?, ?>.HandleProposalsRule proposalsRule = selectHandleProposalsRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_BEST_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());
		testFacts.put(CFP_RECEIVED_PROPOSALS, of(MessageBuilder.builder(0, ACLMessage.CFP).build()));

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			proposalsRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(BEST_PROPOSAL));
			assertFalse(testRule.getInitialParameters().containsKey(ALL_PROPOSALS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleProposals,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleProposalsRule for controller initialization.")
	void testExecuteHandleProposalsRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentCFPRule<?, ?> testRule = new AgentCFPRule<>(testRulesController);
		final AgentCFPRule<?, ?>.HandleProposalsRule proposalsRule = selectHandleProposalsRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(CFP_BEST_MESSAGE, MessageBuilder.builder(0, ACLMessage.CFP).build());
		testFacts.put(CFP_RECEIVED_PROPOSALS, of(MessageBuilder.builder(0, ACLMessage.CFP).build()));

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> proposalsRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	private AgentCFPRule<?, ?>.CreateCFPRule selectCreateCFPRule(final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.CreateCFPRule.class))
				.findFirst()
				.map(AgentCFPRule.CreateCFPRule.class::cast)
				.orElseThrow();
	}

	private AgentCFPRule<?, ?>.CompareCFPMessageRule selectCompareCFPMessageRule(final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.CompareCFPMessageRule.class))
				.findFirst()
				.map(AgentCFPRule.CompareCFPMessageRule.class::cast)
				.orElseThrow();
	}

	private AgentCFPRule<?, ?>.HandleRejectProposalRule selectHandleRejectProposalRule(
			final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.HandleRejectProposalRule.class))
				.findFirst()
				.map(AgentCFPRule.HandleRejectProposalRule.class::cast)
				.orElseThrow();
	}

	private AgentCFPRule<?, ?>.HandleNoResponsesRule selectHandleNoResponsesRule(final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.HandleNoResponsesRule.class))
				.findFirst()
				.map(AgentCFPRule.HandleNoResponsesRule.class::cast)
				.orElseThrow();
	}

	private AgentCFPRule<?, ?>.HandleNoProposalsRule selectHandleNoProposalsRule(final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.HandleNoProposalsRule.class))
				.findFirst()
				.map(AgentCFPRule.HandleNoProposalsRule.class::cast)
				.orElseThrow();
	}

	private AgentCFPRule<?, ?>.HandleProposalsRule selectHandleProposalsRule(final AgentCFPRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentCFPRule.HandleProposalsRule.class))
				.findFirst()
				.map(AgentCFPRule.HandleProposalsRule.class::cast)
				.orElseThrow();
	}
}
