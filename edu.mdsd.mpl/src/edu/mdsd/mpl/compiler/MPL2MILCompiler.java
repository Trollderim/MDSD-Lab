package edu.mdsd.mpl.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mpl.AddExpression;
import edu.mdsd.mpl.Assignment;
import edu.mdsd.mpl.Expression;
import edu.mdsd.mpl.ExpressionStatement;
import edu.mdsd.mpl.LiteralValue;
import edu.mdsd.mpl.MPLModel;
import edu.mdsd.mpl.Program;
import edu.mdsd.mpl.Statement;
import edu.mdsd.mpl.Variable;
import edu.mdsd.mpl.VariableDeclaration;
import edu.mdsd.mpl.VariableReference;
import edu.mdsd.mpl.util.MILCreationUtil;

public class MPL2MILCompiler {
	private List<Instruction> instructions;
	
	public void compileAndSave(Resource mplResource) {
		MILModel milModel = compile(mplResource);
		
		URI mplResourceUri = mplResource.getURI();
		URI milResourceUri = mplResourceUri.trimFileExtension().appendFileExtension("mil");
		
		ResourceSet resourceSet = mplResource.getResourceSet();
		Resource milResource = resourceSet.createResource(milResourceUri);
		
		milResource.getContents().add(milModel);
		
		try {
			milResource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MILModel compile(Resource mplResource) {
		MPLModel mplModel = (MPLModel) mplResource.getContents().get(0);
		Program program = mplModel.getProgram();
		return compile(program);
	}
	
	public MILModel compile(Program program) {
		MILModel milModel = MILCreationUtil.createMILModel();
		
		 instructions = milModel.getInstructions();
		 
		 compileProgram(program);
		
		return milModel;
	}

	private void compileProgram(Program program) {
		List<VariableDeclaration> variableDeclarations = program.getVariableDeclarations();
		
		for(VariableDeclaration variableDeclaration : variableDeclarations) {
			compileVariableDeclaration(variableDeclaration);
		}
		
		List<Statement> statements = program.getFunctionalBody().getStatements();
		
		for(Statement statement : statements) {
			compileStatements(statement);
		}	
	}

	private void compileVariableDeclaration(VariableDeclaration variableDeclaration) {
		Variable variable = variableDeclaration.getVariable();
		
		addLoadInstruction(0);
		addStoreInstruction(variable);
	}
	
	private void compileStatements(Statement statement) {
		if(statement instanceof Assignment) {
			Assignment assignment = (Assignment) statement;
			compileAssignment(assignment);
			return;
		}
		
		if(statement instanceof ExpressionStatement) {
			ExpressionStatement expressionStatement = (ExpressionStatement) statement;
			compileExpressionStatement(expressionStatement);
			return;
		}
		
		throw new UnsupportedOperationException();
	}
	
	private void compileExpressionStatement(ExpressionStatement expressionStatement) {
		Expression expression = expressionStatement.getExpression();
		compileExpression(expression);
	}

	private void compileAssignment(Assignment assignment) {
		Expression rhs = assignment.getRightHandSide();
		compileExpression(rhs);
		
		VariableReference variableReference = assignment.getLeftHandSide();
		Variable variable = variableReference.getVariable();
		
		addStoreInstruction(variable);
	}

	private void compileExpression(Expression expression) {
		if(expression instanceof LiteralValue) {
			LiteralValue literalValue = (LiteralValue) expression;
			int rawValue = literalValue.getRawValue();
			addLoadInstruction(rawValue);
			return;
		}
		
		if(expression instanceof VariableReference) {
			VariableReference variableReference = (VariableReference) expression;
			Variable variable = variableReference.getVariable();
			addLoadInstruction(variable);
			return;
		}
		
		if(expression instanceof AddExpression) {
			AddExpression addExpression = (AddExpression) expression;
			Expression operand1 = addExpression.getOperand1();
			Expression operand2 = addExpression.getOperand2();
			
			compileExpression(operand1);
			compileExpression(operand2);
			addAddInstruction();
			return;
		}
		
		throw new UnsupportedOperationException();
	}

	private LoadInstruction addLoadInstruction(int rawValue) {
		LoadInstruction loadInstruction = MILCreationUtil.createLoadInstruction(rawValue);
		instructions.add(loadInstruction);
		return loadInstruction;
	}
	
	private LoadInstruction addLoadInstruction(Variable variable) {
		String name = variable.getName();
		return addLoadInstruction(name);
	}
	
	private LoadInstruction addLoadInstruction(String address) {
		LoadInstruction loadInstruction = MILCreationUtil.createLoadInstruction(address);
		instructions.add(loadInstruction);
		return loadInstruction;
	}
	
	private StoreInstruction addStoreInstruction(Variable variable) {
		String name = variable.getName();
		return addStoreInstruction(name);
	}
	
	private StoreInstruction addStoreInstruction(String address) {
		StoreInstruction storeInstruction = MILCreationUtil.createStoreInstruction(address);
		instructions.add(storeInstruction);
		return storeInstruction;
	}
	
	private AddInstruction addAddInstruction() {
		AddInstruction addInstruction = MILCreationUtil.createAddInstruction();
		instructions.add(addInstruction);
		return addInstruction;		
	}
}
