package org.jrba.rulesengine.rule;

import org.jrba.rulesengine.enums.rulesteptype.RuleStepType;

/**
 * Class storing common properties which describe a given rule
 *
 * @param ruleType        type of the rule
 * @param subType         secondary type used in combined rules
 * @param stepType        optional type of rule step
 * @param ruleName        name of the rule
 * @param ruleDescription description of the rule
 */
public record AgentRuleDescription(String ruleType, String subType, String stepType, String ruleName,
								   String ruleDescription) {

	public AgentRuleDescription(final String ruleType, final String ruleName, final String ruleDescription) {
		this(ruleType, null, null, ruleName, ruleDescription);
	}

	public AgentRuleDescription(final String ruleType, final String subType, final String ruleName,
			final String ruleDescription) {
		this(ruleType, subType, null, ruleName, ruleDescription);
	}

	public AgentRuleDescription(final String ruleType, final RuleStepType stepType, final String ruleName,
			final String ruleDescription) {
		this(ruleType, null, stepType.getType(), ruleName, ruleDescription);
	}
}
