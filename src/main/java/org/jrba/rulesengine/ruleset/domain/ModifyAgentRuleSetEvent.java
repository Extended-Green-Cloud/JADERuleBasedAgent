package org.jrba.rulesengine.ruleset.domain;

import static org.jrba.environment.types.EventTypeEnum.MODIFY_RULE_SET;

import java.time.Instant;
import java.util.Map;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.environment.domain.ExternalEvent;
import org.jrba.rulesengine.ruleset.RuleSet;

import lombok.Getter;

@Getter
public class ModifyAgentRuleSetEvent extends ExternalEvent {

	private final Boolean replaceFully;
	private final RuleSet newRuleSet;

	/**
	 * Default event constructor
	 */
	public ModifyAgentRuleSetEvent(final Boolean replaceFully, final RuleSet newRuleSet, final String agentName) {
		super(agentName, MODIFY_RULE_SET, Instant.now());
		this.replaceFully = replaceFully;
		this.newRuleSet = newRuleSet;
	}

	@Override
	public <T extends AgentNode> void trigger(Map<String, T> agentNodes) {
		agentNodes.get(agentName).addEvent(this);
	}
}
