package edu.mdsd.mpl.util;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.CallInstruction;
import edu.mdsd.mil.ConditionalJumpInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.DivInstruction;
import edu.mdsd.mil.EqualInstruction;
import edu.mdsd.mil.GreaterThanEqualInstruction;
import edu.mdsd.mil.GreaterThanInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LessThanEqualInstruction;
import edu.mdsd.mil.LessThanInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILFactory;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MulInstruction;
import edu.mdsd.mil.NotEqualInstruction;
import edu.mdsd.mil.PrintInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.ReturnInstruction;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;
import edu.mdsd.mil.UnconditionalJumpInstruction;
import edu.mdsd.mil.YieldInstruciton;
import edu.mdsd.mpl.Operation;

public class MILCreationUtil {
	private static final MILFactory factory = MILFactory.eINSTANCE;
	
	public static MILModel createMILModel() {
		return factory.createMILModel();
	}
	
	public static LoadInstruction createLoadInstruction(int rawValue) {
		LoadInstruction loadInstruction = factory.createLoadInstruction();
		ConstantInteger value = factory.createConstantInteger();
		value.setRawValue(rawValue);
		loadInstruction.setValue(value);
		return loadInstruction;
	}
	
	public static LoadInstruction createLoadInstruction(String address) {
		LoadInstruction loadInstruction = factory.createLoadInstruction();
		RegisterReference registerReference = createRegisterReference(address);
		loadInstruction.setValue(registerReference);
		return loadInstruction;
	}
	
	public static StoreInstruction createStoreInstruction(String address) {
		StoreInstruction storeInstruction = factory.createStoreInstruction();
		RegisterReference registerReference = createRegisterReference(address);
		storeInstruction.setRegisterReference(registerReference);
		return storeInstruction;
	}
	
	public static RegisterReference createRegisterReference(String address) {
		RegisterReference registerReference = factory.createRegisterReference();
		registerReference.setAddress(address);
		return registerReference;
	}

	public static AddInstruction createAddInstruction() {
		AddInstruction addInstruction = factory.createAddInstruction();
		return addInstruction;
	}

	public static SubInstruction createSubInstruction() {
		SubInstruction subInstruction = factory.createSubInstruction();
		return subInstruction;
	}

	public static MulInstruction createMulInstruction() {
		MulInstruction mulInstruction = factory.createMulInstruction();
		return mulInstruction;
	}

	public static DivInstruction createDivInstruction() {
		DivInstruction divInstruction = factory.createDivInstruction();
		return divInstruction;
	}
	
	public static EqualInstruction createEqualInstruction() {
		EqualInstruction instruction = factory.createEqualInstruction();
		return instruction;
	}
	
	public static NotEqualInstruction createNotEqualInstruction() {
		NotEqualInstruction instruction = factory.createNotEqualInstruction();
		return instruction;
	}
	
	public static GreaterThanInstruction createGreaterThanInstruction() {
		GreaterThanInstruction instruction = factory.createGreaterThanInstruction();
		return instruction;
	}
	
	public static GreaterThanEqualInstruction createGreaterThanEqualInstruction() {
		GreaterThanEqualInstruction instruction = factory.createGreaterThanEqualInstruction();
		return instruction;
	}
	
	public static LessThanInstruction createLessThanInstruction() {
		LessThanInstruction instruction = factory.createLessThanInstruction();
		return instruction;
	}
	
	public static LessThanEqualInstruction createLessThanEqualInstruction() {
		LessThanEqualInstruction instruction = factory.createLessThanEqualInstruction();
		return instruction;
	}
	
	public static ConditionalJumpInstruction createConditionalJumpInstruction(LabelInstruction jumpTo) {
		ConditionalJumpInstruction instruction = factory.createConditionalJumpInstruction();
		instruction.setJumpTo(jumpTo);
		return instruction;
	}
	
	public static UnconditionalJumpInstruction createUnconditionalJumpInstruction(LabelInstruction jumpTo) {
		UnconditionalJumpInstruction instruction = factory.createUnconditionalJumpInstruction();
		instruction.setJumpTo(jumpTo);
		return instruction;
	}
	
	public static LabelInstruction createLabelInstruction(String name) {
		LabelInstruction instruction = factory.createLabelInstruction();
		instruction.setName(name);
		return instruction;
	}

	public static PrintInstruction createPrintInstruction(String output) {
		PrintInstruction print = factory.createPrintInstruction();
		print.setOutput(output);
		return print;
	}
	
	public static YieldInstruciton createYieldInstruction() {
		YieldInstruciton yield = factory.createYieldInstruciton();
		return yield;
	}
	
	public static ReturnInstruction createReturnInstruction() {
		ReturnInstruction returnIns = factory.createReturnInstruction();
		return returnIns;
	}
	
	public static CallInstruction createCallInstruction(LabelInstruction operation) {
		CallInstruction callInstruction = factory.createCallInstruction();
		callInstruction.setOperationName(operation);
		return callInstruction;
	}
}
