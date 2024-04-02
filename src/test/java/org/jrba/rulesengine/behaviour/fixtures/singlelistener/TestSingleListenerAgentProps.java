package org.jrba.rulesengine.behaviour.fixtures.singlelistener;

import java.util.concurrent.atomic.AtomicInteger;

import org.jrba.agentmodel.domain.props.AgentProps;

import jade.lang.acl.ACLMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestSingleListenerAgentProps extends AgentProps {

	private String lastExecutedBehaviour;
	private ACLMessage message;

	public TestSingleListenerAgentProps(String agentName) {
		super("SINGLE_LISTENER_AGENT", agentName);
		this.lastExecutedBehaviour = "";
	}
}
