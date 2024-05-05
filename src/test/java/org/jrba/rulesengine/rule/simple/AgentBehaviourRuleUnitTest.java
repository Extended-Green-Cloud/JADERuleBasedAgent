package org.jrba.rulesengine.rule.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.LOGGER;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;
import static org.jrba.rulesengine.types.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum.BEHAVIOUR;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyDefaultRuleRestFields;
import static org.jrba.rulesengine.rule.CommonRuleAssertions.verifyRuleForRulesControllerFields;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.BehaviourRuleRest;
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
class AgentBehaviourRuleUnitTest {

	@Test
	@DisplayName("Test initialize AgentBehaviourRule with controller.")
	void testInitializeAgentBehaviourRuleFromRuleController() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRulesController);

		verifyRuleForRulesControllerFields(testRule);
		assertEquals("initialize agent behaviours", testRule.getName());
		assertEquals("when rule set is selected and agent is set-up, it adds set of default behaviours",
				testRule.getDescription());
		assertEquals(INITIALIZE_BEHAVIOURS_RULE, testRule.getRuleType());
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test initialize AgentBehaviourRule with RuleRest.")
	void testInitializeAgentBehaviourRuleFromRuleRest() {
		final BehaviourRuleRest testRuleRest = prepareBehaviourRuleRest();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRuleRest);

		verifyDefaultRuleRestFields(testRule);
		assertThatCollection(testRule.expressionsBehaviours).hasSize(1);
		assertNull(testRule.getStepRules());
	}

	@Test
	@DisplayName("Test copy of AgentBehaviourRule.")
	void testCopyOfAgentBehaviourRule() {
		final BehaviourRuleRest testRuleRest = prepareBehaviourRuleRest();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRuleRest);
		final RulesController<?, ?> testRulesController = prepareRulesController();
		testRule.connectToController(testRulesController);

		final AgentBehaviourRule<?, ?> testRuleCopy = (AgentBehaviourRule<?, ?>) testRule.copy();

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
		assertNull(testRuleCopy.getStepRules());
		assertThatCollection(testRuleCopy.expressionsBehaviours).hasSize(1);
	}

	@Test
	@DisplayName("Test initialize AgentBehaviourRule behaviours.")
	void testInitializeBehavioursOfAgentBehaviourRule() {
		final BehaviourRuleRest testRuleRest = prepareBehaviourRuleRest();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRuleRest);

		assertThatCollection(testRule.initializeBehaviours()).isEmpty();
	}

	@Test
	@DisplayName("Test get AgentBehaviourRule rule type.")
	void testGetAgentBehaviourRuleRuleType() {
		final BehaviourRuleRest testRuleRest = prepareBehaviourRuleRest();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRuleRest);

		assertEquals(BEHAVIOUR.getType(), testRule.getAgentRuleType());
	}

	@Test
	@DisplayName("Test execute without initial parameters.")
	void testExecuteWithoutInitialParam() {
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRulesController);

		try (MockedStatic<MVEL> mvlMock = mockStatic(MVEL.class)) {
			assertDoesNotThrow(() -> testRule.executeRule(null));
			mvlMock.verifyNoInteractions();
		}
	}

	@Test
	@DisplayName("Test execute with initial parameters.")
	void testExecuteWithInitialParam() {
		final BehaviourRuleRest testRuleRest = prepareBehaviourRuleRest();
		final RulesController<?, ?> testRulesController = prepareRulesController();
		final AgentBehaviourRule<?, ?> testRule = new AgentBehaviourRule<>(testRuleRest);
		testRule.connectToController(testRulesController);

		final RuleSetFacts testFacts = new RuleSetFacts(0);

		assertDoesNotThrow(() -> testRule.executeRule(testFacts));
		assertEquals(testFacts, testRule.getInitialParameters().get(FACTS));
		assertEquals(1, testRulesController.getAgent().getBehavioursCnt());
	}
}
