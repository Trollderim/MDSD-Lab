package edu.mdsd.mil.compiler;

import java.nio.ByteBuffer;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ArithmeticInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.DivInstruction;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MulInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;
import edu.mdsd.mil.Value;

public class MILToMILBCompiler {
	private enum Bytecodes {
		LOAD_CONSTANT(0x01),
		LOAD_VARIABLE(0x02),
		STORE_DUMP(0x04),
		STORE_VARIABLE(0x05),
		ARITHMETIC_ADD(0xA0),
		ARITHMETIC_SUB(0xA1),
		ARITHMETIC_MUL(0xA2),
		ARITHMETIC_DIV(0xA3);
		
		private Byte pattern;
		
		private Bytecodes(int patternNew) {
			pattern = (byte) patternNew;
		}

		public Byte pattern() {
			return pattern;
		}
	}
	
	Map<String, Integer> variableNames; 
	
	public MILToMILBCompiler() {
		variableNames = new LinkedHashMap<>();
	}
	
	public List<Byte> compile(MILModel milModel) {
		List<Byte> result = new ArrayList<Byte>();
		
		List<Instruction> instructions = milModel.getInstructions();
		
		for(Instruction instruction : instructions) {
			result.addAll(translateInstruction(instruction));
		}
			
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
		} else if(instruction instanceof ArithmeticInstruction) {
			translateArithmeticInstruction(translated, (ArithmeticInstruction) instruction);
		} else {
			//throw new UnsupportedOperationException();
		}
		
		return translated;
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
