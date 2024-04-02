package org.jrba.rulesengine.behaviour.fixtures.search;

import java.util.HashSet;
import java.util.Set;

import org.jrba.agentmodel.domain.props.AgentProps;

import jade.core.AID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestSearchAgentProps extends AgentProps {

	private String lastExecutedBehaviour;
	private Set<AID> foundResults;

	public TestSearchAgentProps(String agentName) {
		super("SEARCH_AGENT", agentName);
		this.lastExecutedBehaviour = "";
		this.foundResults = new HashSet<>();
	}
}
