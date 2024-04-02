package org.jrba.rulesengine.rule;

import static java.util.Collections.singletonList;
import static org.jrba.rulesengine.constants.RuleTypeConstants.BASIC_RULE;

import java.util.List;

import org.jeasy.rules.api.Rule;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.ruleset.RuleSetFacts;

/**
 * Interface representing common agent rule.
 * It is implemented by all types of agent rules.
 */
public interface AgentRule extends Rule {

	String getAgentType();

	String getAgentRuleType();

	String getRuleType();

	String getSubRuleType();

	String getStepType();

	boolean isRuleStep();

	/**
	 * Method returns constructed rule
	 *
	 * @return constructed rule Object
	 */
	default List<AgentRule> getRules() {
		return singletonList(this);
	}

	/**
	 * Method performs default evaluation of rule conditions
	 *
	 * @param facts facts used in evaluation
	 * @return boolean indicating if conditions are met
	 */
	default boolean evaluateRule(RuleSetFacts facts) {
		return true;
	}

	/**
	 * Method executes given rule
	 *
	 * @param facts facts used in evaluation
	 */
	default void executeRule(RuleSetFacts facts) {
	}

	/**
	 * Method initialize default rule metadata
	 *
	 * @return rule description
	 */
	default AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(BASIC_RULE, "basic agent rule", "default rule definition");
	}

	/**
	 * Method connects agent rule with controller
	 *
	 * @param rulesController rules controller connected to the agent
	 */
	default void connectToController(final RulesController<?, ?> rulesController) {
	}

	/**
	 * Method clones a given rule to a new object instance
	 * @return cloned rule
	 */
	default AgentRule copy() {
		return null;
	}
}
