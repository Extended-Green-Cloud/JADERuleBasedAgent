package org.jrba.rulesengine.behaviour.fixtures.periodic;

import java.util.concurrent.atomic.AtomicInteger;

import org.jrba.agentmodel.domain.props.AgentProps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPeriodicAgentProps extends AgentProps {

	private String lastExecutedBehaviour;
	private AtomicInteger counter;

	public TestPeriodicAgentProps(String agentName) {
		super("PERIODIC_AGENT", agentName);
		this.lastExecutedBehaviour = "";
		this.counter = new AtomicInteger(0);
	}
}
