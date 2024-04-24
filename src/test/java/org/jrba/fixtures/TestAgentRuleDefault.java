package org.jrba.fixtures;

import static org.jrba.agentmodel.types.AgentTypeEnum.BASIC;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jrba.rulesengine.types.ruletype.AgentRuleTypeEnum;
import org.jrba.rulesengine.rule.AgentRule;

public class TestAgentRuleDefault implements AgentRule {

	@Override
	public String getAgentType() {
		return BASIC.getName();
	}

	@Override
	public String getAgentRuleType() {
		return AgentRuleTypeEnum.BASIC.getType();
	}

	@Override
	public String getRuleType() {
		return "DEFAULT_RULE_TYPE";
	}

	@Override
	public String getSubRuleType() {
		return "DEFAULT_SUB_RULE_TYPE";
	}

	@Override
	public String getStepType() {
		return null;
	}

	@Override
	public boolean isRuleStep() {
		return false;
	}

	@Override
	public boolean evaluate(final Facts facts) {
		return true;
	}

	@Override
	public void execute(final Facts facts) {

	}

	@Override
	public int compareTo(Rule o) {
		return 0;
	}
}
