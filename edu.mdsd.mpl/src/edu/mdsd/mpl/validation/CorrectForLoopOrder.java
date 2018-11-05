package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Assignment;
import edu.mdsd.mpl.Expression;
import edu.mdsd.mpl.ForLoop;
import edu.mdsd.mpl.ForLoopDirection;
import edu.mdsd.mpl.LiteralValue;
import edu.mdsd.mpl.VariableDeclaration;

public class CorrectForLoopOrder extends AbstractModelConstraint {

	public CorrectForLoopOrder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		ForLoop forLoop = (ForLoop) context.getTarget();
		Assignment assignment = forLoop.getCounter();
		Expression initialization = assignment.getRightHandSide();
		
		Expression bound = forLoop.getBound();
		
		if(initialization != null && initialization instanceof LiteralValue && bound instanceof LiteralValue) {
			boolean correctOrder = true;
			
			LiteralValue counterValue = (LiteralValue) initialization;
			LiteralValue boundValue = (LiteralValue) bound;
			
			if(forLoop.getDirection() == ForLoopDirection.UP) {
				if(counterValue.getRawValue() > boundValue.getRawValue()) {
					correctOrder = false;
				}
			} else  {
				if(counterValue.getRawValue() < boundValue.getRawValue()) {
					correctOrder = false;
				}
			}
			
			if(!correctOrder) {
				return context.createFailureStatus();
			}
		}
		
		return context.createSuccessStatus();
	}

}
