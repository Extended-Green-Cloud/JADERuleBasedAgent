package org.jrba.rulesengine.behaviour.fixtures.schedule;

import org.jrba.agentmodel.domain.props.AgentProps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestScheduleAgentProps extends AgentProps {

	private String lastExecutedBehaviour;

	public TestScheduleAgentProps(String agentName) {
		super("SCHEDULE_AGENT", agentName);
		this.lastExecutedBehaviour = "";
	}
}
