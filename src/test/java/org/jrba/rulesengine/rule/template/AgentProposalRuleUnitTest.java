package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.preparePeriodicRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareProposeRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.FactTypeConstants.PROPOSAL_CREATE_MESSAGE;
import static org.jrba.rulesengine.constants.FactTypeConstants.REQUEST_CREATE_MESSAGE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.ACCEPT_MESSAGE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.REJECT_MESSAGE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_PROPOSAL_RULE;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.PROPOSAL_CREATE_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.PROPOSAL_HANDLE_ACCEPT_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.PROPOSAL_HANDLE_REJECT_STEP;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.PROPOSAL;
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
import org.jrba.rulesengine.rest.domain.PeriodicRuleRest;
import org.jrba.rulesengine.rest.domain.ProposalRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.lang.acl.ACLMessage;

class AgentProposalRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentProposalRule with controller.")
	void testInitializeAgentProposalRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default proposal rule", testRule.getName());
		assertEquals("default implementation of a rule that handles each step of FIPA PROPOSE protocol",
				testRule.getDescription());
		assertEquals(DEFAULT_PROPOSAL_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentProposalRule.CreateProposalMessageRule.class,
						AgentProposalRule.HandleAcceptProposalRule.class,
						AgentProposalRule.HandleRejectProposalRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentProposalRule with rule rest.")
	void testInitializeAgentProposalRuleWithRuleRest() {
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentProposalRule.CreateProposalMessageRule.class,
						AgentProposalRule.HandleAcceptProposalRule.class,
						AgentProposalRule.HandleRejectProposalRule.class);
		assertNotNull(testRule.expressionCreateProposal);
		assertNotNull(testRule.expressionHandleAcceptProposal);
		assertNotNull(testRule.expressionHandleRejectProposal);
	}

	@Test
	@DisplayName("Test copy of AgentProposalRule.")
	void testCopyOfAgentProposalRule() {
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentProposalRule<?, ?> testRuleCopy = (AgentProposalRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionCreateProposal);
		assertNotNull(testRuleCopy.expressionHandleAcceptProposal);
		assertNotNull(testRuleCopy.expressionHandleRejectProposal);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentProposalRule.CreateProposalMessageRule.class,
						AgentProposalRule.HandleAcceptProposalRule.class,
						AgentProposalRule.HandleRejectProposalRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);

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
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);

		assertEquals(PROPOSAL.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize CreateProposalMessageRule description.")
	void testInitializeCreateProposalMessageRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.CreateProposalMessageRule proposalRule =
				selectCreateProposalMessageRule(testRule);

		final AgentRuleDescription description = proposalRule.initializeRuleDescription();

		assertEquals(DEFAULT_PROPOSAL_RULE, description.ruleType());
		assertEquals(PROPOSAL_CREATE_STEP.getType(), description.stepType());
		assertEquals("default proposal rule - create proposal message", description.ruleName());
		assertEquals("rule performed when proposal message sent to other agents is to be created",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleAcceptProposalRule description.")
	void testInitializeHandleAcceptProposalRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.HandleAcceptProposalRule acceptRule = selectHandleAcceptProposalRule(testRule);

		final AgentRuleDescription description = acceptRule.initializeRuleDescription();

		assertEquals(DEFAULT_PROPOSAL_RULE, description.ruleType());
		assertEquals(PROPOSAL_HANDLE_ACCEPT_STEP.getType(), description.stepType());
		assertEquals("default proposal rule - handle accept proposal", description.ruleName());
		assertEquals("rule that handles cases when ACCEPT_PROPOSAL message is received",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize HandleRejectProposalRule description.")
	void testInitializeHandleRejectProposalRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.HandleRejectProposalRule rejectRule = selectHandleRejectProposalRule(testRule);

		final AgentRuleDescription description = rejectRule.initializeRuleDescription();

		assertEquals(DEFAULT_PROPOSAL_RULE, description.ruleType());
		assertEquals(PROPOSAL_HANDLE_REJECT_STEP.getType(), description.stepType());
		assertEquals("default proposal rule - handle reject proposal", description.ruleName());
		assertEquals("rule that handles cases when REJECT_PROPOSAL message is received",
				description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of CreateProposalMessageRule for rest initialization.")
	void testExecuteCreateProposalMessageRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentProposalRule<?, ?>.CreateProposalMessageRule createRule = selectCreateProposalMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(PROPOSAL_CREATE_MESSAGE);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(result).satisfies(aclMessage -> {
			assertEquals("123", aclMessage.getContent());
			assertEquals(ACLMessage.PROPOSE, aclMessage.getPerformative());
		});
	}

	@Test
	@DisplayName("Test execution of CreateProposalMessageRule for controller initialization.")
	void testExecuteCreateProposalMessageRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.CreateProposalMessageRule createRule = selectCreateProposalMessageRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		createRule.executeRule(testFacts);

		final ACLMessage result = testFacts.get(PROPOSAL_CREATE_MESSAGE);

		assertNull(testRule.getInitialParameters());
		assertNull(result.getContent());
		assertEquals(ACLMessage.PROPOSE, result.getPerformative());
	}

	@Test
	@DisplayName("Test execution of HandleAcceptProposalRule for rest initialization.")
	void testExecuteHandleAcceptProposalRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentProposalRule<?, ?>.HandleAcceptProposalRule acceptRule = selectHandleAcceptProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			acceptRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(ACCEPT_MESSAGE));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleAcceptProposal,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleAcceptProposalRule for controller initialization.")
	void testExecuteHandleAcceptProposalRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.HandleAcceptProposalRule acceptRule = selectHandleAcceptProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> acceptRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of HandleRejectProposalRule for rest initialization.")
	void testExecuteHandleRejectProposalRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final ProposalRuleRest ruleRest = prepareProposeRuleRest();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentProposalRule<?, ?>.HandleRejectProposalRule rejectRule = selectHandleRejectProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			rejectRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			assertFalse(testRule.getInitialParameters().containsKey(REJECT_MESSAGE));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleRejectProposal,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of HandleRejectProposalRule for controller initialization.")
	void testExecuteHandleRejectProposalRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentProposalRule<?, ?> testRule = new AgentProposalRule<>(testRulesController);
		final AgentProposalRule<?, ?>.HandleRejectProposalRule rejectRule = selectHandleRejectProposalRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> rejectRule.executeRule(testFacts));
			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	private AgentProposalRule<?, ?>.CreateProposalMessageRule selectCreateProposalMessageRule(
			final AgentProposalRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentProposalRule.CreateProposalMessageRule.class))
				.findFirst()
				.map(AgentProposalRule.CreateProposalMessageRule.class::cast)
				.orElseThrow();
	}

	private AgentProposalRule<?, ?>.HandleAcceptProposalRule selectHandleAcceptProposalRule(
			final AgentProposalRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentProposalRule.HandleAcceptProposalRule.class))
				.findFirst()
				.map(AgentProposalRule.HandleAcceptProposalRule.class::cast)
				.orElseThrow();
	}

	private AgentProposalRule<?, ?>.HandleRejectProposalRule selectHandleRejectProposalRule(
			final AgentProposalRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentProposalRule.HandleRejectProposalRule.class))
				.findFirst()
				.map(AgentProposalRule.HandleRejectProposalRule.class::cast)
				.orElseThrow();
	}
}
