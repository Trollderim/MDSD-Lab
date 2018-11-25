package edu.mdsd.mil.interpreter;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
	private Map<String, Integer> variableRegister;
	private int returnAddress;
	
	public StackFrame(int returnAddressNew) {
		variableRegister = new HashMap<>();
		returnAddress = returnAddressNew;
	}
	
	public int getReturnAddress() {
		return returnAddress;
	}
	
	public Map<String, Integer> getVariableRegister() {
		return variableRegister;
	}
	
	public int getVariableRegisterValue(String address) {
		if(!variableRegister.containsKey(address)) {
			variableRegister.put(address, 0);
		}
		
		return variableRegister.get(address);
	}
	
	public void setVariableRegisterValue(String address, int rawValue) {
		variableRegister.put(address, rawValue);
	}
}
