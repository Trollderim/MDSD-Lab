package edu.mdsd.mil.compiler;

import java.nio.ByteBuffer;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ArithmeticInstruction;
import edu.mdsd.mil.CallInstruction;
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
import edu.mdsd.mil.NotEqualInstruction;
import edu.mdsd.mil.OutputInstruction;
import edu.mdsd.mil.PrintInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.ReturnInstruction;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;
import edu.mdsd.mil.UnconditionalJumpInstruction;
import edu.mdsd.mil.Value;
import edu.mdsd.mil.YieldInstruciton;

public class MILToMILBCompiler {
	private enum Bytecodes {
		LOAD_CONSTANT(0x01),
		LOAD_VARIABLE(0x02),
		STORE_DUMP(0x04),
		STORE_VARIABLE(0x05),
		ARITHMETIC_ADD(0xA0),
		ARITHMETIC_SUB(0xA1),
		ARITHMETIC_MUL(0xA2),
		ARITHMETIC_DIV(0xA3),
		JUMP_UNCONDITIONAL(0xB0),
		JUMP_CONDITIONAL(0xB1),
		EQUAL(0xC0),
		INEQUAL(0xC1),
		LESSTHAN(0xC2),
		LESSTHANEQUAL(0xC3),
		GREATERTHAN(0xC4),
		GREATERTHANEQUAL(0xC5),
		YIELD(0xD0),
		PRINT(0xD1),
		CALLOPERATION(0xE0),
		RETURN(0xE1);
		
		private Byte pattern;
		
		private Bytecodes(int patternNew) {
			pattern = (byte) patternNew;
		}

		public Byte pattern() {
			return pattern;
		}
	}
	
	Map<String, Integer> variableNames; 
	Map<String, Integer> labels;
	List<String> stringConstants;
	Map<Integer, String> unresolvedLabelReferences;
	
	List<Byte> result = new ArrayList<Byte>();
	
	public MILToMILBCompiler() {
		variableNames = new LinkedHashMap<>();
		labels = new LinkedHashMap<>();
		stringConstants = new ArrayList<>();
		unresolvedLabelReferences = new LinkedHashMap<>();
	}
	
	public List<Byte> compile(MILModel milModel) {		
		List<Instruction> instructions = milModel.getInstructions();
		
		for(Instruction instruction : instructions) {
			translateInstruction(instruction);
		}
		
		resolveJumpReferences(result);
		
		//addJumpLabels(result);
		addStrings(result);
			
		return result;
	}

	private void resolveJumpReferences(List<Byte> result) {
		for(Entry<Integer, String> unresolvedReference : unresolvedLabelReferences.entrySet()) {
			String labelName = unresolvedReference.getValue();
			Integer jumpToPosition = labels.get(labelName);
			Integer jumpFromPosition = unresolvedReference.getKey();
			
			List<Byte> byteValue = translateConstant(jumpToPosition);
			
			for(int i = 0; i < 4; i++) {
				result.set(jumpFromPosition + i, byteValue.get(i));
			}
		}
		
	}

	private void addStrings(List<Byte> result) {
		List<Byte> translated = new ArrayList<>();
		
		translated.addAll(translateConstant(stringConstants.size()));
		
		for(String stringConstant : stringConstants) {
			translated.addAll(translateString(stringConstant));
		}
		
		result.addAll(0, translated);
	}

	private void addJumpLabels(List<Byte> instructions) {
		List<Byte> translated = new ArrayList<>();
		
		translated.addAll(translateConstant(labels.size()));
		
		for(Integer labelLocation : labels.values()) {
			translated.addAll(translateConstant(labelLocation));
		}
		
		instructions.addAll(0, translated);
	}

	private void translateInstruction(Instruction instruction) {		
		if(instruction instanceof LoadInstruction) {
			LoadInstruction loadInstruction = ((LoadInstruction) instruction);
			
			translateLoadInstruction(result, loadInstruction);
		} else if(instruction instanceof StoreInstruction) {
			StoreInstruction storeInstruction = ((StoreInstruction) instruction);
			
			if(storeInstruction.getRegisterReference() == null) {
				//TODO: Make this implementation safe for more than 256 constants
				result.add(Bytecodes.STORE_DUMP.pattern);
			} else {
				result.add(Bytecodes.STORE_VARIABLE.pattern);
				int variableIndex = getVariableIndex(storeInstruction.getRegisterReference().getAddress());
				result.add((byte) variableIndex);
			}
		} else if(instruction instanceof ArithmeticInstruction) {
			translateArithmeticInstruction(result, (ArithmeticInstruction) instruction);
		} else if(instruction instanceof LabelInstruction){
			LabelInstruction label = (LabelInstruction) instruction;
			labels.put(label.getName(), result.size());
		} else if (instruction instanceof JumpInstruction) {
			translateJumpInstruction(result, (JumpInstruction) instruction);
		} else if (instruction instanceof CompareInstruction) {
			translateComparisonInstruction(result, (CompareInstruction) instruction);
		} else if (instruction instanceof OutputInstruction) {
			translateOutputInstruction(result, (OutputInstruction) instruction);
		} else if (instruction instanceof CallInstruction) {
			CallInstruction callInstruction = (CallInstruction) instruction;
			result.add(Bytecodes.CALLOPERATION.pattern);
			addJumpLabel(result, callInstruction.getOperationName().getName());
		} else if (instruction instanceof ReturnInstruction) {
			result.add(Bytecodes.RETURN.pattern);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void translateOutputInstruction(List<Byte> translated, OutputInstruction instruction) {
		if(instruction instanceof PrintInstruction) {
			translated.add(Bytecodes.PRINT.pattern);
			translated.addAll(translateConstant(stringConstants.size()));
			
			PrintInstruction print = (PrintInstruction) instruction;
			stringConstants.add(print.getOutput());
		} else if (instruction instanceof YieldInstruciton) {
			translated.add(Bytecodes.YIELD.pattern);
		}
	}

	private void translateComparisonInstruction(List<Byte> translated, CompareInstruction instruction) {
		if(instruction instanceof EqualInstruction) {
			translated.add(Bytecodes.EQUAL.pattern);
		} else if (instruction instanceof NotEqualInstruction) {
			translated.add(Bytecodes.INEQUAL.pattern);
		} else if (instruction instanceof LessThanInstruction) {
			translated.add(Bytecodes.LESSTHAN.pattern);
		} else if (instruction instanceof LessThanEqualInstruction) {
			translated.add(Bytecodes.LESSTHANEQUAL.pattern);
		} else if (instruction instanceof GreaterThanInstruction) {
			translated.add(Bytecodes.GREATERTHAN.pattern);
		} else if (instruction instanceof GreaterThanEqualInstruction) {
			translated.add(Bytecodes.GREATERTHANEQUAL.pattern);
		} else {
			throw new UnsupportedOperationException();
		}		
	}

	private void translateJumpInstruction(List<Byte> translated, JumpInstruction instruction) {		
		if(instruction instanceof UnconditionalJumpInstruction) {
			translated.add(Bytecodes.JUMP_UNCONDITIONAL.pattern);
		} else if(instruction instanceof ConditionalJumpInstruction) {
			translated.add(Bytecodes.JUMP_CONDITIONAL.pattern);
		} 
		
		String labelName = instruction.getJumpTo().getName();
		
		addJumpLabel(translated, labelName);		
	}

	private void addJumpLabel(List<Byte> translated, String labelName) {
		if(labels.containsKey(labelName)) {
			int getJumpIndex = labels.get(labelName);
			
			translated.addAll(translateConstant(getJumpIndex));
		} else {
			unresolvedLabelReferences.put(translated.size(), labelName);
			translated.addAll(translateConstant(0));
		}
	}

	private void translateArithmeticInstruction(List<Byte> translated, ArithmeticInstruction instruction) {
		if(instruction instanceof AddInstruction) {
			translated.add(Bytecodes.ARITHMETIC_ADD.pattern);
		} else if(instruction instanceof SubInstruction) {
			translated.add(Bytecodes.ARITHMETIC_SUB.pattern);
		} else if(instruction instanceof MulInstruction) {
			translated.add(Bytecodes.ARITHMETIC_MUL.pattern);
		} else if(instruction instanceof DivInstruction) {
			translated.add(Bytecodes.ARITHMETIC_DIV.pattern);
		}
	}

	private void translateLoadInstruction(List<Byte> translated, LoadInstruction loadInstruction) {
		Value value = loadInstruction.getValue();
		
		if(value instanceof ConstantInteger) {
			translated.add(Bytecodes.LOAD_CONSTANT.pattern);
			List<Byte> translatedConstant = translateConstant(((ConstantInteger) value).getRawValue());
			translated.addAll(translatedConstant);
		} else if(value instanceof RegisterReference) {
			translated.add(Bytecodes.LOAD_VARIABLE.pattern);
			int variableIndex = getVariableIndex(((RegisterReference) value).getAddress());
			translated.add((byte) variableIndex);
		}
	}
	
	private List<Byte> translateConstant(int constant) {
		List<Byte> translated = new ArrayList<>();
		
		byte[] bytes = ByteBuffer.allocate(4).putInt(constant).array();
		
		for(Byte temp : bytes) {
			translated.add(temp);
		}
		
		return translated;
	}
	
	private List<Byte> translateString(String value) {
		List<Byte> translated = new ArrayList<>();
		
		byte[] bytes = value.getBytes();
		
		for(Byte temp : bytes) {
			translated.add(temp);
		}
		
		translated.add((byte) 0);
		
		return translated;
	}
	
	//TODO: Make this implementation safe for more than 256 variables
	private Byte getVariableIndex(String variable) {
		if(variableNames.containsKey(variable)) {
			return variableNames.get(variable).byteValue();
		} else {
			variableNames.put(variable, variableNames.size());
			return (byte) (variableNames.size() - 1); 
		}
	}
}
