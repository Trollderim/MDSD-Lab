package edu.mdsd.mil.compiler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.Value;

public class MILToMILBCompiler {
	private enum Bytecodes {
		LOAD_CONSTANT(0x01),
		LOAD_VARIABLE(0x02),
		SECTION_CONSTANTS(0xF0);
		
		private Byte pattern;
		
		private Bytecodes(int patternNew) {
			pattern = (byte) patternNew;
		}

		public Byte pattern() {
			return pattern;
		}
	}
	
	List<Integer> constants;
	
	public MILToMILBCompiler() {
		constants = new ArrayList<>();
	}
	
	public List<Byte> compile(MILModel milModel) {
		List<Byte> result = new ArrayList<Byte>();
		
		List<Instruction> instructions = milModel.getInstructions();
		
		for(Instruction instruction : instructions) {
			result.addAll(translateInstruction(instruction));
		}
		
		result.addAll(translateConstants());
		
		return result;
	}
	
	private List<Byte> translateInstruction(Instruction instruction) {
		List<Byte> translated = new ArrayList<>();
		
		if(instruction instanceof LoadInstruction) {
			Value value = ((LoadInstruction) instruction).getValue();
			
			if(value instanceof ConstantInteger) {
				//TODO: Make this implementation safe for more than 256 constants
				translated.add(Bytecodes.LOAD_CONSTANT.pattern);
				int indexOfConstant = constants.size();
				translated.add((byte) indexOfConstant);
				constants.add(((ConstantInteger) value).getRawValue());
			} else if(value instanceof RegisterReference) {
				translated.add(Bytecodes.LOAD_VARIABLE.pattern);
				//translated.add(e)
			}
		} else {
			//throw new UnsupportedOperationException();
		}
		
		return translated;
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
}
