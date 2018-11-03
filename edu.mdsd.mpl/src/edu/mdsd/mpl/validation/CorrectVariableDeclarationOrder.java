package edu.mdsd.mpl.validation;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Expression;
import edu.mdsd.mpl.Program;
import edu.mdsd.mpl.Variable;
import edu.mdsd.mpl.VariableDeclaration;
import edu.mdsd.mpl.VariableInitialization;
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
		
		VariableDeclaration declaration = (VariableDeclaration) variable.eContainer();
		Program program = (Program) declaration.eContainer();
		
		List<VariableDeclaration> declarations = program.getVariableDeclarations();
		int indexVariableDeclaration = declarations.indexOf(declaration);
		
		int indexVariableUsage = Stream.iterate(reference, parent -> parent.eContainer() != null, EObject::eContainer)
			.filter(parent -> parent instanceof VariableDeclaration)
			.mapToInt(temp -> declarations.indexOf(temp))
			.findAny()
			.orElse(-1);;
		
		if(indexVariableUsage != -1 && indexVariableUsage < indexVariableDeclaration) {
			return context.createFailureStatus(variable.getName());
		}
		
		return context.createSuccessStatus();
	}

}
