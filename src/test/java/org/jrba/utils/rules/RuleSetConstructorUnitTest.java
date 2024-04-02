package org.jrba.utils.rules;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareChainRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCombinedRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareDefaultRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.jrba.utils.rules.RuleSetConstructor.constructRuleSetWithName;
import static org.jrba.utils.rules.RuleSetConstructor.modifyBaseRuleSetWithName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.function.Predicate;

import org.jeasy.rules.core.DefaultRulesEngine;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.CombinedRuleRest;
import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.AgentRule;
import org.jrba.rulesengine.rule.combined.AgentCombinedRule;
import org.jrba.rulesengine.rule.template.AgentCFPRule;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RuleSetConstructorUnitTest {

	@BeforeEach
	void initialize() {
		getAvailableRuleSets().clear();
	}

	@Test
	@DisplayName("Test construct rule set with name when rule set not found.")
	void testConstructRuleSetWithNameWhenNotFound() {
		final RulesController<?, ?> rulesController = prepareRulesController();
		assertNull(constructRuleSetWithName("TestRuleSetName", rulesController));
	}

	@Test
	@DisplayName("Test construct rule set with name.")
	void testConstructRuleSetWithName() {
		final RuleSet testRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("TestRuleSetName", testRuleSet);

		final RulesController<?, ?> rulesController = prepareRulesController();
		final RuleSet result = constructRuleSetWithName("TestRuleSetName", rulesController);

		assertNotNull(result);
		assertEquals(rulesController, result.getRulesController());
		assertInstanceOf(DefaultRulesEngine.class, result.getRulesEngine());
		assertEquals("TestRuleSet", result.getName());
		assertThat(result.getAgentRules()).containsAll(testRuleSet.getAgentRules());
		assertFalse(result.isCallInitializeRules());
		assertThatCollection(result.getAgentRules())
				.allMatch((rule) -> ((AgentBasicRule<?, ?>) rule).getController().equals(rulesController));
	}

	@Test
	@DisplayName("Test modify rule set with name when rule set not found.")
	void testModifyRuleSetWithNameWhenNotFound() {
		final RulesController<?, ?> rulesController = prepareRulesController();
		assertNull(modifyBaseRuleSetWithName("TestRuleSetName", "TestModificationSetName", rulesController));

		final RuleSet testRuleSet = prepareRuleSet();
		getAvailableRuleSets().put("TestRuleSetName", testRuleSet);

		assertThat(modifyBaseRuleSetWithName("TestRuleSetName", "TestModificationSetName", rulesController))
				.usingRecursiveComparison()
				.ignoringFields("rulesController")
				.isEqualTo(testRuleSet);
	}

	@Test
	@DisplayName("Test modify rule set for no modifications.")
	void testModifyRuleSetNoModifications() {
		final RulesController<?, ?> rulesController = prepareRulesController();

		final RuleSet baseRuleRest = prepareBaseRuleSet();
		getAvailableRuleSets().put("TestRuleSetName", baseRuleRest);

		final RuleSetRest modificationRuleRest = new RuleSetRest();
		modificationRuleRest.setRules(emptyList());
		final RuleSet modificationRuleSet = new RuleSet(modificationRuleRest);
		getAvailableRuleSets().put("TestModificationSetName", modificationRuleSet);

		final RuleSet result = modifyBaseRuleSetWithName("TestRuleSetName", "TestModificationSetName", rulesController);

		assertThatCollection(result.getAgentRules()).containsAll(baseRuleRest.getAgentRules());
	}

	@Test
	@DisplayName("Test modify rule set.")
	void testModifyRuleSet() {
		final RulesController<?, ?> rulesController = prepareRulesController();

		final RuleSet baseRuleRest = prepareBaseRuleSet();
		getAvailableRuleSets().put("TestRuleSetName", baseRuleRest);

		final RuleSet modificationRuleSet = prepareModificationsSet();
		getAvailableRuleSets().put("TestModificationSetName", modificationRuleSet);

		final RuleSet result = modifyBaseRuleSetWithName("TestRuleSetName", "TestModificationSetName", rulesController);

		// Check changes to main rules
		final List<AgentRule> rulesThatShouldBeRemoved = baseRuleRest.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_BEHAVIOUR_TYPE") ||
						rule.getRuleType().equals("TEST_COMBINED_FULL_TYPE"))
				.toList();
		final List<AgentRule> rulesThatShouldBeAdded = modificationRuleSet.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_BEHAVIOUR_TYPE") ||
						rule.getRuleType().equals("TEST_COMBINED_FULL_TYPE"))
				.toList();

		// Test that rules were not removed from original rule set
		assertThatCollection(baseRuleRest.getAgentRules())
				.containsAll(rulesThatShouldBeRemoved);

		assertThatCollection(result.getAgentRules())
				.doesNotContainAnyElementsOf(rulesThatShouldBeRemoved)
				.containsAll(rulesThatShouldBeAdded);

		// Check nested changes to combination rule
		final Predicate<AgentRule> subRule = rule -> rule.getSubRuleType().equals("TEST_SUB_RULE_TYPE");

		final AgentCombinedRule<?, ?> exchangedCombinationRule = result.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_COMBINED_TYPE"))
				.map(AgentCombinedRule.class::cast)
				.findFirst()
				.orElseThrow();
		final AgentCombinedRule<?, ?> baseCombinationRule = baseRuleRest.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_COMBINED_TYPE"))
				.map(AgentCombinedRule.class::cast)
				.findFirst()
				.orElseThrow();
		final AgentRule removedNestedRule = baseCombinationRule.getRulesToCombine().stream().filter(subRule)
				.findFirst().orElseThrow();
		final AgentRule keptNestedRule = baseCombinationRule.getRulesToCombine().stream().filter(not(subRule))
				.findFirst().orElseThrow();
		final AgentRule addedNestedRule = modificationRuleSet.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_COMBINED_TYPE"))
				.findFirst()
				.orElseThrow();

		assertThatCollection(exchangedCombinationRule.getRulesToCombine())
				.doesNotContain(removedNestedRule)
				.contains(keptNestedRule, addedNestedRule);

		// Check nested changes to step rule
		final Predicate<AgentRule> stepRule = rule -> rule.getStepType().equals("CFP_HANDLE_NO_RESPONSES_STEP");

		final AgentCFPRule<?, ?> exchangedStepRule = result.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_CFP_TYPE"))
				.map(AgentCFPRule.class::cast)
				.findFirst()
				.orElseThrow();
		final AgentCFPRule<?, ?> baseStepRule = baseRuleRest.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_CFP_TYPE"))
				.map(AgentCFPRule.class::cast)
				.findFirst()
				.orElseThrow();
		final AgentRule removedCFPSubRule = baseStepRule.getRules().stream().filter(stepRule)
				.findFirst().orElseThrow();
		final AgentRule addedStepRule = modificationRuleSet.getAgentRules().stream()
				.filter(rule -> rule.getRuleType().equals("TEST_CFP_TYPE"))
				.findFirst()
				.orElseThrow();

		assertThatCollection(exchangedStepRule.getRules())
				.doesNotContain(removedCFPSubRule)
				.anySatisfy((rule) -> assertThat(rule).usingRecursiveComparison().isEqualTo(addedStepRule));

	}

	private RuleSet prepareModificationsSet() {
		final RuleSetRest ruleSetRest = new RuleSetRest();

		final RuleRest behaviourExchangeRule = prepareChainRuleRest();
		behaviourExchangeRule.setType("TEST_BEHAVIOUR_TYPE");
		behaviourExchangeRule.setStepType(null);

		final RuleRest cfpExchangeRule = prepareDefaultRuleRest();
		cfpExchangeRule.setType("TEST_CFP_TYPE");
		cfpExchangeRule.setStepType("CFP_HANDLE_NO_RESPONSES_STEP");

		final RuleRest defaultExchangeRule = prepareDefaultRuleRest();
		defaultExchangeRule.setType("TEST_COMBINED_TYPE");
		defaultExchangeRule.setSubType("TEST_SUB_RULE_TYPE");
		defaultExchangeRule.setStepType(null);

		final RuleRest defaultCombinedRule = prepareDefaultRuleRest();
		defaultCombinedRule.setType("TEST_COMBINED_FULL_TYPE");
		defaultCombinedRule.setSubType("TEST_SUB_RULE_TYPE_2");
		defaultCombinedRule.setStepType(null);

		final CombinedRuleRest combinedExchangeRule = prepareCombinedRuleRest();
		combinedExchangeRule.setType("TEST_COMBINED_FULL_TYPE");
		combinedExchangeRule.setRulesToCombine(List.of(defaultCombinedRule));
		combinedExchangeRule.setStepType(null);
		combinedExchangeRule.setSubType(null);

		ruleSetRest.setName("TestModificationSetName");
		ruleSetRest.setRules(
				List.of(behaviourExchangeRule, cfpExchangeRule, defaultExchangeRule, combinedExchangeRule));
		return new RuleSet(ruleSetRest);
	}

	private RuleSet prepareBaseRuleSet() {
		final RuleSetRest ruleSetRest = new RuleSetRest();

		final RuleRest behaviourRule = prepareBehaviourRuleRest();
		behaviourRule.setType("TEST_BEHAVIOUR_TYPE");
		behaviourRule.setStepType(null);

		final RuleRest cfpRule = prepareCallForProposalRuleRest();
		cfpRule.setType("TEST_CFP_TYPE");
		cfpRule.setStepType(null);

		final RuleRest ruleToExchange = prepareDefaultRuleRest();
		ruleToExchange.setType("TEST_COMBINED_TYPE");
		ruleToExchange.setSubType("TEST_SUB_RULE_TYPE");
		ruleToExchange.setStepType(null);

		final RuleRest ruleToLeave = prepareDefaultRuleRest();
		ruleToLeave.setType("TEST_COMBINED_TYPE");
		ruleToLeave.setSubType("TEST_SUB_RULE_TO_LEAVE");
		ruleToLeave.setStepType(null);

		final CombinedRuleRest combinedRule = prepareCombinedRuleRest();
		combinedRule.setType("TEST_COMBINED_TYPE");
		combinedRule.setStepType(null);
		combinedRule.setRulesToCombine(List.of(ruleToExchange, ruleToLeave));

		final CombinedRuleRest combinedFullRule = prepareCombinedRuleRest();
		combinedFullRule.setType("TEST_COMBINED_FULL_TYPE");
		combinedFullRule.setStepType(null);

		final RuleRest defaultRule = prepareDefaultRuleRest();
		defaultRule.setType("TEST_RULE_TO_LEAVE");
		defaultRule.setStepType(null);

		ruleSetRest.setName("TestRuleSetName");
		ruleSetRest.setRules(List.of(behaviourRule, cfpRule, combinedRule, combinedFullRule, defaultRule));
		return new RuleSet(ruleSetRest);
	}
}
