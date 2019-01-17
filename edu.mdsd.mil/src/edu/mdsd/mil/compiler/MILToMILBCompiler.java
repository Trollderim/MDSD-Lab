package edu.mdsd.mil.compiler;

import java.nio.ByteBuffer;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.Value;

public class MILToMILBCompiler {
	private enum Bytecodes {
		LOAD_CONSTANT(0x01),
		LOAD_VARIABLE(0x02),
		STORE_DUMP(0x04),
		STORE_VARIABLE(0x05),
		ARITHMETIC_ADD(0xA0),
		SECTION_CONSTANTS(0xF0),
		SECTION_VARIABLE_NAMES(0xF2);
		
		private Byte pattern;
		
		private Bytecodes(int patternNew) {
			pattern = (byte) patternNew;
		}

		public Byte pattern() {
			return pattern;
		}
	}
	
	List<Integer> constants;
	Map<String, Integer> variableNames; 
	
	public MILToMILBCompiler() {
		constants = new ArrayList<>();
		variableNames = new LinkedHashMap<>();
	}
	
	public List<Byte> compile(MILModel milModel) {
		List<Byte> result = new ArrayList<Byte>();
		
		List<Instruction> instructions = milModel.getInstructions();
		
		for(Instruction instruction : instructions) {
			result.addAll(translateInstruction(instruction));
		}
		
		result.addAll(translateConstants());
		result.addAll(translateVariableNames());
		
		return result;
	}
	
	private List<Byte> translateInstruction(Instruction instruction) {
		List<Byte> translated = new ArrayList<>();
		
		if(instruction instanceof LoadInstruction) {
			LoadInstruction loadInstruction = ((LoadInstruction) instruction);
			
			translateLoadInstruction(translated, loadInstruction);
		} else if(instruction instanceof StoreInstruction) {
			StoreInstruction storeInstruction = ((StoreInstruction) instruction);
			
			if(storeInstruction.getRegisterReference() == null) {
				//TODO: Make this implementation safe for more than 256 constants
				translated.add(Bytecodes.STORE_DUMP.pattern);
			} else {
				translated.add(Bytecodes.STORE_VARIABLE.pattern);
				int variableIndex = getVariableIndex(storeInstruction.getRegisterReference().getAddress());
				translated.add((byte) variableIndex);
			}
		} else if(instruction instanceof AddInstruction) {
			translated.add(Bytecodes.ARITHMETIC_ADD.pattern);
		} else {
			//throw new UnsupportedOperationException();
		}
		
		return translated;
	}

	private void translateLoadInstruction(List<Byte> translated, LoadInstruction loadInstruction) {
		Value value = loadInstruction.getValue();
		
		if(value instanceof ConstantInteger) {
			//TODO: Make this implementation safe for more than 256 constants
			translated.add(Bytecodes.LOAD_CONSTANT.pattern);
			int indexOfConstant = constants.size();
			translated.add((byte) indexOfConstant);
			constants.add(((ConstantInteger) value).getRawValue());
		} else if(value instanceof RegisterReference) {
			translated.add(Bytecodes.LOAD_VARIABLE.pattern);
			int variableIndex = getVariableIndex(((RegisterReference) value).getAddress());
			translated.add((byte) variableIndex);
		}
	}
	
	private List<Byte> translateConstants() {
		List<Byte> translated = new ArrayList<>();
		translated.add(Bytecodes.SECTION_CONSTANTS.pattern);
		
		for (Integer constant : constants) {
			byte[] bytes = ByteBuffer.allocate(4).putInt(constant).array();
			
			for(Byte temp : bytes) {
				translated.add(temp);
			}
		}
		
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
	
	private List<Byte> translateVariableNames() {
		List<Byte> translated = new ArrayList<>();
		translated.add(Bytecodes.SECTION_VARIABLE_NAMES.pattern);
		
		for (String name : variableNames.keySet()) {
			byte[] bytes = name.getBytes();
			
			for(Byte temp : bytes) {
				translated.add(temp);
			}
			
			translated.add((byte) 0x00);
		}
		
		return translated;
	}
}
