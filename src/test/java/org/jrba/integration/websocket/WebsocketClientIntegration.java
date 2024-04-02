package org.jrba.integration.websocket;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.jrba.environment.websocket.GuiWebSocketClient;
import org.jrba.environment.websocket.GuiWebSocketServer;

import lombok.Getter;

@Getter
public class WebsocketClientIntegration extends GuiWebSocketClient {

	private ByteBuffer pongReceived;

	public WebsocketClientIntegration(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onWebsocketPong(final WebSocket conn, final Framedata f) {
		pongReceived = f.getPayloadData();
	}
}
