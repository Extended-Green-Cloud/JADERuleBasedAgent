package org.jrba.utils.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.utils.mapper.FactsMapper.mapToRuleSetFacts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FactsMapperUnitTest {

	static final int mockIndex = 0;
	static final String testFactName = "test-fact";
	static final String testFactValue = "fact-value";

	@Test
	@DisplayName("Test map to rule set facts from rule set facts.")
	void testMapFromRuleSetFactsToRuleSetFacts() {
		final RuleSetFacts ruleSetFacts = new RuleSetFacts(mockIndex);
		ruleSetFacts.put(testFactName, testFactValue);

		var result = mapToRuleSetFacts(ruleSetFacts);

		assertNotEquals(ruleSetFacts, result);
		assertThat(result.asMap()).containsExactly(
				Map.entry("rule-set-idx", 0),
				Map.entry(testFactName, testFactValue));
	}

	@Test
	@DisplayName("Test map to rule set facts from facts.")
	void testMapFromFactsToRuleSetFacts() {
		final Facts ruleSetFacts = new Facts();
		ruleSetFacts.put(testFactName, testFactValue);
		ruleSetFacts.put("rule-set-idx", 0);

		var result = mapToRuleSetFacts(ruleSetFacts);

		assertNotEquals(ruleSetFacts, result);
		assertThat(result.asMap()).containsExactly(
				Map.entry("rule-set-idx", 0),
				Map.entry(testFactName, testFactValue));
	}
}
