package org.jrba.utils.messages;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.utils.messages.MessageReader.readForPerformative;
import static org.jrba.utils.messages.MessageReader.readMessageContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.assertj.core.api.Condition;
import org.jrba.agentmodel.domain.args.AgentArgs;
import org.jrba.agentmodel.domain.args.ImmutableAgentArgs;
import org.jrba.exception.IncorrectMessageContentException;
import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class MessageReaderUnitTest {

	@Test
	@DisplayName("Test read message content (successful)")
	void testReadMessageContent() {
		final AgentArgs agentArgs = ImmutableAgentArgs.builder()
				.name("Name")
				.build();
		final ACLMessage msg = MessageBuilder.builder(0, PROPOSE)
				.withObjectContent(agentArgs)
				.build();

		assertEquals(agentArgs, readMessageContent(msg, AgentArgs.class));
	}

	@Test
	@DisplayName("Test read message content (unsuccessful)")
	void testReadMessageContentInvalid() {
		final AgentArgs agentArgs = ImmutableAgentArgs.builder()
				.name("Name")
				.build();
		final ACLMessage msg = MessageBuilder.builder(0, PROPOSE)
				.withObjectContent(agentArgs)
				.build();

		assertThrows(IncorrectMessageContentException.class, () -> readMessageContent(msg, Path.class));
	}

	@Test
	@DisplayName("Test retrieve empty set of messages for performative")
	void testRetrieveForPerformativeEmpty() {
		assertTrue(readForPerformative(new Vector<>(), INFORM).isEmpty());
	}

	@Test
	@DisplayName("Test retrieve non empty set of messages for performative")
	void testRetrieveForPerformative() {
		final Vector<ACLMessage> messages = new Vector<>(prepareMessages());

		assertThat(readForPerformative(messages, REFUSE))
				.hasSize(1)
				.areExactly(1,
						new Condition<>(message -> Objects.equals("Message 3", message.getContent()), "thirdMsg"));
	}

	private List<ACLMessage> prepareMessages() {
		final AID aid1 = mock(AID.class);
		final AID aid2 = mock(AID.class);
		final AID aid3 = mock(AID.class);

		doReturn("Sender1").when(aid1).getName();
		doReturn("Sender2").when(aid2).getName();
		doReturn("Sender3").when(aid3).getName();

		final ACLMessage aclMessage1 = MessageBuilder.builder(0, PROPOSE)
				.withStringContent("Message 1")
				.withSender(aid1)
				.build();
		final ACLMessage aclMessage2 = MessageBuilder.builder(0, PROPOSE)
				.withStringContent("Message 2")
				.withSender(aid2)
				.build();
		final ACLMessage aclMessage3 = MessageBuilder.builder(0, REFUSE)
				.withStringContent("Message 3")
				.withSender(aid3)
				.build();
		return List.of(aclMessage1, aclMessage2, aclMessage3);
	}
}
