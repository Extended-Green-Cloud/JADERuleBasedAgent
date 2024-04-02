package org.jrba.rulesengine.mvel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.fixtures.TestRulesFixtures.prepareBehaviourRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCallForProposalRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareChainRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareCombinedRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareDefaultRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareMessageListenerRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.preparePeriodicRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareProposeRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRequestRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareScheduledRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareSearchRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareSingleMessageRuleRest;
import static org.jrba.fixtures.TestRulesFixtures.prepareSubscriptionRuleRest;
import static org.jrba.rulesengine.mvel.MVELRuleMapper.getRuleForType;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.stream.Stream;

import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.combined.AgentCombinedRule;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.rule.simple.AgentChainRule;
import org.jrba.rulesengine.rule.template.AgentCFPRule;
import org.jrba.rulesengine.rule.template.AgentMessageListenerRule;
import org.jrba.rulesengine.rule.template.AgentPeriodicRule;
import org.jrba.rulesengine.rule.template.AgentProposalRule;
import org.jrba.rulesengine.rule.template.AgentRequestRule;
import org.jrba.rulesengine.rule.template.AgentScheduledRule;
import org.jrba.rulesengine.rule.template.AgentSearchRule;
import org.jrba.rulesengine.rule.template.AgentSingleMessageListenerRule;
import org.jrba.rulesengine.rule.template.AgentSubscriptionRule;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class MVELRuleMapperUnitTest {

	private static Stream<Arguments> ruleTypeMapping() {
		return Stream.of(
				arguments(prepareScheduledRuleRest(), AgentScheduledRule.class),
				arguments(preparePeriodicRuleRest(), AgentPeriodicRule.class),
				arguments(prepareProposeRuleRest(), AgentProposalRule.class),
				arguments(prepareRequestRuleRest(), AgentRequestRule.class),
				arguments(prepareBehaviourRuleRest(), AgentBehaviourRule.class),
				arguments(prepareSearchRuleRest(), AgentSearchRule.class),
				arguments(prepareCallForProposalRuleRest(), AgentCFPRule.class),
				arguments(prepareSubscriptionRuleRest(), AgentSubscriptionRule.class),
				arguments(prepareSingleMessageRuleRest(), AgentSingleMessageListenerRule.class),
				arguments(prepareCombinedRuleRest(), AgentCombinedRule.class),
				arguments(prepareChainRuleRest(), AgentChainRule.class),
				arguments(prepareMessageListenerRuleRest(), AgentMessageListenerRule.class),
				arguments(prepareDefaultRuleRest(), AgentBasicRule.class)
		);
	}

	@ParameterizedTest
	@MethodSource("ruleTypeMapping")
	@DisplayName("Test mapper of different rest rules.")
	void testMVELGetSet(final RuleRest ruleRest, final Class<?> resultingRule) {
		final RuleSet ruleSet = prepareRuleSet();

		assertThat(getRuleForType(ruleRest, ruleSet)).isInstanceOf(resultingRule);
	}
}
