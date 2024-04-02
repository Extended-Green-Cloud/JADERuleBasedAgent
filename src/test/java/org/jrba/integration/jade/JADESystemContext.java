package org.jrba.integration.jade;

import static jade.core.AID.ISGUID;
import static jade.core.Runtime.instance;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedMethods;
import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jrba.exception.JadeControllerException;
import org.jrba.rulesengine.RulesController;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public class JADESystemContext implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

	private static final Logger logger = LoggerFactory.getLogger(JADESystemContext.class);

	private final ExecutorService executorService = newCachedThreadPool();
	private Future<ContainerController> controllerStartUp;
	private ContainerController jadeController;
	private MultiValuedMap<Integer, AgentController> agentControllers;
	private Map<String, Field> agentContextFields;
	private List<Field> agentFields;
	private List<Method> methodFields;
	private Class<?> testClass;

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		jadeController = runContainer();
		await().timeout(10, SECONDS)
				.pollDelay(5, SECONDS)
				.untilAsserted(() -> controllerStartUp.isDone());

		context.getTestClass().ifPresent(testClass -> {
			this.testClass = testClass;
			agentFields = findAnnotatedFields(testClass, AgentContext.class);
			methodFields = findAnnotatedMethods(testClass, ParametersContext.class, BOTTOM_UP);

			if (nonNull(testClass.getDeclaredAnnotation(RunRMA.class))) {
				runRMA();
			}
		});
	}

	@Override
	public void beforeEach(final ExtensionContext context) {
		agentContextFields = new HashMap<>();
		agentControllers = agentFields.stream().collect(ArrayListValuedHashMap::new,
				this::putControllerIntoMap,
				ArrayListValuedHashMap::putAll);

		agentControllers.asMap().entrySet().stream()
				.sorted(comparingByKey())
				.map(Map.Entry::getValue)
				.flatMap(Collection::stream)
				.forEach(controller -> runAgentController(controller, context));

	}

	@Override
	public void afterEach(final ExtensionContext context) {
		agentControllers.asMap().entrySet().stream()
				.sorted(comparingByKey())
				.map(Map.Entry::getValue)
				.flatMap(Collection::stream)
				.forEach(this::stopAgentController);
	}

	@Override
	public void afterAll(final ExtensionContext context) throws ControllerException {
		jadeController.getPlatformController().kill();
		executorService.shutdownNow();
	}

	private void runRMA() {
		final AgentController rma;
		try {
			rma = jadeController.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
		} catch (StaleProxyException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	private void runAgentController(final AgentController controller, final ExtensionContext context) {
		try {
			controller.start();
			controller.activate();
			mapToLocalAgentField(controller, context);
			logger.info("Agent {} has been started.", controller.getName());
		} catch (StaleProxyException | NoSuchFieldException | IllegalAccessException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	private void putControllerIntoMap(final ArrayListValuedHashMap<Integer, AgentController> map,
			final Field agentField) {
		final AgentContext annotation = agentField.getAnnotation(AgentContext.class);
		final Integer order = annotation.order();
		agentContextFields.put(annotation.agentName(), agentField);
		map.put(order, initializeControllerFromAnnotation(annotation, methodFields, testClass));
	}

	private void stopAgentController(final AgentController controller) {
		try {
			final String agentName = controller.getName();
			controller.kill();
			await().timeout(10, SECONDS).until(() -> checkIfAgentTerminated(controller));
			logger.info("Agent {} has been stopped.", agentName);
		} catch (StaleProxyException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to stop agent controller", e);
		}
	}

	private ContainerController runContainer() throws ExecutionException, InterruptedException, StaleProxyException {
		final Runtime runtime = instance();
		final Profile profile = new ProfileImpl("127.0.0.1", 6996, "TestPlatform", true);
		controllerStartUp = executorService.submit(() -> runtime.createMainContainer(profile));

		return controllerStartUp.get();
	}

	private void mapToLocalAgentField(final AgentController controller, final ExtensionContext context)
			throws NoSuchFieldException, IllegalAccessException, StaleProxyException {
		final Field myImplField = ContainerController.class.getDeclaredField("myImpl");
		myImplField.setAccessible(true);
		final AgentContainer myImpl = ((AgentContainer) myImplField.get(jadeController));
		final Agent initializedAgent = myImpl.acquireLocalAgent(new AID(controller.getName(), ISGUID));
		myImpl.releaseLocalAgent(initializedAgent.getAID());
		final Field agentField = agentContextFields.get(initializedAgent.getLocalName());
		agentField.set(context.getRequiredTestInstance(), agentField.getType().cast(initializedAgent));
	}

	private AgentController initializeControllerFromAnnotation(final AgentContext annotation,
			final List<Method> methodFields, final Object testClass) {
		final String agentClass = annotation.agentClass();
		final String agentName = annotation.agentName();
		final String paramsConstructName = annotation.parametersConstructor();

		final Object[] params = methodFields.stream()
				.filter(method -> method.getName().equals(paramsConstructName))
				.findFirst()
				.map(paramsMethod -> getAgentParameters(paramsMethod, testClass))
				.orElse(new Object[] {});

		return createAgentController(agentName, agentClass, params);
	}

	private AgentController createAgentController(final String name, final String className, final Object[] params) {
		final RulesController<?, ?> rulesController = new RulesController<>();
		try {
			final List<Object> argumentsToPass = new ArrayList<>(singletonList(rulesController));
			argumentsToPass.addAll(asList(params));
			return jadeController.createNewAgent(name, className, argumentsToPass.toArray());
		} catch (StaleProxyException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to run custom agent controller", e);
		}
	}

	private Object[] getAgentParameters(final Method parametersMethod, final Object testInstance) {
		try {
			final Object executor = ((Class<?>) testInstance).getConstructor().newInstance();
			return (Object[]) parametersMethod.invoke(executor);
		} catch (IllegalAccessException
				 | NoSuchMethodException
				 | InstantiationException
				 | InvocationTargetException e) {
			throw new IllegalStateException("Could not invoke method.");
		}
	}

	private boolean checkIfAgentTerminated(final AgentController controller) {
		try {
			controller.getState();
			return false;
		} catch (Exception e) {
			return e.getMessage().equals("Controlled agent does not exist");
		}
	}
}
