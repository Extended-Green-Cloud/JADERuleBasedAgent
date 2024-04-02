package org.jrba.rulesengine.rest;

import static java.lang.String.format;
import static org.jrba.rulesengine.constants.RuleSetTypeConstants.DEFAULT_RULE_SET;
import static org.jrba.utils.file.FileReader.readAllFiles;
import static org.jrba.utils.mapper.JsonMapper.getMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jrba.agentmodel.domain.node.AgentNode;
import org.jrba.exception.InvalidFileException;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.jrba.rulesengine.ruleset.RuleSet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.annotations.VisibleForTesting;

/**
 * REST API that should be run in order to initialize system rule set and listen for next ones.
 */
@SpringBootApplication
public class RuleSetRestApi {

	protected static Map<String, RuleSet> availableRuleSets = new HashMap<>();
	protected static List<AgentNode> agentNodes = new ArrayList<>();

	@VisibleForTesting
	protected static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		startSpring();
	}

	/**
	 * Method starts REST controller that listens for new rule sets.
	 *
	 * @param ruleSet default rule set added to the map under the name DEFAULT_RULE_SET
	 */
	public static void startRulesControllerRest(final RuleSet ruleSet) {
		startSpring();
		availableRuleSets.put(DEFAULT_RULE_SET, ruleSet);
	}

	/**
	 * Method starts REST controller that listens for new rule sets.
	 *
	 * @param ruleSet      default rule set added to the map under the name DEFAULT_RULE_SET
	 * @param ruleSetsPath path to the folder in /resources containing initial rule sets
	 */
	public static void startRulesControllerRest(final RuleSet ruleSet, final String ruleSetsPath) {
		startSpring();
		availableRuleSets.put(DEFAULT_RULE_SET, ruleSet);

		final List<File> ruleSetFiles = readAllFiles(ruleSetsPath);
		ruleSetFiles.forEach(file -> {
			final RuleSetRest nestRuleSet = parseRuleSetStructure(file);
			final RuleSet newRuleSet = new RuleSet(nestRuleSet);
			getAvailableRuleSets().put(newRuleSet.getName(), newRuleSet);
		});
	}

	public static void addAgentNode(final AgentNode newNode) {
		agentNodes.add(newNode);
	}

	public static Map<String, RuleSet> getAvailableRuleSets() {
		return availableRuleSets;
	}

	public static List<AgentNode> getAgentNodes() {
		return agentNodes;
	}

	private static void startSpring() {
		final SpringApplicationBuilder builder = new SpringApplicationBuilder(RuleSetRestApi.class);
		builder.headless(false);
		context = builder.run();

		availableRuleSets = new HashMap<>();
		agentNodes = new ArrayList<>();
	}

	private static RuleSetRest parseRuleSetStructure(final File ruleSetFile) {
		try {
			return getMapper().readValue(ruleSetFile, RuleSetRest.class);
		} catch (IOException e) {
			throw new InvalidFileException(format("Failed to parse rule set file \"%s\"", ruleSetFile));
		}
	}

}
