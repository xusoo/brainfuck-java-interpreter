package com.xuso.brainfuck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Stack;

import static com.xuso.brainfuck.Brainfuck.Commands.*;

public class Brainfuck {

	static class Commands {
		static final char
				LEFT = '<',
				RIGHT = '>',
				ADD = '+',
				SUBTRACT = '-',
				READ_INPUT = ',',
				WRITE_OUTPUT = '.',
				START_LOOP = '[',
				END_LOOP = ']';
	}

	private static final int DEFAULT_INITIAL_MEMORY_SIZE = 1024;

	private String code;
	private byte[] memory;
	private int memoryPointer;
	private int executionPointer;
	private InputStream input = System.in;
	private OutputStream output = System.out;

	private Stack<Integer> loopsPositions = new Stack<>();

	public Brainfuck(String code) {
		this(code, DEFAULT_INITIAL_MEMORY_SIZE);
	}

	public Brainfuck(String code, int initialMemorySize) {
		this.code = code;
		this.memory = new byte[initialMemorySize];
	}

	public void execute() {
		while (executionPointer < code.length()) {
			try {
				executeNext();
			} catch (Exception e) {
				throw new BrainfuckException(code, executionPointer, e);
			}
		}
	}

	private void executeNext() throws Exception {
		char command = code.charAt(executionPointer);
		switch (command) {
			case LEFT: moveLeft(); break;
			case RIGHT: moveRight(); break;
			case ADD: increaseValue(); break;
			case SUBTRACT: decreaseValue(); break;
			case READ_INPUT: readValue(); break;
			case WRITE_OUTPUT: writeValue(); break;
			case START_LOOP: startLoop(); break;
			case END_LOOP: endLoop(); break;
		}
		executionPointer++;
	}

	private void moveLeft() {
		memoryPointer--;
		if (memoryPointer < 0) throw new ArrayIndexOutOfBoundsException(memoryPointer);
	}

	private void moveRight() {
		memoryPointer++;
		duplicateMemorySizeIfNeeded();
	}

	private void increaseValue() {
		memory[memoryPointer]++;
	}

	private void decreaseValue() {
		memory[memoryPointer]--;
	}

	private void readValue() throws IOException {
		memory[memoryPointer] = (byte) input.read();
	}

	private void writeValue() throws IOException {
		output.write(memory[memoryPointer]);
	}

	private void startLoop() {
		if (memory[memoryPointer] == 0) {
			executionPointer = findMatchingLoopEnding();
		} else {
			loopsPositions.push(executionPointer);
		}
	}

	private void endLoop() {
		if (memory[memoryPointer] != 0) {
			executionPointer = loopsPositions.peek();
		} else {
			loopsPositions.pop();
		}
	}

	private int findMatchingLoopEnding() {
		int innerLoops = 0;
		for (int i = executionPointer + 1; i < code.length(); i++) {
			if (code.charAt(i) == END_LOOP) {
				if (innerLoops > 0) {
					innerLoops--;
				} else {
					return i;
				}
			} else if (code.charAt(i) == START_LOOP) {
				innerLoops++;
			}
		}
		throw new IllegalStateException("Matching loop ending not found!");
	}

	private void duplicateMemorySizeIfNeeded() {
		if (memoryPointer >= memory.length) {
			memory = Arrays.copyOf(memory, memory.length * 2);
		}
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}
}
