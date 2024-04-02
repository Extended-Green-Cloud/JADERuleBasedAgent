package org.jrba.rulesengine.behaviour.fixtures;

import org.jrba.agentmodel.domain.AbstractAgent;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestServiceAgent extends AbstractAgent<TestAgentNodeDefault, TestAgentPropsDefault> {

	public TestServiceAgent() {
	}

}
