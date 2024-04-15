package org.jrba.utils.messages;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.ACLMessage.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.utils.mapper.JsonMapper.getMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.commons.collections4.IteratorUtils;
import org.jrba.exception.IncorrectMessageContentException;
import org.jrba.utils.mapper.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class MessageBuilderUnitTest {

	@Test
	@DisplayName("Test initialize MessageBuilder.")
	void testInitializeMessageBuilder() {
		ACLMessage message = MessageBuilder.builder(0).build();

		assertEquals("0", message.getOntology());
		assertEquals(UNKNOWN, message.getPerformative());

		message = MessageBuilder.builder(0, PROPOSE).build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());

		message = MessageBuilder.builder("1", PROPOSE).build();

		assertEquals("1", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
	}

	@Test
	@DisplayName("Test build message protocol.")
	void testBuildWithMessageProtocol() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withMessageProtocol("TEST_PROTOCOL")
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals("TEST_PROTOCOL", message.getProtocol());
	}

	@Test
	@DisplayName("Test build message ontology.")
	void testBuildWithMessageOntology() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withOntology("TEST_ONTOLOGY")
				.build();

		assertEquals("TEST_ONTOLOGY", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
	}

	@Test
	@DisplayName("Test build message conversation identifier.")
	void testBuildWithMessageConversationId() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withConversationId("TEST_CONVERSATION_ID")
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals("TEST_CONVERSATION_ID", message.getConversationId());
	}

	@Test
	@DisplayName("Test build message reply with.")
	void testBuildWithMessageReplyWith() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withReplyWith("TEST_REPLY_WITH")
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals("TEST_REPLY_WITH", message.getReplyWith());
	}

	@Test
	@DisplayName("Test build message generated reply with.")
	void testBuildWithMessageGeneratedReplyWith() {
		try (MockedStatic<UUID> mockUUIDGenerator = mockStatic(UUID.class)) {
			mockUUIDGenerator.when(UUID::randomUUID).thenReturn(new UUID(10, 10));

			final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
					.withGeneratedReplyWith()
					.build();

			assertEquals("0", message.getOntology());
			assertEquals(PROPOSE, message.getPerformative());
			assertEquals("00000000-0000-000a-0000-00000000000a", message.getReplyWith());
		}
	}

	@Test
	@DisplayName("Test build message string content.")
	void testBuildWithMessageStringContent() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withStringContent("TEST_CONTENT")
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals("TEST_CONTENT", message.getContent());
	}

	@Test
	@DisplayName("Test build message object content.")
	void testBuildWithMessageObjectContent() throws JsonProcessingException {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withObjectContent(Instant.parse("2024-01-01T12:00:00.00Z"))
				.build();
		final Instant content = getMapper().readValue(message.getContent(), Instant.class);

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals(Instant.parse("2024-01-01T12:00:00.00Z"), content);
	}

	@Test
	@DisplayName("Test build message object content with incorrect content.")
	void testBuildWithMessageObjectContentIncorrectContent() throws JsonProcessingException {
		try (MockedStatic<JsonMapper> mockJsonMapper = mockStatic(JsonMapper.class)) {
			final ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
			mockJsonMapper.when(JsonMapper::getMapper).thenReturn(mockMapper);

			assertThrows(IncorrectMessageContentException.class, () -> MessageBuilder.builder(0, PROPOSE)
					.withObjectContent(Instant.parse("2024-01-01T12:00:00.00Z"))
					.build());
		}
	}

	@Test
	@DisplayName("Test build message object content with error handler.")
	void testBuildWithMessageObjectContentWithErrorHandler() throws JsonProcessingException {
		final AtomicBoolean isCorrectResponse = new AtomicBoolean(false);
		final Consumer<Exception> testHandler = (e) -> isCorrectResponse.set(true);

		try (MockedStatic<JsonMapper> mockJsonMapper = mockStatic(JsonMapper.class)) {
			final ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
			mockJsonMapper.when(JsonMapper::getMapper).thenReturn(mockMapper);

			MessageBuilder.builder(0, PROPOSE)
					.withObjectContent(Instant.parse("2024-01-01T12:00:00.00Z"), testHandler)
					.build();
			assertTrue(isCorrectResponse.get());
		}
	}

	@Test
	@DisplayName("Test build message performative.")
	void testBuildWithMessagePerformative() {
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withPerformative(REQUEST)
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(REQUEST, message.getPerformative());
	}

	@Test
	@DisplayName("Test build message sender.")
	void testBuildWithMessageSender() {
		final AID testAID = new AID("TestName", AID.ISGUID);
		final ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withSender(testAID)
				.build();

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertEquals(testAID, message.getSender());
	}

	@Test
	@DisplayName("Test build message receivers.")
	void testBuildWithMessageReceivers() {
		final AID testAID1 = new AID("TestName", AID.ISGUID);
		ACLMessage message = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(testAID1)
				.build();
		List<AID> receivers = IteratorUtils.toList((Iterator<AID>) message.getAllReceiver());

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertThatCollection(receivers).contains(testAID1);

		final AID testAID2 = new AID("TestName2", AID.ISGUID);
		message = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(List.of(testAID2))
				.build();
		receivers = IteratorUtils.toList((Iterator<AID>) message.getAllReceiver());

		assertEquals("0", message.getOntology());
		assertEquals(PROPOSE, message.getPerformative());
		assertThatCollection(receivers).contains(testAID2);
	}

	@Test
	@DisplayName("Test copy message.")
	void testCopyMessage() {
		final AID testAID1 = new AID("TestName", AID.ISGUID);
		final ACLMessage initialMsg = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(testAID1)
				.build();
		final ACLMessage copiedMsg = MessageBuilder.builder(0, PROPOSE).copy(initialMsg).build();

		assertThat(copiedMsg).usingRecursiveComparison().isEqualTo(initialMsg);
	}

	@Test
	@DisplayName("Test build message new receivers.")
	void testBuildWithMessageNewReceivers() {
		final AID testAID1 = new AID("TestName", AID.ISGUID);
		final ACLMessage initialMsg = MessageBuilder.builder(0, PROPOSE)
				.withReceivers(testAID1)
				.build();
		final AID testAID2 = new AID("TestName2", AID.ISGUID);
		final ACLMessage copiedMsg = MessageBuilder.builder(0, PROPOSE)
				.copy(initialMsg)
				.withNewReceivers(testAID2)
				.build();
		final List<AID> receivers = IteratorUtils.toList((Iterator<AID>) copiedMsg.getAllReceiver());

		assertThatCollection(receivers).doesNotContain(testAID1).contains(testAID2);
	}
}
