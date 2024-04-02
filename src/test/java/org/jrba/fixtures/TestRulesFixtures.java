package org.jrba.fixtures;

import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.rulesengine.enums.rulecombinationtype.AgentCombinedRuleTypeEnum.EXECUTE_FIRST;
import static org.jrba.rulesengine.enums.rulesteptype.RuleStepTypeEnum.REQUEST_CREATE_STEP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.BASIC;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.BEHAVIOUR;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.CFP;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.CHAIN;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.COMBINED;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.LISTENER;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.LISTENER_SINGLE;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.PERIODIC;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.PROPOSAL;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.REQUEST;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.SCHEDULED;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.SEARCH;
import static org.jrba.rulesengine.enums.ruletype.AgentRuleTypeEnum.SUBSCRIPTION;
import static org.jrba.rulesengine.mvel.MVELObjectType.MAP;

import java.util.List;
import java.util.Map;

import org.jrba.agentmodel.types.AgentTypeEnum;
import org.jrba.rulesengine.RulesController;
import org.jrba.rulesengine.rest.domain.BehaviourRuleRest;
import org.jrba.rulesengine.rest.domain.CallForProposalRuleRest;
import org.jrba.rulesengine.rest.domain.CombinedRuleRest;
import org.jrba.rulesengine.rest.domain.MessageListenerRuleRest;
import org.jrba.rulesengine.rest.domain.PeriodicRuleRest;
import org.jrba.rulesengine.rest.domain.ProposalRuleRest;
import org.jrba.rulesengine.rest.domain.RequestRuleRest;
import org.jrba.rulesengine.rest.domain.RuleRest;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.jrba.rulesengine.rest.domain.ScheduledRuleRest;
import org.jrba.rulesengine.rest.domain.SearchRuleRest;
import org.jrba.rulesengine.rest.domain.SingleMessageListenerRuleRest;
import org.jrba.rulesengine.rest.domain.SubscriptionRuleRest;
import org.jrba.rulesengine.ruleset.RuleSet;

public class TestRulesFixtures {

	public static RuleRest prepareDefaultRuleRest() {
		final RuleRest ruleRest = new RuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(BASIC.name());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"));

		return ruleRest;
	}

	public static RuleRest prepareChainRuleRest() {
		final RuleRest ruleRest = new RuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(CHAIN.name());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"));

		return ruleRest;
	}

	public static BehaviourRuleRest prepareBehaviourRuleRest() {
		final BehaviourRuleRest ruleRest = new BehaviourRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(BEHAVIOUR.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import org.jrba.fixtures.TestAgentBehaviourDefault;"));
		ruleRest.setBehaviours(List.of("return new TestAgentBehaviourDefault(agent);"));

		return ruleRest;
	}

	public static CombinedRuleRest prepareCombinedRuleRest() {
		final CombinedRuleRest ruleRest = new CombinedRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(COMBINED.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"));
		ruleRest.setCombinedRuleType(EXECUTE_FIRST);
		ruleRest.setRulesToCombine(List.of(prepareBehaviourRuleRest(), prepareDefaultRuleRest()));

		return ruleRest;
	}

	public static SubscriptionRuleRest prepareSubscriptionRuleRest() {
		final SubscriptionRuleRest ruleRest = new SubscriptionRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(SUBSCRIPTION.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import org.jrba.utils.messages.MessageBuilder;"
				+ "import jade.lang.acl.ACLMessage;"));
		ruleRest.setCreateSubscriptionMessage("return MessageBuilder.builder(0, ACLMessage.CFP)"
				+ ".withStringContent(\"123\")"
				+ ".build();");
		ruleRest.setHandleRemovedAgents("System.out.println(\"2\");");
		ruleRest.setHandleAddedAgents("System.out.println(\"3\");");

		return ruleRest;
	}

	public static SingleMessageListenerRuleRest prepareSingleMessageRuleRest() {
		final SingleMessageListenerRuleRest ruleRest = new SingleMessageListenerRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(LISTENER_SINGLE.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import jade.lang.acl.MessageTemplate;"));
		ruleRest.setConstructMessageTemplate("return MessageTemplate.MatchContent(\"123\");");
		ruleRest.setSpecifyExpirationTime("return 10;");
		ruleRest.setHandleMessageProcessing("System.out.println(\"1\");");
		ruleRest.setHandleMessageNotReceived("System.out.println(\"2\");");

		return ruleRest;
	}

	public static SearchRuleRest prepareSearchRuleRest() {
		final SearchRuleRest ruleRest = new SearchRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(SEARCH.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import java.util.Set;"
				+ "import jade.core.AID;"));
		ruleRest.setSearchAgents("return Set.of(new AID(\"TestAgent\", AID.ISGUID));");
		ruleRest.setHandleResults("for (agent : agents) { logger.info(agent.getName());} ");
		ruleRest.setHandleNoResults("System.out.println(\"2\");");

		return ruleRest;
	}

	public static ScheduledRuleRest prepareScheduledRuleRest() {
		final ScheduledRuleRest ruleRest = new ScheduledRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(SCHEDULED.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import java.time.Instant;"
				+ "import java.util.Date;"));
		ruleRest.setSpecifyTime("return Date.from(Instant.parse(\"2024-01-01T00:00:00.00Z\"));");
		ruleRest.setEvaluateBeforeTrigger("return false; ");
		ruleRest.setHandleActionTrigger("System.out.println(\"2\");");

		return ruleRest;
	}

	public static ProposalRuleRest prepareProposeRuleRest() {
		final ProposalRuleRest ruleRest = new ProposalRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(PROPOSAL.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import org.jrba.utils.messages.MessageBuilder;"
				+ "import jade.lang.acl.ACLMessage;"));
		ruleRest.setCreateProposalMessage("return MessageBuilder.builder(0, ACLMessage.PROPOSE)"
				+ ".withStringContent(\"123\")"
				+ ".build();");
		ruleRest.setHandleAcceptProposal("System.out.println(\"1\");");
		ruleRest.setHandleRejectProposal("System.out.println(\"2\");");

		return ruleRest;
	}

	public static PeriodicRuleRest preparePeriodicRuleRest() {
		final PeriodicRuleRest ruleRest = new PeriodicRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(PERIODIC.name());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"));
		ruleRest.setSpecifyPeriod("return 10;");
		ruleRest.setEvaluateBeforeTrigger("return false;");
		ruleRest.setHandleActionTrigger("System.out.println(\"1\");");

		return ruleRest;
	}

	public static RequestRuleRest prepareRequestRuleRest() {
		final RequestRuleRest ruleRest = new RequestRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(REQUEST.name());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import org.jrba.utils.messages.MessageBuilder;"
				+ "import jade.lang.acl.ACLMessage;"));
		ruleRest.setCreateRequestMessage("return MessageBuilder.builder(0, ACLMessage.REQUEST)"
				+ ".withStringContent(\"123\")"
				+ ".build();");
		ruleRest.setEvaluateBeforeForAll("return false;");
		ruleRest.setHandleInform("System.out.println(\"1\");");
		ruleRest.setHandleFailure("System.out.println(\"2\");");
		ruleRest.setHandleRefuse("System.out.println(\"3\");");
		ruleRest.setHandleAllResults("System.out.println(\"4\");");

		return ruleRest;
	}

	public static MessageListenerRuleRest prepareMessageListenerRuleRest() {
		final MessageListenerRuleRest ruleRest = new MessageListenerRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(LISTENER.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import jade.lang.acl.MessageTemplate;"));
		ruleRest.setClassName("java.time.Instant");
		ruleRest.setMessageTemplate("return MessageTemplate.MatchContent(\"123\");");
		ruleRest.setBatchSize(5);
		ruleRest.setActionHandler("TEST_REST_HANDLER");
		ruleRest.setSelectRuleSetIdx("return 5;");

		return ruleRest;
	}

	public static CallForProposalRuleRest prepareCallForProposalRuleRest() {
		final CallForProposalRuleRest ruleRest = new CallForProposalRuleRest();

		prepareDefaultRuleRest(ruleRest);
		ruleRest.setAgentRuleType(CFP.getType());
		ruleRest.setImports(List.of("import org.jrba.rulesengine.rest.domain.RuleRest;"
				+ "import org.jrba.utils.messages.MessageBuilder;"
				+ "import jade.lang.acl.ACLMessage;"));
		ruleRest.setCreateCFP("return MessageBuilder.builder(0, ACLMessage.CFP)"
				+ ".withStringContent(\"123\")"
				+ ".build();");
		ruleRest.setCompareProposals("return 1;");
		ruleRest.setHandleRejectProposal("System.out.println(\"1\");");
		ruleRest.setHandleNoProposals("System.out.println(\"2\");");
		ruleRest.setHandleNoResponses("System.out.println(\"3\");");
		ruleRest.setHandleProposals("System.out.println(\"4\");");

		return ruleRest;
	}

	public static RuleSetRest prepareRuleSetRest() {
		final RuleSetRest ruleSetRest = new RuleSetRest();

		ruleSetRest.setName("TestRuleSet");
		ruleSetRest.setRules(List.of(prepareBehaviourRuleRest(), prepareDefaultRuleRest()));

		return ruleSetRest;
	}

	public static RuleSetRest prepareRuleSetRestWithDifferentTypes() {
		final RuleSetRest ruleSetRest = new RuleSetRest();

		final RuleRest behaviourRule = prepareBehaviourRuleRest();
		behaviourRule.setType("TEST_TYPE_1");
		behaviourRule.setStepType(null);
		final RuleRest cfpRule = prepareCallForProposalRuleRest();
		cfpRule.setType("TEST_TYPE_1");
		final RuleRest combinedRule = prepareCombinedRuleRest();
		combinedRule.setType("TEST_TYPE_1");
		final RuleRest defaultRule = prepareDefaultRuleRest();
		defaultRule.setType("TEST_TYPE_2");

		ruleSetRest.setName("TestRuleSet");
		ruleSetRest.setRules(List.of(behaviourRule, cfpRule, combinedRule, defaultRule));

		return ruleSetRest;
	}

	public static RuleSet prepareRuleSetWithDifferentTypes() {
		return new RuleSet(prepareRuleSetRestWithDifferentTypes());
	}

	public static RuleSet prepareRuleSet() {
		return new RuleSet(prepareRuleSetRest());
	}

	public static RulesController<TestAgentPropsDefault, TestAgentNodeDefault> prepareRulesController() {
		final RulesController<TestAgentPropsDefault, TestAgentNodeDefault> rulesController = new RulesController<>();

		rulesController.setAgent(new TestAbstractAgentCustom(),
				new TestAgentPropsDefault("TestAgent"),
				new TestAgentNodeDefault(),
				DEFAULT_RULE_SET);

		return rulesController;
	}

	private static void prepareDefaultRuleRest(final RuleRest ruleRest) {
		ruleRest.setAgentType(AgentTypeEnum.BASIC.getName());
		ruleRest.setType("DEFAULT_RULE_TYPE");
		ruleRest.setSubType("DEFAULT_RULE_SUB_TYPE");
		ruleRest.setStepType(REQUEST_CREATE_STEP.getType());
		ruleRest.setPriority(1);
		ruleRest.setName("DEFAULT_NAME");
		ruleRest.setDescription("Example description.");
		ruleRest.setInitialParams(Map.of("exampleMap", MAP));
		ruleRest.setExecute("System.out.println(\":)\");");
		ruleRest.setEvaluate("true");
	}
}
