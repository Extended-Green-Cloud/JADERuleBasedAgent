package org.jrba.rulesengine.behaviour.fixtures.listener;

import java.util.List;

import org.jrba.agentmodel.domain.props.AgentProps;

import jade.lang.acl.ACLMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestListenerAgentProps extends AgentProps {

	private String lastExecutedBehaviour;
	private List<ACLMessage> messages;

	public TestListenerAgentProps(String agentName) {
		super("LISTENER_AGENT", agentName);
		this.lastExecutedBehaviour = "";
	}
}
