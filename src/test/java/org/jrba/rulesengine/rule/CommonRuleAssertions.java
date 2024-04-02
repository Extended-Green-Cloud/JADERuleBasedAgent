package org.jrba.rulesengine.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_NODE;
import static org.jrba.rulesengine.constants.MVELParameterConstants.AGENT_PROPS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.FACTS;
import static org.jrba.rulesengine.constants.MVELParameterConstants.RULES_CONTROLLER;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.fixtures.TestAbstractAgentCustom;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.rulesengine.RulesController;

public class CommonRuleAssertions {

	public static void verifyDefaultRuleRestFields(final AgentBasicRule<?, ?> testRule) {
		assertTrue(testRule.isRuleStep());
		assertEquals("DEFAULT_NAME", testRule.getName());
		assertEquals("Example description.", testRule.getDescription());
		assertEquals("DEFAULT_RULE_TYPE", testRule.getRuleType());
		assertEquals("DEFAULT_RULE_SUB_TYPE", testRule.getSubRuleType());
		assertEquals(REQUEST_CREATE_STEP.getType(), testRule.getStepType());
		assertThat(testRule.getInitialParameters()).containsEntry("exampleMap", new HashMap<>());
		assertEquals(1, testRule.getPriority());
		assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
		assertNotNull(testRule.getExecuteExpression());
		assertNotNull(testRule.getEvaluateExpression());
		assertNull(testRule.getController());
		assertNull(testRule.getAgent());
		assertNull(testRule.getAgentProps());
		assertNull(testRule.getAgentNode());
	}

	public static void verifyRuleForRulesControllerFields(final AgentBasicRule<?, ?> testRule) {
		assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
		assertEquals(2147483646, testRule.getPriority());
		assertFalse(testRule.isRuleStep());
		assertNull(testRule.getSubRuleType());
		assertNull(testRule.getStepType());
		assertNull(testRule.getInitialParameters());
		assertNull(testRule.getExecuteExpression());
		assertNull(testRule.getEvaluateExpression());
		assertNotNull(testRule.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRule.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRule.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRule.getAgentNode());
	}

	public static void verifyRuleForRulesControllerFields(final AgentBasicRule<?, ?> testRule, final int priority) {
		assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
		assertEquals(priority, testRule.getPriority());
		assertFalse(testRule.isRuleStep());
		assertNull(testRule.getSubRuleType());
		assertNull(testRule.getStepType());
		assertNull(testRule.getInitialParameters());
		assertNull(testRule.getExecuteExpression());
		assertNull(testRule.getEvaluateExpression());
		assertNotNull(testRule.getController());
		assertInstanceOf(TestAbstractAgentCustom.class, testRule.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRule.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRule.getAgentNode());
	}

	public static void verifyDefaultRulesControllerConnection(final AgentBasicRule<?, ?> testRule,
			final RulesController<?, ?> testRulesController) {
		assertEquals(AgentTypeEnum.BASIC.getName(), testRule.getAgentType());
		assertInstanceOf(TestAbstractAgentCustom.class, testRule.getAgent());
		assertInstanceOf(TestAgentPropsDefault.class, testRule.getAgentProps());
		assertInstanceOf(TestAgentNodeDefault.class, testRule.getAgentNode());
		assertThat(testRule.getInitialParameters())
				.containsEntry(AGENT, testRulesController.getAgent())
				.containsEntry(AGENT_PROPS, testRulesController.getAgentProps())
				.containsEntry(AGENT_NODE, testRulesController.getAgentNode())
				.containsEntry(RULES_CONTROLLER, testRulesController)
				.containsEntry(FACTS, null);
	}
}
