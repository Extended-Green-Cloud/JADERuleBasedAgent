package org.jrba.utils.yellowpages;

import static jade.lang.acl.ACLMessage.CFP;
import static org.jrba.fixtures.TestAgentDescriptionMatchers.matchDescription;
import static org.jrba.fixtures.TestAgentDescriptionMatchers.matchDescriptionWithName;
import static org.jrba.fixtures.TestAgentDescriptionMatchers.matchDescriptionWithOwnership;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.SUBSCRIBE;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.asList;
import static org.jrba.utils.yellowpages.YellowPagesRegister.decodeSubscription;
import static org.jrba.utils.yellowpages.YellowPagesRegister.deregister;
import static org.jrba.utils.yellowpages.YellowPagesRegister.prepareDF;
import static org.jrba.utils.yellowpages.YellowPagesRegister.prepareSubscription;
import static org.jrba.utils.yellowpages.YellowPagesRegister.register;
import static org.jrba.utils.yellowpages.YellowPagesRegister.search;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.jrba.utils.messages.MessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class YellowPageServiceUnitTest {

	private static final String mockPlatformId = "10";
	private static final String mockJadeAddress = "192.168.56.1:6996/JADE";
	private static final String mockAgent = "test_agent@" + mockJadeAddress;

	private static Stream<Arguments> parametersSubscriptionMessage() {
		return Stream.of(
				arguments("test_agent",
						"((iota ?x (result (action "
								+ "( agent-identifier :name test_df ) "
								+ "(search (df-agent-description "
								+ ":services (set (service-description :type test_agent_type :ownership test_agent))) "
								+ "(search-constraints :max-results -1))) ?x)))"),
				arguments(null,
						"((iota ?x (result (action "
								+ "( agent-identifier :name test_df ) "
								+ "(search (df-agent-description "
								+ ":services (set (service-description :type test_agent_type))) "
								+ "(search-constraints :max-results -1))) ?x)))")
		);
	}

	@Test
	@DisplayName("Test decode subscription.")
	void testDecodeSubscription() {
		var mockAddress = ":addresses (sequence http://Test-Address/acc)) ";

		var iotaPrefix = "((= (iota ?x (result (action ";
		var mockDFDescription = "(agent-identifier " + ":name df@" + mockJadeAddress + " " + mockAddress;
		var mockSearch = "(search "
				+ "(df-agent-description :services "
				+ "(set (service-description :type test_type))) "
				+ "(search-constraints :max-results -1))) ?x)) ";

		var mockResult = "(sequence "
				+ "(df-agent-description "
				+ ":name (agent-identifier :name " + mockAgent + " " + mockAddress
				+ ":services (set (service-description "
				+ ":name test_agent "
				+ ":type test_type))))))";

		var messageContent = String.join("", iotaPrefix, mockDFDescription, mockSearch, mockResult);

		final ACLMessage testMessage = MessageBuilder.builder(0, INFORM)
				.withStringContent(messageContent)
				.build();

		var result = decodeSubscription(testMessage);

		assertThat(result)
				.as("Result has correct size equal to 1")
				.hasSize(1)
				.as("Result has correct content")
				.allSatisfy((aid, bool) -> assertEquals(mockAgent, aid.getName()));
	}

	@Test
	@DisplayName("Test decode subscription (unsuccessful).")
	void testDecodeSubscriptionWithFIPAException() {
		var mockAddress = ":addresses (sequence http://Test-Address/acc)) ";

		var iotaPrefix = "((= (iota ?x (result (action ";
		var mockDFDescription = "(agent-identifier " + ":name df@" + mockJadeAddress + " " + mockAddress;
		var mockSearch = "(search "
				+ "(df-agent-description :services "
				+ "(set (service-description :type test_type))) "
				+ "(search-constraints :max-results -1))) ?x)) ";

		var mockResult = "(sequence "
				+ "(df-agent-description "
				+ ":name (agent-identifier :name " + mockAgent + " " + mockAddress
				+ ":services (set (service-description "
				+ ":name test_agent "
				+ ":type test_type))))))";

		var messageContent = String.join("", iotaPrefix, mockDFDescription, mockSearch, mockResult);

		final ACLMessage testMessage = MessageBuilder.builder(0, INFORM)
				.withStringContent(messageContent)
				.build();

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.decodeNotification(messageContent)).thenThrow(FIPAException.class);

			var result = decodeSubscription(testMessage);
			assertTrue(result.isEmpty());
		}
	}

	@ParameterizedTest
	@MethodSource("parametersSubscriptionMessage")
	@DisplayName("Test preparing subscription message for DF.")
	void testPrepareSubscription(@Nullable String ownership, String expectedContent) {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = spy(Agent.class);

		doReturn("test_agent").when(mockAgent).getName();
		doReturn(mockDF).when(mockAgent).getDefaultDF();

		var result = nonNull(ownership) ?
				prepareSubscription(mockAgent, mockDF, "test_agent_type", "test_agent") :
				prepareSubscription(mockAgent, mockDF, "test_agent_type");

		assertThat(result)
				.as("Message contains correct fields")
				.satisfies(message -> {
					assertThat(message.getProtocol()).isEqualTo("fipa-subscribe");
					assertThat(message.getOntology()).isEqualTo("FIPA-Agent-Management");
					assertThat(message.getLanguage()).isEqualTo("fipa-sl");
					assertThat(message.getConversationId()).contains("conv-test_agent");
					assertThat(message.getReplyWith()).contains("rw-test_agent");
					assertThat(message.getContent()).isEqualTo(expectedContent);
					assertThat(message.getPerformative()).isEqualTo(SUBSCRIBE);
					assertThat(message.getAllReceiver().next())
							.isInstanceOfSatisfying(AID.class,
									receiver -> assertThat(receiver.getName()).isEqualTo("test_df"));
				});
	}

	@Test
	@DisplayName("Test prepare DF.")
	void testPrepareDF() {
		final AID result = prepareDF(mockJadeAddress, mockPlatformId);

		assertEquals("df@10", result.getName());
		assertEquals("df", result.getLocalName());
		assertTrue(asList(result.getAddressesArray()).contains(mockJadeAddress));
	}

	@Test
	@DisplayName("Test register agent service with ownership (successful).")
	void testRegisterAgentServiceWithOwnership() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			register(mockAgent, mockDF, "test_service_type", "test_service_name", "test_agent");

			dfService.verify(() -> DFService.register(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership)), times(1));
			dfService.verify(() -> DFService.keepRegistered(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership), eq(null)), times(1));
		}
	}

	@Test
	@DisplayName("Test register agent service with ownership (FIPAException).")
	void testRegisterAgentServiceWithOwnershipWithException() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.register(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithOwnership)))
					.thenThrow(FIPAException.class);

			register(mockAgent, mockDF, "test_service_type", "test_service_name", "test_agent");

			dfService.verify(() -> DFService.register(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership)), times(1));
			dfService.verify(() -> DFService.keepRegistered(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership), eq(null)), times(0));
		}
	}

	@Test
	@DisplayName("Test register agent service (successful).")
	void testRegisterAgentService() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			register(mockAgent, mockDF, "test_service_type", "test_service_name");

			dfService.verify(() -> DFService.register(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)),
					times(1));
		}
	}

	@Test
	@DisplayName("Test register agent service (FIPAException).")
	void testRegisterAgentServiceWithException() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.register(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)))
					.thenThrow(FIPAException.class);

			register(mockAgent, mockDF, "test_service_type", "test_service_name");

			dfService.verify(() -> DFService.register(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)),
					times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by only agent instance (successful).")
	void testDeregisterAgentServiceByOnlyAgent() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			deregister(mockAgent, mockDF);
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF)), times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by only agent instance (unsuccessful).")
	void testDeregisterAgentServiceByOnlyAgentFIPAException() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.deregister(eq(mockAgent), eq(mockDF))).thenThrow(FIPAException.class);

			assertDoesNotThrow(() -> deregister(mockAgent, mockDF));
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF)), times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by service type and name (successful).")
	void testDeregisterAgentServiceByServiceTypeAndName() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			deregister(mockAgent, mockDF, "test_service_type", "test_service_name");
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)),
					times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by service type and name (unsuccessful).")
	void testDeregisterAgentServiceByServiceTypeAndNameFIPAException() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.deregister(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)))
					.thenThrow(FIPAException.class);

			assertDoesNotThrow(() -> deregister(mockAgent, mockDF, "test_service_type", "test_service_name"));
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)),
					times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by service type, name and ownership (successful).")
	void testDeregisterAgentServiceByServiceTypeAndNameAndOwnership() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			deregister(mockAgent, mockDF, "test_service_type", "test_service_name", "test_agent");
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF), argThat(matchDescriptionWithName)),
					times(1));
		}
	}

	@Test
	@DisplayName("Test deregister agent service by service type, name and ownership (unsuccessful).")
	void testDeregisterAgentServiceByServiceTypeAndNameAndOwnershipFIPAException() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.deregister(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership))).thenThrow(FIPAException.class);

			assertDoesNotThrow(() -> deregister(mockAgent, mockDF, "test_service_type", "test_service_name",
					"test_agent"));
			dfService.verify(() -> DFService.deregister(eq(mockAgent), eq(mockDF),
					argThat(matchDescriptionWithOwnership)), times(1));
		}
	}

	@Test
	@DisplayName("Test search for agents (successful).")
	void testSearchForAgents() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgentAID = new AID("test_agent", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.search(eq(mockAgent), eq(mockDF), argThat(matchDescription)))
					.thenReturn(prepareAgentDescriptions(mockAgentAID));

			var result = search(mockAgent, mockDF, "test_service_type");

			dfService.verify(() -> DFService.search(eq(mockAgent), eq(mockDF), argThat(matchDescription)), times(1));
			assertEquals(1, result.size());
			assertEquals(mockAgentAID, result.stream().findFirst().get());
		}
	}

	@Test
	@DisplayName("Test search for agents (unsuccessful).")
	void testSearchForAgentsFIPAError() {
		var mockDF = new AID("test_df", AID.ISGUID);
		var mockAgentAID = new AID("test_agent", AID.ISGUID);
		var mockAgent = mock(Agent.class);

		try (MockedStatic<DFService> dfService = mockStatic(DFService.class)) {
			dfService.when(() -> DFService.search(eq(mockAgent), eq(mockDF), argThat(matchDescription)))
					.thenThrow(FIPAException.class);

			var result = search(mockAgent, mockDF, "test_service_type");

			dfService.verify(() -> DFService.search(eq(mockAgent), eq(mockDF), argThat(matchDescription)), times(1));
			assertTrue(result.isEmpty());
		}
	}

	private DFAgentDescription[] prepareAgentDescriptions(final	AID aid) {
		final DFAgentDescription mockDescription = new DFAgentDescription();
		mockDescription.setName(aid);

		return new DFAgentDescription[] { mockDescription };
	}
}
