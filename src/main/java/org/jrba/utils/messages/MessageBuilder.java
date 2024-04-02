package org.jrba.utils.messages;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static org.jrba.utils.mapper.JsonMapper.getMapper;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import org.jrba.exception.IncorrectMessageContentException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Class used to build ACL messages.
 */
public class MessageBuilder {

	private ACLMessage aclMessage;

	private MessageBuilder(final Integer ruleIdx, final Integer performative) {
		aclMessage = new ACLMessage(performative);
		aclMessage.setOntology(ruleIdx.toString());
	}

	public static MessageBuilder builder(final Integer ruleIdx, final Integer performative) {
		return new MessageBuilder(ruleIdx, performative);
	}

	public static MessageBuilder builder(final String ruleIdx, final Integer performative) {
		return new MessageBuilder(parseInt(ruleIdx), performative);
	}

	public MessageBuilder withMessageProtocol(final String messageProtocol) {
		this.aclMessage.setProtocol(messageProtocol);
		return this;
	}

	public MessageBuilder withOntology(final String messageOntology) {
		this.aclMessage.setOntology(messageOntology);
		return this;
	}

	public MessageBuilder withConversationId(final String conversationId) {
		this.aclMessage.setConversationId(conversationId);
		return this;
	}

	public MessageBuilder withGeneratedReplyWith() {
		final String replyWith = UUID.randomUUID().toString();
		this.aclMessage.setReplyWith(replyWith);
		return this;
	}

	public MessageBuilder withStringContent(final String content) {
		this.aclMessage.setContent(content);
		return this;
	}

	public MessageBuilder withObjectContent(final Object content) {
		try {
			this.aclMessage.setContent(getMapper().writeValueAsString(content));
		} catch (JsonProcessingException e) {
			throw new IncorrectMessageContentException();
		}
		return this;
	}

	public MessageBuilder withObjectContent(final Object content, final Consumer<Exception> errorHandler) {
		try {
			this.aclMessage.setContent(getMapper().writeValueAsString(content));
		} catch (JsonProcessingException e) {
			errorHandler.accept(e);
		}
		return this;
	}

	public MessageBuilder withPerformative(final Integer performative) {
		this.aclMessage.setPerformative(performative);
		return this;
	}

	public MessageBuilder withReplyWith(final String replyWith) {
		this.aclMessage.setReplyWith(replyWith);
		return this;
	}

	public MessageBuilder withSender(final AID sender) {
		this.aclMessage.setSender(sender);
		return this;
	}

	public MessageBuilder withReceivers(final AID... aids) {
		stream(aids).forEach(aclMessage::addReceiver);
		return this;
	}

	public MessageBuilder withNewReceivers(final AID... aids) {
		aclMessage.clearAllReceiver();
		stream(aids).forEach(aclMessage::addReceiver);
		return this;
	}

	public MessageBuilder withReceivers(final Collection<AID> aids) {
		aids.forEach(aclMessage::addReceiver);
		return this;
	}

	public MessageBuilder copy(final ACLMessage message) {
		this.aclMessage = (ACLMessage) message.clone();
		return this;
	}

	public ACLMessage build() {
		return aclMessage;
	}
}
