package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.ExpressionStatement;
import edu.mdsd.mpl.OperationExpression;

public class CorrectExpressionResultUsage extends AbstractModelConstraint {

	public CorrectExpressionResultUsage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		ExpressionStatement expStmt = (ExpressionStatement) context.getTarget();
		
		final boolean isFunctionCall = expStmt.getExpression() instanceof OperationExpression;
		
		if(isFunctionCall) {
			return context.createSuccessStatus();
		}
		
		return context.createFailureStatus();
	}

}
