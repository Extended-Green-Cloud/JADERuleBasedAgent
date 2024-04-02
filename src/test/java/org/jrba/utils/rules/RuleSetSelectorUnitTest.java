package org.jrba.utils.rules;

import static org.jrba.fixtures.TestRulesFixtures.prepareRulesController;
import static org.jrba.utils.rules.RuleSetSelector.SELECT_BY_FACTS_IDX;
import static org.jrba.utils.rules.RuleSetSelector.SELECT_LATEST;
import static org.jrba.utils.rules.RuleSetSelector.selectRuleSetIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RuleSetSelectorUnitTest {

	@Test
	@DisplayName("Test select by facts identifier.")
	void testSelectByFactsId() {
		final RulesController<?, ?> rulesController = prepareRulesController();
		final RuleSetFacts testFacts = new RuleSetFacts(1);

		assertEquals(1, selectRuleSetIndex(SELECT_BY_FACTS_IDX, rulesController).applyAsInt(testFacts));
	}

	@Test
	@DisplayName("Test select by controller.")
	void testSelectByController() {
		final RulesController<?, ?> rulesController = prepareRulesController();
		final RuleSetFacts testFacts = new RuleSetFacts(1);

		assertEquals(0, selectRuleSetIndex(SELECT_LATEST, rulesController).applyAsInt(testFacts));
	}
}
