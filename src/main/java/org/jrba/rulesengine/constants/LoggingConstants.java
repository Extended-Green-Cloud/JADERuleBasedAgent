package org.jrba.rulesengine.constants;

import static java.lang.String.valueOf;
import static org.jrba.rulesengine.constants.FactTypeConstants.RULE_SET_IDX;

import java.util.function.Function;

import org.jeasy.rules.api.Facts;

public class LoggingConstants {

	public static final String MDC_JOB_ID = "jobId";
	public static final String MDC_RULE_SET_ID = "ruleSetId";
	public static final String MDC_AGENT_NAME = "agentName";
	public static final String MDC_CLIENT_NAME = "clientName";

	public static final Function<Facts, String> getIdxFromFacts = (facts -> valueOf((int) facts.get(RULE_SET_IDX)));
}
