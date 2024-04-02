package org.jrba.integration.websocket;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WebsocketContext implements BeforeAllCallback, AfterAllCallback {

	private WebsocketServerIntegration server;

	@Override
	public void beforeAll(final ExtensionContext context) {
		server = new WebsocketServerIntegration();
		server.start();
	}

	@Override
	public void afterAll(final ExtensionContext extensionContext) throws Exception {
		server.stop();
	}
}
