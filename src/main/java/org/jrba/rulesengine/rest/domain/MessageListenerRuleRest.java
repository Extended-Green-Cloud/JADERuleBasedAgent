package org.jrba.rulesengine.rest.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageListenerRuleRest extends RuleRest implements Serializable {

	String className;
	String messageTemplate;
	int batchSize;
	String actionHandler;
	String selectRuleSetIdx;
}
