package org.jrba.environment.types;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interface that is used while defining environmental events
 */
@JsonSerialize(as = ImmutableEventType.class)
@JsonDeserialize(as = ImmutableEventType.class)
@Value.Immutable
public interface EventType {

	/**
	 * @return String type of the rule executed upon detection of given event
	 */
	String getRuleType();
}
