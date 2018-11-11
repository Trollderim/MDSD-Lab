package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.ExpressionStatement;
import edu.mdsd.mpl.Operation;
import edu.mdsd.mpl.OperationExpression;
import edu.mdsd.mpl.Procedure;

public class CorrectProcedureCallUsage extends AbstractModelConstraint {

	public CorrectProcedureCallUsage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		OperationExpression opExp = (OperationExpression) context.getTarget();
		Operation operation = opExp.getOperation();
		
		if(operation instanceof Procedure) {
			if(!(opExp.eContainer() instanceof ExpressionStatement)) {
				return context.createFailureStatus();
			}
		}
		
		return context.createSuccessStatus();
	}

}
