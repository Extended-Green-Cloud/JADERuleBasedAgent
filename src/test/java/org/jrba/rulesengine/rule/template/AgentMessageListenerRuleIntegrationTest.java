package org.jrba.rulesengine.rule.template;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.awaitility.Awaitility.await;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.rulesengine.constants.FactTypeConstants.MESSAGES;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.utils.mapper.JsonMapper.getMapper;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jrba.fixtures.TestAbstractAgentCustom2;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.fixtures.TestAgentPropsDefault;
import org.jrba.integration.jade.AgentContext;
import org.jrba.integration.jade.JADESystemContext;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.jrba.rulesengine.ruleset.RuleSetFacts;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.core.JsonProcessingException;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@ExtendWith(JADESystemContext.class)
public class AgentMessageListenerRuleIntegrationTest {

	@AgentContext(agentClass = "org.jrba.fixtures.TestAbstractAgentCustom2", agentName = "TestAgent")
	public TestAbstractAgentCustom2 testAgent;
	@AgentContext(agentClass = "org.jrba.fixtures.TestAbstractAgentCustom2", agentName = "TestAgentSender")
	public TestAbstractAgentCustom2 testAgentSender;

	@Test
	@DisplayName("Test execution of ReadMessagesRule for no messages matching template.")
	void testExecuteReadMessagesRuleWhenNoMessagesMatchingTemplate() throws JsonProcessingException {
		final AgentMessageListenerRule<?, ?> testRule = initializeDefaultListenerWithController();
		final AgentMessageListenerRule<?, ?>.ReadMessagesRule readRule = selectReadMessageRule(testRule);

		final ACLMessage testMsg = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(testAgent.getAID())
				.withStringContent("123")
				.build();

		testAgentSender.send(testMsg);
		await().timeout(5, SECONDS)
				.until(() -> testAgentSender.getSentMessagesCnt() == 1);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		readRule.executeRule(testFacts);
		final List<ACLMessage> result = testFacts.get(MESSAGES);

		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Test execution of ReadMessagesRule with messages matching template.")
	void testExecuteReadMessagesRuleWithMessagesMatchingTemplate() throws IOException {
		final AgentMessageListenerRule<?, ?> testRule = initializeDefaultListenerWithController();
		final AgentMessageListenerRule<?, ?>.ReadMessagesRule readRule = selectReadMessageRule(testRule);

		final ACLMessage testMsg = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(testAgent.getAID())
				.withObjectContent(testAgent.getAID())
				.build();

		testAgentSender.send(testMsg);
		await().timeout(5, SECONDS)
				.until(() -> testAgentSender.getSentMessagesCnt() == 1);

		final RuleSetFacts testFacts = new RuleSetFacts(0);
		readRule.executeRule(testFacts);
		final List<ACLMessage> result = testFacts.get(MESSAGES);

		await().timeout(10, SECONDS)
				.untilAsserted(() -> {
					assertThatCollection(result).hasSize(1);
					assertThat(result.getFirst())
							.usingRecursiveComparison()
							.ignoringFields("postTimeStamp")
							.isEqualTo(testMsg);
				});

	}

	private AgentMessageListenerRule<?, ?> initializeDefaultListenerWithController() throws JsonProcessingException {
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> testRulesController = new RulesController<>();
		testRulesController.setAgent(testAgent,
				new TestAgentPropsDefault("TestAgent"),
				new TestAgentNodeDefault(),
				DEFAULT_RULE_SET);

		final RuleSet testRuleSet = prepareRuleSet();
		final MessageTemplate testTemplate = MatchContent(getMapper().writeValueAsString(testAgent.getAID()));

		return new AgentMessageListenerRule<>(testRulesController, testRuleSet, AID.class, testTemplate, 10,
				"TEST_HANDLER_RULE_TYPE");
	}

	private AgentMessageListenerRule<?, ?>.ReadMessagesRule selectReadMessageRule(
			final AgentMessageListenerRule<?, ?> testRule) {
		return testRule.getRules().stream()
				.filter(rule -> rule.getClass().equals(AgentMessageListenerRule.ReadMessagesRule.class))
				.findFirst()
				.map(AgentMessageListenerRule.ReadMessagesRule.class::cast)
				.orElseThrow();
	}
}
