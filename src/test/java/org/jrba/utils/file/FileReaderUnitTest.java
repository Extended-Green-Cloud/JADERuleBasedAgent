package org.jrba.utils.file;

import static java.io.File.separator;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.jrba.utils.file.FileReader.buildResourceFilePath;
import static org.jrba.utils.file.FileReader.isLoadedInJar;
import static org.jrba.utils.file.FileReader.readAllFiles;
import static org.jrba.utils.file.FileReader.readFile;
import static org.jrba.utils.mapper.JsonMapper.getMapper;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jrba.exception.InvalidFileException;
import org.jrba.rulesengine.rest.domain.RuleSetRest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class FileReaderUnitTest {

	@Test
	@DisplayName("Test loaded in jar.")
	void testLoadedInJar() {
		assertFalse(isLoadedInJar());
	}

	@Test
	@DisplayName("Test build file path (not .jar).")
	void testBuildFilePathNotJar() {
		final String result = buildResourceFilePath("test1", "test2");
		assertEquals("test1" + separator + "test2", result);
	}

	@Test
	@DisplayName("Test build file path (.jar).")
	void testBuildFilePathJar() {
		try (MockedStatic<FileReader> mockReader = mockStatic(FileReader.class)) {
			mockReader.when(FileReader::isLoadedInJar).thenReturn(true);
			mockReader.when(() -> FileReader.buildResourceFilePath(any(), any())).thenCallRealMethod();

			final String result = buildResourceFilePath("test1", "test2");
			assertEquals("test1/test2", result);

		}
	}

	@Test
	@DisplayName("Test read file from path.")
	void testReadFileFromPath() throws IOException {
		final String filePath = buildResourceFilePath("test-rulesets", "test-ruleset.json");
		final File result = readFile(filePath);
		final RuleSetRest resultContent = getMapper().readValue(result, RuleSetRest.class);

		assertNotNull(result);
		assertNotNull(resultContent);
	}

	@Test
	@DisplayName("Test read file from path error.")
	void testReadFileFromPathError() {
		final String filePath = buildResourceFilePath("incorrect", "test-ruleset.json");

		final InvalidFileException error = assertThrows(InvalidFileException.class, () -> readFile(filePath));
		assertEquals("Invalid file name.", error.getMessage());
	}

	@Test
	@DisplayName("Test read file from file.")
	void testReadFileFromFile() throws IOException {
		final String filePath = buildResourceFilePath("test-rulesets", "test-ruleset.json");
		final File initialFile = readFile(filePath);

		final File result = readFile(initialFile);
		final RuleSetRest resultContent = getMapper().readValue(result, RuleSetRest.class);

		assertNotNull(result);
		assertNotNull(resultContent);
	}

	@Test
	@DisplayName("Test read file from file error.")
	void testReadFileFromFileError() {
		final InvalidFileException error = assertThrows(InvalidFileException.class, () -> readFile((File) null));
		assertEquals("File could not be read.", error.getMessage());
	}

	@Test
	@DisplayName("Test read files from empty directory.")
	void testReadFilesFromEmptyDirectory() {
		final List<File> result = assertDoesNotThrow(() -> readAllFiles("test-empty-directory"));
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Test read files from non-existing directory.")
	void testReadFilesFromNonExistingDirectory() {
		final List<File> result = assertDoesNotThrow(() -> readAllFiles("test-non-existing-directory"));
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Test read files from existing directory.")
	void testReadFilesFromExistingDirectory() {
		final List<File> result = assertDoesNotThrow(() -> readAllFiles("test-rulesets"));
		assertThatCollection(result).hasSize(1);
	}

}
