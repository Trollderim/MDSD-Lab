package edu.mdsd.mil.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ArithmeticInstruction;
import edu.mdsd.mil.CompareInstruction;
import edu.mdsd.mil.ConditionalJumpInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.DivInstruction;
import edu.mdsd.mil.EqualInstruction;
import edu.mdsd.mil.GreaterThanEqualInstruction;
import edu.mdsd.mil.GreaterThanInstruction;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.JumpInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LessThanEqualInstruction;
import edu.mdsd.mil.LessThanInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MulInstruction;
import edu.mdsd.mil.NegateInstruction;
import edu.mdsd.mil.NotEqualInstruction;
import edu.mdsd.mil.OutputInstruction;
import edu.mdsd.mil.PrintInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;
import edu.mdsd.mil.UnconditionalJumpInstruction;
import edu.mdsd.mil.Value;
import edu.mdsd.mil.YieldInstruciton;

public class MILInterpreter {
	private Stack<Integer> operandStack;
	private int programCounter;
	private Map<String, Integer> variableRegister;
	private List<Instruction> instructions;
	
	public MILInterpreter() {
		operandStack = new Stack<>();
		variableRegister = new HashMap<>();
		instructions = new ArrayList<>();
	}
	
	protected void initialize() {
		operandStack.clear();
		programCounter = 0;
		variableRegister.clear();
	}
	
	public Map<String, Integer> interpret (MILModel milModel) {
		initialize();
		
		instructions = milModel.getInstructions();
		
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
		
		if(instruction instanceof ArithmeticInstruction) {
			interpretArithmeticInstruction((ArithmeticInstruction) instruction);
			
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
		
		if(instruction instanceof CompareInstruction) {
			CompareInstruction compareInstruction = (CompareInstruction) instruction;
			interpretCompareInstruction(compareInstruction);
			
			return;
		}
		
		if(instruction instanceof JumpInstruction) {			
			interpretJumpInstruction((JumpInstruction) instruction);
			
			return;
		}
		
		if(instruction instanceof NegateInstruction) {
			boolean currentValue = popBooleanValueFromStack();
			pushBooleanOnStack(!currentValue);
			
			return;
		}
		
		if(instruction instanceof OutputInstruction) {
			interpretPrintInstruction((OutputInstruction) instruction);
			
			return;
		}
		
		if(instruction instanceof LabelInstruction) {
			return;
		}
		
		throw new UnsupportedOperationException();
	}

	private void interpretPrintInstruction(OutputInstruction instruction) {
		if(instruction instanceof YieldInstruciton) {
			int value = popFromOperandStack();
			System.out.print(value);
		} else if (instruction instanceof PrintInstruction) {
			PrintInstruction print = (PrintInstruction) instruction;
			System.out.print(print.getOutput());
		} else {
			throw new UnsupportedOperationException();
		}
		
	}

	private void interpretCompareInstruction(CompareInstruction instruction) {
		int operand2 = popFromOperandStack();
		int operand1 = popFromOperandStack();
		
		boolean result = false;
		
		if(instruction instanceof EqualInstruction) {
			result = operand1 == operand2;
		} else if (instruction instanceof NotEqualInstruction) {
			result = operand1 != operand2;
		} else if (instruction instanceof LessThanInstruction) {
			result = operand1 < operand2;
		} else if (instruction instanceof LessThanEqualInstruction) {
			result = operand1 <= operand2;
		} else if (instruction instanceof GreaterThanInstruction) {
			result = operand1 > operand2;
		} else if (instruction instanceof GreaterThanEqualInstruction) {
			result = operand1 >= operand2;
		} else {
			throw new UnsupportedOperationException();
		}
		
		pushBooleanOnStack(result);
	}
	

	private void interpretJumpInstruction(JumpInstruction instruction) {
		int indexToJumpTo = instructions.indexOf(instruction);
		
		if(instruction instanceof ConditionalJumpInstruction) {
			boolean evaluation = popBooleanValueFromStack();
			
			if(evaluation) {
				return;
			}
		}
		
		programCounter = indexToJumpTo + 1;
	}
	
	private void pushBooleanOnStack(boolean value) {
		if(value) {
			pushOnOperandStack(1);
		} else {
			pushOnOperandStack(0);
		}
	}
	
	private boolean popBooleanValueFromStack() {
		int value = popFromOperandStack();
		
		if (value == 0) {
			return false;
		}
		
		return true;
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
	
	private void interpretArithmeticInstruction(ArithmeticInstruction arithmeticInstruction) {
		int operand2 = popFromOperandStack();
		int operand1 = popFromOperandStack();

		int result = 0;
		
		if(arithmeticInstruction instanceof AddInstruction) {
			result = operand1 + operand2;
		} else if (arithmeticInstruction instanceof SubInstruction) {
			result = operand1 - operand2;
		} else if (arithmeticInstruction instanceof MulInstruction) {
			result = operand1 * operand2;
		} else if (arithmeticInstruction instanceof DivInstruction) {
			result = operand1 / operand2;
		} else {
			throw new UnsupportedOperationException();
		}
		
		pushOnOperandStack(result);
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
