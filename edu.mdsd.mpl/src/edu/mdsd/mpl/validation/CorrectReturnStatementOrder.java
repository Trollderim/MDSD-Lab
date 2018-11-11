package edu.mdsd.mpl.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Block;
import edu.mdsd.mpl.ReturnStatement;
import edu.mdsd.mpl.Statement;

public class CorrectReturnStatementOrder extends AbstractModelConstraint {

	public CorrectReturnStatementOrder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		Statement stmt = (Statement) context.getTarget();
		Block block = (Block) stmt.eContainer(); 
		
		final List<Statement> statements = block.getStatements();
		
		final int indexOfStatement = statements.indexOf(stmt);
		
		int indexOfReturnStatement = statements.stream()
				.filter(candidate -> candidate instanceof ReturnStatement)
				.mapToInt(temp -> statements.indexOf(temp))
				.findFirst()
				.orElse(-1);
		
		if(indexOfReturnStatement != -1 && indexOfStatement > indexOfReturnStatement) {
			return context.createFailureStatus();
		}
		
		return context.createSuccessStatus();
	}

}
