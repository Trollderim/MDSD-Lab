package edu.mdsd.mpl.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.FunctionalUnit;
import edu.mdsd.mpl.Operation;
import edu.mdsd.mpl.Variable;
import edu.mdsd.mpl.VariableDeclaration;

public class UniqueVariableNamesConstraint extends AbstractModelConstraint {

	public UniqueVariableNamesConstraint() {
		
	}

	@Override
	public IStatus validate(IValidationContext context) {
		Variable variable = (Variable) context.getTarget();
		String variableName = variable.getName();
		
		List<Variable> variables = new ArrayList<>();
		
		if(variable.eContainer() instanceof VariableDeclaration) {
			VariableDeclaration declaration = (VariableDeclaration) variable.eContainer();
			
			variables.addAll(extractVariablesFromDeclaration(declaration));
		}
		
		if(variables.isEmpty()) {
			return context.createSuccessStatus();
		}
		
		for(Variable otherVariable : variables) {
			String otherVariableName = otherVariable.getName();
			
			if(variable != otherVariable) {
				if(variableName.equals(otherVariableName)) {
					return context.createFailureStatus(variableName);
				}
			}
		}
		
		return context.createSuccessStatus();
	}

	List<Variable> extractVariablesFromDeclaration(VariableDeclaration declaration) {
		List<Variable> variables = new ArrayList<>();
		
		FunctionalUnit unit = null;
		if(FunctionalUnit.class.isAssignableFrom(declaration.eContainer().getClass())) {
			unit = (FunctionalUnit) declaration.eContainer();
		} else {
			return null;
		}
		
		List<VariableDeclaration> variableDeclarations = unit.getVariableDeclarations();
		
		for(VariableDeclaration tempDeclaration : variableDeclarations) {
			variables.add(tempDeclaration.getVariable());
		}
		
		final boolean isOperation = Operation.class.isAssignableFrom(declaration.eContainer().getClass());
		if(isOperation) {
			Operation operation = (Operation) unit;
			
			variables.addAll(operation.getParameters());
		}
		
		return variables;
	}
}
