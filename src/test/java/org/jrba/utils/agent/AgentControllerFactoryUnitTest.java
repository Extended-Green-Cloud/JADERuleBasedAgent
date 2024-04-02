package org.jrba.utils.agent;

import static jade.wrapper.AgentController.ASYNC;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.assertj.core.data.Offset;
import org.jrba.agentmodel.domain.args.AgentArgs;
import org.jrba.exception.JadeControllerException;
import org.jrba.fixtures.TestAgentNodeDefault;
import org.jrba.rulesengine.RulesController;
import org.jrba.utils.factory.AgentControllerFactory;
import org.jrba.utils.factory.AgentControllerFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import org.jrba.fixtures.TestAgentArgsDefault;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class AgentControllerFactoryUnitTest {

	@Mock
	ContainerController containerController;
	@Mock
	AgentController agentController;
	AgentControllerFactory agentControllerFactory;

	@BeforeEach
	void initialize() {
		openMocks(this);
		agentControllerFactory = new AgentControllerFactoryImpl(containerController);
	}

	@Test
	@DisplayName("Test create agent controller (no error).")
	void testCreateAgentController() throws StaleProxyException {
		final AgentArgs mockArgs = new TestAgentArgsDefault();
		final String mockAgentClass = "MockClass";

		final ArgumentMatcher<Object[]> matchArguments = (args) -> {
			var listArgs = asList(args);
			return listArgs.contains("Test agent") &&
					listArgs.contains("Test Parameter") &&
					listArgs.stream().anyMatch((arg) -> arg instanceof RulesController<?, ?>);
		};

		when(containerController.createNewAgent(eq("Test agent"), eq(mockAgentClass), argThat(matchArguments)))
				.thenReturn(agentController);

		var result = agentControllerFactory.createAgentController(mockArgs, mockAgentClass);

		assertEquals(agentController, result);
	}

	@Test
	@DisplayName("Test create agent controller (error).")
	void testCreateAgentControllerJadeControllerException() throws StaleProxyException {
		final AgentArgs mockArgs = new TestAgentArgsDefault();
		final String mockAgentClass = "MockClass";

		final ArgumentMatcher<Object[]> matchArguments = (args) -> {
			var listArgs = asList(args);
			return listArgs.contains("Test agent") &&
					listArgs.contains("Test Parameter") &&
					listArgs.stream().anyMatch((arg) -> arg instanceof RulesController<?, ?>);
		};

		when(containerController.createNewAgent(eq("Test agent"), eq(mockAgentClass), argThat(matchArguments)))
				.thenThrow(StaleProxyException.class);

		assertThrows(JadeControllerException.class,
				() -> agentControllerFactory.createAgentController(mockArgs, mockAgentClass));
	}

	@Test
	@DisplayName("Test connect agent controller with node (no error).")
	void testCreateAgentControllerWithNode() throws StaleProxyException {
		final AgentController mockInitialController = mock(AgentController.class);
		final TestAgentNodeDefault agentNode = new TestAgentNodeDefault();

		var result = agentControllerFactory.connectAgentController(mockInitialController, agentNode);

		verify(mockInitialController).putO2AObject(agentNode, ASYNC);
		verify(mockInitialController).putO2AObject(argThat(RulesController.class::isInstance), eq(ASYNC));
		assertEquals(mockInitialController, result);
	}

	@Test
	@DisplayName("Test connect agent controller with node (error).")
	void testCreateAgentControllerWithNodeError() throws StaleProxyException {
		final AgentController mockInitialController = mock(AgentController.class);
		final TestAgentNodeDefault agentNode = new TestAgentNodeDefault();

		doThrow(StaleProxyException.class).when(mockInitialController).putO2AObject(agentNode, ASYNC);

		assertThrows(JadeControllerException.class,
				() -> agentControllerFactory.connectAgentController(mockInitialController, agentNode));
	}

	@Test
	@DisplayName("Test running agent controller.")
	void testRunAgentController() throws StaleProxyException {
		final Instant timeBefore = Instant.now();
		agentControllerFactory.runAgentController(agentController, 1000);

		final long duration = Duration.between(timeBefore, Instant.now()).getSeconds() * 1000;

		assertThat(duration).isCloseTo(1000L, Offset.offset(20L));
		verify(agentController, times(1)).start();
		verify(agentController, times(1)).activate();
	}

	@Test
	@DisplayName("Test running agent controllers.")
	void testRunAgentControllers() throws StaleProxyException {
		agentControllerFactory.runAgentControllers(List.of(agentController, agentController), 1000);

		verify(agentController, times(2)).start();
		verify(agentController, times(2)).activate();
	}

	@Test
	@DisplayName("Test running agent controller with error.")
	void testRunAgentControllerError() throws StaleProxyException {
		doThrow(StaleProxyException.class).when(agentController).activate();
		assertThrows(JadeControllerException.class,
				() -> agentControllerFactory.runAgentController(agentController, 1000));
	}

	@Test
	@DisplayName("Test shutdown termination.")
	void testShutDownTermination() {
		var testExecutor = newSingleThreadExecutor();
		testExecutor.execute(() -> await().pollDelay(10, SECONDS).untilAsserted(() -> assertTrue(true)));

		assertFalse(testExecutor.isTerminated());
		agentControllerFactory.shutdownAndAwaitTermination(testExecutor, 2, SECONDS);

		await().timeout(5, SECONDS)
				.pollDelay(2, SECONDS)
				.untilAsserted(() -> assertTrue(testExecutor.isTerminated()));
	}

	@Test
	@DisplayName("Test shutdown termination with error.")
	void testShutDownTerminationWithError() throws InterruptedException {
		var testExecutor = spy(ExecutorService.class);

		willAnswer((invocation) -> {throw new InterruptedException("Test exception");})
				.given(testExecutor).awaitTermination(anyLong(), any());

		agentControllerFactory.shutdownAndAwaitTermination(testExecutor, 5, SECONDS);
		verify(testExecutor).shutdownNow();
	}
}
