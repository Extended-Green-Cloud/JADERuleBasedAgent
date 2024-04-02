package org.jrba.fixtures;

import org.jrba.environment.types.EventType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestEventType implements EventType {

	TEST_EVENT_TYPE("Test rule type");

	final String ruleType;
}
