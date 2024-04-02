package org.jrba.rulesengine.behaviour.fixtures.search;

import static org.jrba.utils.yellowpages.YellowPagesRegister.search;

import java.util.Set;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rule.AgentRuleDescription;
import org.jrba.rulesengine.rule.template.AgentSearchRule;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

import jade.core.AID;

public class TestSearchForAgentRule extends AgentSearchRule<TestSearchAgentProps, TestSearchAgentNode> {

	public TestSearchForAgentRule(final RulesController<TestSearchAgentProps, TestSearchAgentNode> controller) {
		super(controller);
	}

	@Override
	protected Set<AID> searchAgents(RuleSetFacts facts) {
		return search(agent, agent.getDefaultDF(), "TEST_SERVICE");
	}

	@Override
	protected void handleNoResults(RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("NO_RESULTS_EXECUTED");
	}

	@Override
	protected void handleResults(Set<AID> dfResults, RuleSetFacts facts) {
		agentProps.setLastExecutedBehaviour("AGENT_FOUND_EXECUTED");
		agentProps.setFoundResults(dfResults);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("TEST_SEARCH_RULE",
				"searching for TestServiceAgent",
				"handle search for TestServiceAgent");
	}
}
