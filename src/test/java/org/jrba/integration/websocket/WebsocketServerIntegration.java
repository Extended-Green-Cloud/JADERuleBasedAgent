package org.jrba.integration.websocket;

import org.jrba.environment.websocket.GuiWebSocketServer;

import lombok.Getter;

@Getter
public class WebsocketServerIntegration extends GuiWebSocketServer {

	public WebsocketServerIntegration() {
		super(9669);
	}
}
