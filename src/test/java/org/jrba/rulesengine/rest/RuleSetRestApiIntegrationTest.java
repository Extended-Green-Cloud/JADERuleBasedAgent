package org.jrba.rulesengine.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSet;
import static org.jrba.fixtures.TestRulesFixtures.prepareRuleSetRest;
import static org.jrba.rulesengine.rest.RuleSetRestApi.addAgentNode;
import static org.jrba.rulesengine.rest.RuleSetRestApi.context;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAgentNodes;
import static org.jrba.rulesengine.rest.RuleSetRestApi.getAvailableRuleSets;
import static org.jrba.utils.mapper.JsonMapper.getMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jeasy.rules.core.DefaultRulesEngine;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.jrba.rulesengine.rule.AgentBasicRule;
import org.jrba.rulesengine.rule.simple.AgentBehaviourRule;
import org.jrba.rulesengine.ruleset.domain.ModifyAgentRuleSetEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RuleSetRestApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void reset() {
		getAvailableRuleSets().clear();
		getAgentNodes().clear();
	}

	@Test
	@DisplayName("Test POST /ruleSet.")
	void testRuleSetPOST() throws Exception {
		final RuleSetRest testRuleSet = prepareRuleSetRest();

		assertTrue(getAvailableRuleSets().isEmpty());

		final ResultActions result = mockMvc.perform(post("/ruleSet")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getMapper().writeValueAsString(testRuleSet)));

		result.andExpect(status().isOk());
		assertThat(getAvailableRuleSets())
				.hasSize(1)
				.containsKey("TestRuleSet");
		assertThat(getAvailableRuleSets().get("TestRuleSet"))
				.satisfies(ruleSet -> {
					assertThat(ruleSet.getRulesEngine()).isInstanceOf(DefaultRulesEngine.class);
					assertNull(ruleSet.getRulesController());
					assertFalse(ruleSet.isCallInitializeRules());
					assertThatCollection(ruleSet.getAgentRules())
							.hasSize(2)
							.hasExactlyElementsOfTypes(AgentBehaviourRule.class, AgentBasicRule.class);
				});
	}

	@Test
	@DisplayName("Test PUT /ruleSet/modify when agent is not found.")
	void testRuleSetModifyPUTWithAgentNotFound() throws Exception {
		final RuleSetRest testRuleSet = prepareRuleSetRest();


		final ResultActions result = mockMvc.perform(put("/ruleSet/modify")
				.contentType(MediaType.APPLICATION_JSON)
				.param("agentName", "TestName")
				.param("replaceFully", "true")
				.content(getMapper().writeValueAsString(testRuleSet)));

		result.andExpect(status().isNotFound())
				.andExpect(content().string("Couldn't find an agent with given name."));
	}

	@Test
	@DisplayName("Test PUT /ruleSet/modify.")
	void testRuleSetModifyPUT() throws Exception {
		final RuleSetRest testRuleSet = prepareRuleSetRest();
		final TestAgentNodeDefault testAgentNode = new TestAgentNodeDefault();
		addAgentNode(testAgentNode);

		final ResultActions result = mockMvc.perform(put("/ruleSet/modify")
				.contentType(MediaType.APPLICATION_JSON)
				.param("agentName", "Test name")
				.param("replaceFully", "true")
				.content(getMapper().writeValueAsString(testRuleSet)));

		result.andExpect(status().isOk());
		assertEquals(1, testAgentNode.getEventsQueue().size());
		assertThat(testAgentNode.getEventsQueue().element()).isInstanceOf(ModifyAgentRuleSetEvent.class);
	}

	@Test
	@DisplayName("Test PUT /ruleSet/change when rule set is not found.")
	void testRuleSetChangePUTWithRuleSetNotFound() throws Exception {
		final ResultActions result = mockMvc.perform(put("/ruleSet/change")
				.contentType(MediaType.APPLICATION_JSON)
				.param("ruleSetName", "WrongName")
				.param("agentName", "TestName")
				.param("replaceFully", "true"));

		result.andExpect(status().isNotFound())
				.andExpect(content().string("Couldn't find a rule set with given name."));
	}

	@Test
	@DisplayName("Test PUT /ruleSet/change when agent is not found.")
	void testRuleSetChangePUTWithAgentNotFound() throws Exception {
		getAvailableRuleSets().put("TestRuleSet", prepareRuleSet());

		final ResultActions result = mockMvc.perform(put("/ruleSet/change")
				.contentType(MediaType.APPLICATION_JSON)
				.param("ruleSetName", "TestRuleSet")
				.param("agentName", "TestName")
				.param("replaceFully", "true"));

		result.andExpect(status().isNotFound())
				.andExpect(content().string("Couldn't find an agent with given name."));
	}

	@Test
	@DisplayName("Test PUT /ruleSet/change.")
	void testRuleSetChangePUT() throws Exception {
		getAvailableRuleSets().put("TestRuleSet", prepareRuleSet());
		final TestAgentNodeDefault testAgentNode = new TestAgentNodeDefault();
		addAgentNode(testAgentNode);

		final ResultActions result = mockMvc.perform(put("/ruleSet/change")
				.contentType(MediaType.APPLICATION_JSON)
				.param("ruleSetName", "TestRuleSet")
				.param("agentName", "Test name")
				.param("replaceFully", "true"));

		result.andExpect(status().isOk());
		assertEquals(1, testAgentNode.getEventsQueue().size());
		assertThat(testAgentNode.getEventsQueue().element()).isInstanceOf(ModifyAgentRuleSetEvent.class);
	}
}
