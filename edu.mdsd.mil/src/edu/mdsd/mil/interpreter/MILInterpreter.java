package edu.mdsd.mil.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.Value;

public class MILInterpreter {
	private Stack<Integer> operandStack;
	private int programCounter;
	private Map<String, Integer> variableRegister;
	
	public MILInterpreter() {
		operandStack = new Stack<>();
		variableRegister = new HashMap<>();
	}
	
	protected void initialize() {
		operandStack.clear();
		programCounter = 0;
		variableRegister.clear();
	}
	
	public Map<String, Integer> interpret (MILModel milModel) {
		initialize();
		
		List<Instruction> instructions = milModel.getInstructions();
		
		while(programCounter < instructions.size()) {
			Instruction instruction = instructions.get(programCounter);
			
			programCounter++;
			interpretInstruction(instruction);
		}
		
		return variableRegister;
	}
	
	protected void interpretInstruction(Instruction instruction) {
		if(instruction instanceof LoadInstruction) {
			LoadInstruction loadInstruction = (LoadInstruction) instruction;
			Value value = loadInstruction.getValue();
			int rawValue = getRawValue(value);
			pushOnOperandStack(rawValue);			
			return;
		}
		
		if(instruction instanceof AddInstruction) {
			int operand2 = popFromOperandStack();
			int operand1 = popFromOperandStack();

			int result = operand1 + operand2;
			pushOnOperandStack(result);
			return;
		}
		
		if(instruction instanceof StoreInstruction) {
			StoreInstruction storeInstruction = (StoreInstruction) instruction;
			RegisterReference registerReference = storeInstruction.getRegisterReference();
			
			int rawValue = popFromOperandStack();
			
			if(registerReference != null) {
				String address = registerReference.getAddress();
				setVariableRegisterValue(address, rawValue);
			}
			
			return;
		}
		
		throw new UnsupportedOperationException();
	}

	private int getRawValue(Value value) {
		if(value instanceof ConstantInteger) {
			ConstantInteger constantInteger = (ConstantInteger) value;
			return constantInteger.getRawValue();
		}
		
		if(value instanceof RegisterReference) {
			RegisterReference registerReference = (RegisterReference) value;
			String address = registerReference.getAddress();
			return getVariableRegisterValue(address);
		}
		
		throw new UnsupportedOperationException();
	}

	private int popFromOperandStack() {
		return operandStack.pop();
	}
	
	private void pushOnOperandStack(int rawValue) {
		operandStack.push(rawValue);
	}
	
	private int getVariableRegisterValue(String address) {
		if(!variableRegister.containsKey(address)) {
			variableRegister.put(address, 0);
		}
		
		return variableRegister.get(address);
	}
	
	private void setVariableRegisterValue(String address, int rawValue) {
		variableRegister.put(address, rawValue);
	}
	
	public void interpretAndOutputResult(MILModel milModel) {
		Map<String, Integer> results = interpret(milModel);
		
		System.out.println("Results");
		
		for(Entry<String, Integer> entry : results.entrySet()) {
			String address = entry.getKey();
			Integer rawValue = entry.getValue();
			
			System.out.println(address + " = " + rawValue);
		}
	}
}
