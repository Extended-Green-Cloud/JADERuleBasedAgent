package org.jrba.fixtures;

import org.mockito.ArgumentMatcher;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TestAgentDescriptionMatchers {

	public static final ArgumentMatcher<DFAgentDescription> matchDescription = (desc) -> {
		final ServiceDescription serviceDesc = (ServiceDescription) desc.getAllServices().next();
		return serviceDesc.getType().equals("test_service_type");
	};

	public static final ArgumentMatcher<DFAgentDescription> matchDescriptionWithName = (desc) -> {
		final ServiceDescription serviceDesc = (ServiceDescription) desc.getAllServices().next();
		return serviceDesc.getName().equals("test_service_name") &&
				serviceDesc.getType().equals("test_service_type");
	};

	public static final ArgumentMatcher<DFAgentDescription> matchDescriptionWithOwnership = (desc) -> {
		final ServiceDescription serviceDesc = (ServiceDescription) desc.getAllServices().next();
		return serviceDesc.getName().equals("test_service_name") &&
				serviceDesc.getType().equals("test_service_type") &&
				serviceDesc.getOwnership().equals("test_agent");
	};

}
