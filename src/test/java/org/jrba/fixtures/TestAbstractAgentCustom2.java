package org.jrba.fixtures;

import java.util.List;
import java.util.function.Consumer;

import org.jrba.agentmodel.domain.AbstractAgent;

import jade.core.behaviours.Behaviour;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestAbstractAgentCustom2 extends AbstractAgent<TestAgentNodeDefault, TestAgentPropsDefault> {

	private String lastExecutedBehaviour;
	private Consumer<List<Behaviour>> customInitializer = (behaviours -> lastExecutedBehaviour = "CustomInitialization");

	public TestAbstractAgentCustom2() {
		this.lastExecutedBehaviour = "";
	}

}
