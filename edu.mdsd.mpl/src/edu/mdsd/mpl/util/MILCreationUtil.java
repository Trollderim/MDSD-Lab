package edu.mdsd.mpl.util;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILFactory;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;

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
}
