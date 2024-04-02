package org.jrba.environment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.jrba.integration.websocket.WebsocketClientIntegration;
import org.jrba.integration.websocket.WebsocketServerIntegration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GuiWebSocketIntegrationTest {

	private static WebsocketClientIntegration testSocketClient;
	private static WebsocketServerIntegration server;

	@BeforeAll
	static void initializeSocket() throws InterruptedException {
		server = new WebsocketServerIntegration();
		server.start();

		testSocketClient = new WebsocketClientIntegration(URI.create("ws://localhost:8080/"));
		testSocketClient.connectBlocking();
	}

	@AfterAll
	static void closeClient() throws InterruptedException {
		server.stop();
		testSocketClient.closeBlocking();
	}

	@Test
	@DisplayName("Test WebSocket server and client initialization.")
	void testWebsocketInitialization() {
		assertEquals(8080, server.getPort());
		assertEquals("0.0.0.0", server.getAddress().getHostName());
		assertTrue(testSocketClient.isOpen());
	}

	@Test
	@DisplayName("Ping-pong websocket test.")
	void testPingPongWebsocket() {
		testSocketClient.sendPing();

		await().timeout(3, SECONDS)
				.pollDelay(1, SECONDS)
				.untilAsserted(() -> assertNotNull(testSocketClient.getPongReceived()));
	}

	@Test
	@DisplayName("Send correct message via websocket.")
	void testSendCorrectMessage() {
		testSocketClient.send((Object) "Example message");

		await().timeout(3, SECONDS)
				.pollDelay(1, SECONDS)
				.untilAsserted(() -> assertEquals("\"Example message\"", server.getMessageReceived()));
	}

}
