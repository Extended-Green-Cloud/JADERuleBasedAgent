package org.jrba.rulesengine.mvel;

import static org.jrba.rulesengine.mvel.MVELObjectType.CONCURRENT_MAP;
import static org.jrba.rulesengine.mvel.MVELObjectType.LIST;
import static org.jrba.rulesengine.mvel.MVELObjectType.MAP;
import static org.jrba.rulesengine.mvel.MVELObjectType.SET;
import static org.jrba.rulesengine.mvel.MVELObjectType.getObjectForType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class MVELObjectTypeUnitTest {

	@Test
	@DisplayName("Test getting HashSet object from MVEL enum.")
	void testMVELGetSet() {
		final Object result = getObjectForType(SET);

		assertInstanceOf(HashSet.class, result);
		assertEquals(0, ((HashSet<?>) result).size());
	}

	@Test
	@DisplayName("Test getting ArrayList object from MVEL enum.")
	void testMVELGetList() {
		final Object result = getObjectForType(LIST);

		assertInstanceOf(ArrayList.class, result);
		assertEquals(0, ((ArrayList<?>) result).size());
	}

	@Test
	@DisplayName("Test getting HashMap object from MVEL enum.")
	void testMVELGetMap() {
		final Object result = getObjectForType(MAP);

		assertInstanceOf(HashMap.class, result);
		assertEquals(0, ((HashMap<?, ?>) result).size());
	}

	@Test
	@DisplayName("Test getting ConcurrentHashMap object from MVEL enum.")
	void testMVELGetConcurrentMap() {
		final Object result = getObjectForType(CONCURRENT_MAP);

		assertInstanceOf(ConcurrentHashMap.class, result);
		assertEquals(0, ((ConcurrentHashMap<?, ?>) result).size());
	}

}
