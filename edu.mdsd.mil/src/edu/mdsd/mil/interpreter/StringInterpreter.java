package edu.mdsd.mil.interpreter;

public class StringInterpreter {
	public static String interpretStringWithControlSecuence(String input) {
		input = input.substring(1, input.length() - 1);
		
		input = input.replace("\\n", "\n");
		input = input.replace("\\\\", "\\");
		input = input.replace("\\t", "\t");
		input = input.replace("\\b", "\b");
		input = input.replace("\\\'", "\'");
		input = input.replace("\\r", "\r");
		input = input.replace("\\\"", "\"");
		
		return input;
	}
}
