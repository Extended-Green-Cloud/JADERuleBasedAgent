package org.jrba.rulesengine.rule.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.fixtures.TestRulesFixtures.prepareSearchRuleRest;
import static org.jrba.rulesengine.constants.FactTypeConstants.RESULT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.DEFAULT_SEARCH_RULE;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.SEARCH_AGENTS_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.SEARCH_HANDLE_NO_RESULTS_STEP;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.SEARCH_HANDLE_RESULTS_STEP;
import static org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum.SEARCH;
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
import java.util.Set;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.SearchRuleRest;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mvel2.MVEL;

import jade.core.AID;

class AgentSearchRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentSearchRule with controller.")
	void testInitializeAgentSearchRuleWithController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("default search rule", testRule.getName());
		assertEquals("default implementation of a rule that searches for pre-defined agent services",
				testRule.getDescription());
		assertEquals(DEFAULT_SEARCH_RULE, testRule.getRuleType());
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentSearchRule.SearchForAgentsRule.class,
						AgentSearchRule.NoResultsRule.class,
						AgentSearchRule.AgentsFoundRule.class);
	}

	@Test
	@DisplayName("Test initialize AgentSearchRule with rule rest.")
	void testInitializeAgentSearchRuleWithRuleRest() {
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentSearchRule.SearchForAgentsRule.class,
						AgentSearchRule.NoResultsRule.class,
						AgentSearchRule.AgentsFoundRule.class);
		assertNotNull(testRule.expressionSearchAgents);
		assertNotNull(testRule.expressionHandleNoResults);
		assertNotNull(testRule.expressionHandleResults);
	}

	@Test
	@DisplayName("Test copy of AgentSearchRule.")
	void testCopyOfAgentSearchRule() {
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentSearchRule<?, ?> testRuleCopy = (AgentSearchRule<?, ?>) testRule.copy();

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
		assertNotNull(testRuleCopy.expressionSearchAgents);
		assertNotNull(testRuleCopy.expressionHandleNoResults);
		assertNotNull(testRuleCopy.expressionHandleResults);
		assertEquals(testRulesController, testRuleCopy.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRuleCopy.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRuleCopy.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRuleCopy.getAgentNode());
		assertThatCollection(testRuleCopy.getInitialParameters().keySet())
				.contains(AGENT, AGENT_PROPS, AGENT_NODE, RULES_CONTROLLER, LOGGER, FACTS);
		assertThatCollection(testRuleCopy.getRules())
				.hasSize(3)
				.hasExactlyElementsOfTypes(AgentSearchRule.SearchForAgentsRule.class,
						AgentSearchRule.NoResultsRule.class,
						AgentSearchRule.AgentsFoundRule.class);
	}

	@Test
	@DisplayName("Test connection to controller")
	void testConnectToController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);

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
	@DisplayName("Test get AgentSearchRule rule type.")
	void testGetAgentSearchRuleRuleType() {
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);

		assertEquals(SEARCH.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test initialize SearchForAgentsRule description.")
	void testInitializeSearchForAgentsRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.SearchForAgentsRule searchRule = selectSearchForAgentRule(testRule);

		final AgentRuleDescription description = searchRule.initializeRuleDescription();

		assertEquals(DEFAULT_SEARCH_RULE, description.ruleType());
		assertEquals(SEARCH_AGENTS_STEP.getType(), description.stepType());
		assertEquals("default search rule - search for agents", description.ruleName());
		assertEquals("rule performed when searching for agents in DF", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize NoResultsRule description.")
	void testInitializeNoResultsRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.NoResultsRule searchRule = selectNoResultsRule(testRule);

		final AgentRuleDescription description = searchRule.initializeRuleDescription();

		assertEquals(DEFAULT_SEARCH_RULE, description.ruleType());
		assertEquals(SEARCH_HANDLE_NO_RESULTS_STEP.getType(), description.stepType());
		assertEquals("default search rule - no results", description.ruleName());
		assertEquals("rule that handles case when no DF results were retrieved", description.ruleDescription());
	}

	@Test
	@DisplayName("Test initialize AgentsFoundRule description.")
	void testInitializeAgentsFoundRuleDescription() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.AgentsFoundRule searchRule = selectAgentsFoundRule(testRule);

		final AgentRuleDescription description = searchRule.initializeRuleDescription();

		assertEquals(DEFAULT_SEARCH_RULE, description.ruleType());
		assertEquals(SEARCH_HANDLE_RESULTS_STEP.getType(), description.stepType());
		assertEquals("default search rule - agents found", description.ruleName());
		assertEquals("rule triggerred when DF returned set of agents", description.ruleDescription());
	}

	@Test
	@DisplayName("Test execution of SearchForAgentsRule for rest initialization.")
	void testExecuteSearchForAgentsRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSearchRule<?, ?>.SearchForAgentsRule searchRule = selectSearchForAgentRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		searchRule.executeRule(testFacts);

		final Set<AID> result = testFacts.get(RESULT);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertThat(result)
				.hasSize(1)
				.allMatch(aid -> aid.getName().equals("TestAgent"));
	}

	@Test
	@DisplayName("Test execution of SearchForAgentsRule for controller initialization.")
	void testExecuteSearchForAgentsRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.SearchForAgentsRule searchRule = selectSearchForAgentRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		searchRule.executeRule(testFacts);
		final Set<AID> result = testFacts.get(RESULT);

		assertNull(testRule.getInitialParameters());
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Test execution of NoResultsRule for rest initialization.")
	void testExecuteNoResultsRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSearchRule<?, ?>.NoResultsRule noResultsRule = selectNoResultsRule(testRule);
		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			noResultsRule.executeRule(testFacts);

			assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
			mvlMock.verify(() -> MVEL.executeExpression(testRule.expressionHandleNoResults,
					testRule.getInitialParameters()));
		}
	}

	@Test
	@DisplayName("Test execution of NoResultsRule for controller initialization.")
	void testExecuteNoResultsRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.NoResultsRule noResultsRule = selectNoResultsRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			noResultsRule.executeRule(testFacts);

			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execution of AgentsFoundRule for rest initialization.")
	void testExecuteAgentsFoundRuleWithRestInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final SearchRuleRest ruleRest = prepareSearchRuleRest();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(ruleRest);
		testRule.connectToController(testRulesController);

		final AgentSearchRule<?, ?>.AgentsFoundRule agentsFoundRule = selectAgentsFoundRule(testRule);
		final RuleSetFacts testFacts = new RuleSetFacts(0);
		testFacts.put(RESULT, Set.of(new AID("TestAgent", AID.ISGUID)));

		agentsFoundRule.executeRule(testFacts);

		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertFalse(testRule.getInitialParameters().containsKey(AGENTS));
	}

	@Test
	@DisplayName("Test execution of AgentsFoundRule for controller initialization.")
	void testExecuteAgentsFoundRuleWithControllerInitialization() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentSearchRule<?, ?> testRule = new AgentSearchRule<>(testRulesController);
		final AgentSearchRule<?, ?>.AgentsFoundRule agentsFoundRule = selectAgentsFoundRule(testRule);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			agentsFoundRule.executeRule(testFacts);

			assertNull(testRule.getInitialParameters());
			mvlMock.verifyNoInteractions();
		}
	}

	private AgentSearchRule<?, ?>.SearchForAgentsRule selectSearchForAgentRule(final AgentSearchRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentSearchRule.SearchForAgentsRule.class))
				.findFirst()
				.map(AgentSearchRule.SearchForAgentsRule.class::cast)
				.orElseThrow();
	}

	private AgentSearchRule<?, ?>.NoResultsRule selectNoResultsRule(final AgentSearchRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentSearchRule.NoResultsRule.class))
				.findFirst()
				.map(AgentSearchRule.NoResultsRule.class::cast)
				.orElseThrow();
	}

	private AgentSearchRule<?, ?>.AgentsFoundRule selectAgentsFoundRule(final AgentSearchRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentSearchRule.AgentsFoundRule.class))
				.findFirst()
				.map(AgentSearchRule.AgentsFoundRule.class::cast)
				.orElseThrow();
	}
}
