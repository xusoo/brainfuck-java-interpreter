package com.xuso.brainfuck;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class BrainfuckTest {

	@ParameterizedTest
	@MethodSource("getSampleFiles")
	void testSampleFiles(Path testFile) throws IOException {
		File input = new File(getFileBaseName(testFile) + ".in");
		File expectedOutput = new File(getFileBaseName(testFile) + ".out");
		Brainfuck brainfuck = new Brainfuck(new String(Files.readAllBytes(testFile)));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		brainfuck.setOutput(output);
		if (input.exists()) {
			brainfuck.setInput(new ByteArrayInputStream(Files.readAllBytes(input.toPath())));
		}
		brainfuck.execute();
		if (expectedOutput.exists()) {
			assertEquals(new String(Files.readAllBytes(expectedOutput.toPath())), output.toString());
		}
	}

	private static DirectoryStream<Path> getSampleFiles() throws IOException {
		return Files.newDirectoryStream(new File("./samples").toPath(), "*.b");
	}

	private static String getFileBaseName(Path path) {
		return path.toString().substring(0, path.toString().lastIndexOf('.'));
	}

}