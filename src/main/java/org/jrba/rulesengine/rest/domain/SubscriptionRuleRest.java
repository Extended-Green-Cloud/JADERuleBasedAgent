package org.jrba.rulesengine.rest.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionRuleRest extends RuleRest implements Serializable {

	String createSubscriptionMessage;
	String handleRemovedAgents;
	String handleAddedAgents;
}
