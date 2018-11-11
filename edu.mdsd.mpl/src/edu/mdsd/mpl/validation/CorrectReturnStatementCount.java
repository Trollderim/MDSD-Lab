package edu.mdsd.mpl.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.Function;
import edu.mdsd.mpl.IfStatement;
import edu.mdsd.mpl.ReturnStatement;
import edu.mdsd.mpl.Statement;

public class CorrectReturnStatementCount extends AbstractModelConstraint {

	public CorrectReturnStatementCount() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		Function function = (Function) context.getTarget();
		
		List<Statement> statements = function.getFunctionalBody().getStatements();
		List<Statement> allStatements = collectAllStatements(statements);
		
		int indexOfReturnStatement = allStatements.stream()
				.filter(candidate -> candidate instanceof ReturnStatement)
				.mapToInt(temp -> allStatements.indexOf(temp))
				.findAny()
				.orElse(-1);
		
		if(indexOfReturnStatement == -1) {
			return context.createFailureStatus();
		}
		
		return context.createSuccessStatus();
	}
	
	public List<Statement> collectAllStatements(final List<Statement> statements) {		
		for(int i = 0; i < statements.size(); i++) {
			Statement statement = statements.get(i);
			
			if(statement instanceof IfStatement) {
				IfStatement ifStatement = (IfStatement) statement; 
				
				if(ifStatement.getThenBlock() != null) {
					statements.addAll(ifStatement.getThenBlock().getStatements());
				}
				
				if(ifStatement.getElseBlock() != null) {
					statements.addAll(ifStatement.getElseBlock().getStatements());
				}
			}
		}
		
		return statements;
	}

}
