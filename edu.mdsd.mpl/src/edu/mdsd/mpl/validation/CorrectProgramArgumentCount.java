package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Operation;
import edu.mdsd.mpl.OperationExpression;

public class CorrectProgramArgumentCount extends AbstractModelConstraint {

	public CorrectProgramArgumentCount() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		OperationExpression opExpr = (OperationExpression) context.getTarget();
		Operation operation = opExpr.getOperation();
		
		final int countExpressionArguments = opExpr.getParameters().size();
		final int countExpectedParameters = operation.getParameters().size();
		
		if(countExpressionArguments != countExpectedParameters) {
			return context.createFailureStatus();
		}
		
		return context.createSuccessStatus();
	}

}
