package org.jrba.agentmodel.types;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interface that is used while defining agent properties
 */
public interface AgentType {

	/**
	 * @return name of agent type
	 */
	String getName();
}
