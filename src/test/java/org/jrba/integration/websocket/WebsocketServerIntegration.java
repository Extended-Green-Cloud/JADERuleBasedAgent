package org.jrba.integration.websocket;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.jrba.environment.websocket.GuiWebSocketServer;

import lombok.Getter;

@Getter
public class WebsocketServerIntegration extends GuiWebSocketServer {

	private ByteBuffer pingReceived;
	private String messageReceived;

	public WebsocketServerIntegration() {
		super(9669);
	}

	@Override
	public void onWebsocketPing(final WebSocket conn, final Framedata f) {
		super.onWebsocketPing(conn, f);
		pingReceived = f.getPayloadData();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		super.onMessage(conn, message);
		messageReceived = message;
	}
}
