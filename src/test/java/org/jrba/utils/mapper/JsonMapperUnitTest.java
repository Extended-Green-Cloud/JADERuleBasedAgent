package org.jrba.utils.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jrba.utils.mapper.JsonMapper.getMapper;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonMapperUnitTest {

	@Test
	@DisplayName("Test if mapper has correct modules.")
	void testMapperModules() {
		var result = getMapper();

		assertThat(result.getRegisteredModuleIds())
				.satisfiesExactlyInAnyOrder(
						(id) -> assertThat((String) id).containsIgnoringCase("GuavaModule"),
						(id) -> assertThat((String) id).containsIgnoringCase("jackson-datatype-jsr310")
				);
	}

	@Test
	@DisplayName("Test mapping dates.")
	void testDateMapping() {
		var result = getMapper();

		assertDoesNotThrow(() -> result.writeValueAsString(Instant.now()));
	}
}
