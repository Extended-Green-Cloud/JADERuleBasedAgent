package org.jrba.environment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.quality.Strictness.LENIENT;

import java.io.IOException;

import org.jrba.environment.websocket.GuiWebSocketClient;
import org.jrba.environment.websocket.GuiWebSocketServer;
import org.jrba.exception.IncorrectMessageContentException;
import org.jrba.utils.mapper.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class GuiWebSocketUnitTest {

	@Spy
	GuiWebSocketClient mockClient;
	@Spy
	GuiWebSocketServer mockServer;
	@Mock
	ObjectMapper mockMapper;

	@BeforeEach
	void initializeMocks() {
		openMocks(this);
	}

	@Test
	@DisplayName("Test GuiWebSocketClient onError.")
	void testGuiWebsocketClientOnErrorHandling() {
		doCallRealMethod().when(mockClient).run();
		doThrow(RuntimeException.class).when(mockClient).isTcpNoDelay();

		mockClient.run();
		verify(mockClient, times(1)).onError(any());
	}

	@Test
	@DisplayName("Test GuiWebSocketServer onError.")
	void testGuiWebsocketServerOnErrorHandling() {
		doCallRealMethod().when(mockServer).run();
		willAnswer((invocation) -> { throw new IOException(); }).given(mockServer).isReuseAddr();

		mockServer.run();
		verify(mockServer, times(1)).onError(any(), any());
	}

	@Test
	@DisplayName("Test GuiWebSocketClient invalid object to send.")
	void testInvalidMessageHandling() throws JsonProcessingException {
		doCallRealMethod().when(mockClient).send((Object) any());
		doReturn(true).when(mockClient).isOpen();
		doThrow(JsonProcessingException.class).when(mockMapper).writeValueAsString(any());

		try (MockedStatic<JsonMapper> mapUtils = mockStatic(JsonMapper.class)) {
			mapUtils.when(JsonMapper::getMapper).thenReturn(mockMapper);

			assertThrows(IncorrectMessageContentException.class, () -> mockClient.send((Object) "test"));
		}
	}
}
