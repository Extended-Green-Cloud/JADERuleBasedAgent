package org.jrba.rulesengine.behaviour.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenForMessagesHandlerRule;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenForMessagesRule;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgentInitialRule;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.listener.TestListenerAgentProps;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgentInitialRule;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestPeriodicAgentProps;
import org.jrba.rulesengine.behaviour.fixtures.periodic.TestSchedulePeriodicallyForAgentRule;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgentInitialRule;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleAgentProps;
import org.jrba.rulesengine.behaviour.fixtures.schedule.TestScheduleForAgentRule;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentInitialRule;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchAgentProps;
import org.jrba.rulesengine.behaviour.fixtures.search.TestSearchForAgentRule;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestListenForSingleMessageRule;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgentInitialRule;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgentNode;
import org.jrba.rulesengine.behaviour.fixtures.singlelistener.TestSingleListenerAgentProps;
import org.jrba.rulesengine.rule.AgentRule;
import org.jrba.rulesengine.ruleset.RuleSet;

public class TestRuleSet extends RuleSet {

	public TestRuleSet() {
		super("DEFAULT_RULE_SET");
	}

	@Override
	protected List<AgentRule> initializeRules(final RulesController<?, ?> controller) {
		if (controller.getAgentProps().getAgentType().equals("SEARCH_AGENT")) {
			return new ArrayList<>(List.of(
					new TestSearchAgentInitialRule(
							(RulesController<TestSearchAgentProps, TestSearchAgentNode>) controller),
					new TestSearchForAgentRule(
							(RulesController<TestSearchAgentProps, TestSearchAgentNode>) controller)));
		} else if (controller.getAgentProps().getAgentType().equals("SCHEDULE_AGENT")) {
			return new ArrayList<>(List.of(
					new TestScheduleAgentInitialRule(
							(RulesController<TestScheduleAgentProps, TestScheduleAgentNode>) controller),
					new TestScheduleForAgentRule(
							(RulesController<TestScheduleAgentProps, TestScheduleAgentNode>) controller)));
		} else if (controller.getAgentProps().getAgentType().equals("PERIODIC_AGENT")) {
			return new ArrayList<>(List.of(
					new TestPeriodicAgentInitialRule(
							(RulesController<TestPeriodicAgentProps, TestPeriodicAgentNode>) controller),
					new TestSchedulePeriodicallyForAgentRule(
							(RulesController<TestPeriodicAgentProps, TestPeriodicAgentNode>) controller)));
		} else if (controller.getAgentProps().getAgentType().equals("SINGLE_LISTENER_AGENT")) {
			return new ArrayList<>(List.of(
					new TestSingleListenerAgentInitialRule(
							(RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode>) controller),
					new TestListenForSingleMessageRule(
							(RulesController<TestSingleListenerAgentProps, TestSingleListenerAgentNode>) controller)));
		} else if (controller.getAgentProps().getAgentType().equals("LISTENER_AGENT")) {
			return new ArrayList<>(List.of(
					new TestListenerAgentInitialRule(
							(RulesController<TestListenerAgentProps, TestListenerAgentNode>) controller),
					new TestListenForMessagesRule(
							(RulesController<TestListenerAgentProps, TestListenerAgentNode>) controller, this),
					new TestListenForMessagesHandlerRule(
							(RulesController<TestListenerAgentProps, TestListenerAgentNode>) controller)));
		}
		return new ArrayList<>();
	}
}
