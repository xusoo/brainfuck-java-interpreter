package com.xuso.brainfuck;

public class BrainfuckException extends RuntimeException {

	BrainfuckException(String code, int position, Throwable cause) {
		super(buildErrorMessage(code, position, cause), cause);
	}

	private static String buildErrorMessage(String code, int position, Throwable cause) {
		String message = "Error at: \n" + code + "\n";
		message += new String(new char[position]).replace("\0", " ");
		message += "^";
		message += "\n" + cause.getMessage();
		return message;
	}
}
