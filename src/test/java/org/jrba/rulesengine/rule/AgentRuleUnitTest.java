package org.jrba.rulesengine.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.jrba.fixtures.TestAgentRuleDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AgentRuleUnitTest {

	@Test
	@DisplayName("Test get default rules.")
	void testDefaultRules() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();

		assertThatCollection(testRule.getRules())
				.contains(testRule)
				.hasSize(1);
	}

	@Test
	@DisplayName("Test default rule evaluation.")
	void testDefaultEvaluateRule() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();
		final RuleSetFacts ruleSetFacts = new RuleSetFacts(0);

		assertThat(testRule.evaluate(ruleSetFacts)).isTrue();
	}

	@Test
	@DisplayName("Test default rule execution.")
	void testDefaultExecuteRule() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();
		final RuleSetFacts ruleSetFacts = new RuleSetFacts(0);

		assertThatNoException().isThrownBy(() -> testRule.executeRule(ruleSetFacts));
	}

	@Test
	@DisplayName("Test default connection of the rule with controller.")
	void testDefaultRuleControllerConnection() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();

		assertThatNoException().isThrownBy(() -> testRule.connectToController(mock(RulesController.class)));
	}

	@Test
	@DisplayName("Test default rule description.")
	void testDefaultRuleDescription() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();

		assertEquals("BASIC_RULE", testRule.initializeRuleDescription().ruleType());
		assertEquals("default rule definition", testRule.initializeRuleDescription().ruleDescription());
		assertEquals("basic agent rule", testRule.initializeRuleDescription().ruleName());
	}

	@Test
	@DisplayName("Test default copy function.")
	void testDefaultCopy() {
		final TestAgentRuleDefault testRule = new TestAgentRuleDefault();
		assertNull(testRule.copy());
	}
}
