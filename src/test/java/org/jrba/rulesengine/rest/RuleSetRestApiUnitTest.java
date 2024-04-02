package org.jrba.rulesengine.rest;

import static java.util.Objects.nonNull;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.rest.RuleSetRestApi.context;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAgentNodes;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jrba.fixtures.TestAgentNodeDefault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RuleSetRestApiUnitTest {

	@BeforeEach
	void initialize() {
		clearSpring();
	}

	@AfterAll
	static void reset() {
		clearSpring();
	}

	@Test
	@DisplayName("Test run Spring application.")
	void testRunningApplication() {
		getAvailableRuleSets().put("TestRuleSet", prepareRuleSet());
		getAgentNodes().add(new TestAgentNodeDefault());

		RuleSetRestApi.main(new String[] {});

		assertTrue(getAvailableRuleSets().isEmpty());
		assertTrue(getAgentNodes().isEmpty());
	}

	@Test
	@DisplayName("Test run Spring application with initial rule set.")
	void testRunningApplicationWithInitialRuleSet() {
		assertTrue(getAvailableRuleSets().isEmpty());
		assertTrue(getAgentNodes().isEmpty());

		RuleSetRestApi.startRulesControllerRest(prepareRuleSet());

		assertTrue(getAvailableRuleSets().containsKey(DEFAULT_RULE_SET));
		assertTrue(getAgentNodes().isEmpty());
	}

	@Test
	@DisplayName("Test run Spring application reading initial directory.")
	void testRunningApplicationReadingInitialDirectory() {
		assertTrue(getAvailableRuleSets().isEmpty());
		assertTrue(getAgentNodes().isEmpty());

		RuleSetRestApi.startRulesControllerRest(prepareRuleSet(), "test-rulesets");

		assertEquals(2, getAvailableRuleSets().size());
		assertTrue(getAvailableRuleSets().containsKey("TEST_RULE_SET"));
		assertTrue(getAvailableRuleSets().containsKey(DEFAULT_RULE_SET));
		assertTrue(getAgentNodes().isEmpty());
	}

	private static void clearSpring() {
		if (nonNull(context) && context.isRunning()) {
			context.close();
		}
		getAvailableRuleSets().clear();
		getAgentNodes().clear();
	}
}
