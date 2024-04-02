package org.jrba.rulesengine.behaviour.fixtures.search;

import static org.jrba.rulesengine.constants.RuleTypeConstants.INITIALIZE_BEHAVIOURS_RULE;

import java.util.HashSet;
import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.search.SearchForAgents;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.core.behaviours.Behaviour;

public class TestSearchAgentInitialRule extends AgentBehaviourRule<TestSearchAgentProps, TestSearchAgentNode> {

	public TestSearchAgentInitialRule(final RulesController<TestSearchAgentProps, TestSearchAgentNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize set of behaviours that are to be added
	 */
	@Override
	protected Set<Behaviour> initializeBehaviours() {
		return new HashSet<>(
				Set.of(SearchForAgents.create(agent, new RuleSetFacts(0), "TEST_SEARCH_RULE", controller)));
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(INITIALIZE_BEHAVIOURS_RULE,
				"initial behaviour rule TestSearchAgent",
				"initializes TestSearchAgent behaviours");
	}
}
