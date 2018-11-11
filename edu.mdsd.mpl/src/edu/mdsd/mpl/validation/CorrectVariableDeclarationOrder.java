package edu.mdsd.mpl.validation;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.FunctionalUnit;
import edu.mdsd.mpl.Operation;
import edu.mdsd.mpl.Variable;
import edu.mdsd.mpl.VariableDeclaration;
import edu.mdsd.mpl.VariableReference;

public class CorrectVariableDeclarationOrder extends AbstractModelConstraint {

	public CorrectVariableDeclarationOrder() {
	}

	@Override
	public IStatus validate(IValidationContext context) {
		// Get index of variable reference
		// get index of initialization
		
		VariableReference reference = (VariableReference) context.getTarget();
		Variable variable = reference.getVariable();
		
		VariableDeclaration declaration = null;
		
		if(variable.eContainer() == null) {
			return context.createSuccessStatus();
		} else if(variable.eContainer() instanceof VariableDeclaration) {
			declaration = (VariableDeclaration) variable.eContainer();
		} else if (Operation.class.isAssignableFrom(variable.eContainer().getClass())){
			Operation operation = (Operation) variable.eContainer();
			
			final boolean variableInParameters = operation.getParameters().contains(variable);
			
			if(variableInParameters) {
				return context.createSuccessStatus();
			} else {
				return context.createFailureStatus(variable.getName());
			}
		}
		
		
		FunctionalUnit program = null;
		if(FunctionalUnit.class.isAssignableFrom(declaration.eContainer().getClass())) {
			program = (FunctionalUnit) declaration.eContainer();
		} else {
			return context.createSuccessStatus();
		}
		
		List<VariableDeclaration> declarations = program.getVariableDeclarations();
		int indexVariableDeclaration = declarations.indexOf(declaration);
		
		int indexVariableUsage = Stream.iterate(reference, parent -> parent.eContainer() != null, EObject::eContainer)
			.filter(parent -> parent instanceof VariableDeclaration)
			.mapToInt(temp -> declarations.indexOf(temp))
			.findAny()
			.orElse(-1);
		
		final boolean indexReferencedBeforeDeclared = indexVariableUsage != -1 && indexVariableUsage < indexVariableDeclaration; 
		
		if(indexReferencedBeforeDeclared) {
			return context.createFailureStatus(variable.getName());
		}
		
		return context.createSuccessStatus();
	}

}
