package edu.mdsd.mpl.validation;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Function;
import edu.mdsd.mpl.FunctionalUnit;
import edu.mdsd.mpl.Procedure;
import edu.mdsd.mpl.Program;
import edu.mdsd.mpl.ReturnStatement;

public class CorrectReturnStatementUsage extends AbstractModelConstraint {

	public CorrectReturnStatementUsage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		ReturnStatement statement = (ReturnStatement) context.getTarget();
		
		FunctionalUnit parent = (FunctionalUnit) Stream.iterate(statement, next -> next.eContainer() != null, EObject::eContainer)
				.filter(potentialParent -> potentialParent instanceof FunctionalUnit)
				.findAny()
				.orElse(null);
		
		if(parent != null) {
			if(parent instanceof Program) {
				return context.createFailureStatus();
			} else if (parent instanceof Procedure) {
				if(statement.getReturnValue() != null) {
					return context.createFailureStatus();
				}
			} else if (parent instanceof Function) {
				if(statement.getReturnValue() == null) {
					return context.createFailureStatus();
				}
			}
		}
		
		return null;
	}

}
