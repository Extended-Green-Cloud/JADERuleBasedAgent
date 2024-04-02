package org.jrba.rulesengine.ruleset.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.jrba.environment.types.EventTypeEnum.MODIFY_RULE_SET;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModifyAgentRuleSetEventUnitTest {

	@Test
	@DisplayName("Test initialize ModifyAgentRuleSetEvent.")
	void testModifyAgentRuleSetEventInitialize() {
		final RuleSet testRuleSet = prepareRuleSet();
		final ModifyAgentRuleSetEvent testEvent = new ModifyAgentRuleSetEvent(true, testRuleSet, "Test name");

		assertThat(testEvent.getOccurrenceTime()).isCloseTo(Instant.now(), within(500, ChronoUnit.MILLIS));
		assertEquals(true, testEvent.getReplaceFully());
		assertEquals(testRuleSet, testEvent.getNewRuleSet());
		assertEquals("Test name", testEvent.getAgentName());
		assertEquals(MODIFY_RULE_SET, testEvent.getEventType());
	}

	@Test
	@DisplayName("Test trigger ModifyAgentRuleSetEvent.")
	void testModifyAgentRuleSetEventTrigger() {
		final RuleSet testRuleSet = prepareRuleSet();
		final ModifyAgentRuleSetEvent testEvent = new ModifyAgentRuleSetEvent(true, testRuleSet, "Test name");
		final AgentNode<?> testNode = new TestAgentNodeDefault();

		final Map<String, AgentNode<?>> testNodes = Map.of("Test name", testNode);
		testEvent.trigger(testNodes);

		assertEquals(1, testNode.getEventsQueue().size());
		assertTrue(testNode.getEventsQueue().contains(testEvent));
	}
}
