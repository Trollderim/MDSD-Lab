package edu.mdsd.mpl.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.CallInstruction;
import edu.mdsd.mil.CompareInstruction;
import edu.mdsd.mil.ConditionalJumpInstruction;
import edu.mdsd.mil.DivInstruction;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MulInstruction;
import edu.mdsd.mil.PrintInstruction;
import edu.mdsd.mil.ReturnInstruction;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;
import edu.mdsd.mil.UnconditionalJumpInstruction;
import edu.mdsd.mil.YieldInstruciton;
import edu.mdsd.mil.compiler.MILToMILBCompiler;
import edu.mdsd.mpl.AddExpression;
import edu.mdsd.mpl.ArithmeticExpression;
import edu.mdsd.mpl.Assignment;
import edu.mdsd.mpl.Block;
import edu.mdsd.mpl.ComparisonExpression;
import edu.mdsd.mpl.ComparisonOperator;
import edu.mdsd.mpl.DivExpression;
import edu.mdsd.mpl.Expression;
import edu.mdsd.mpl.ExpressionStatement;
import edu.mdsd.mpl.ForLoop;
import edu.mdsd.mpl.ForLoopDirection;
import edu.mdsd.mpl.FunctionalUnit;
import edu.mdsd.mpl.IfStatement;
import edu.mdsd.mpl.LiteralValue;
import edu.mdsd.mpl.MPLModel;
import edu.mdsd.mpl.MulExpression;
import edu.mdsd.mpl.Operation;
import edu.mdsd.mpl.OperationExpression;
import edu.mdsd.mpl.ParenthesisExpression;
import edu.mdsd.mpl.Program;
import edu.mdsd.mpl.ReturnStatement;
import edu.mdsd.mpl.Statement;
import edu.mdsd.mpl.SubExpression;
import edu.mdsd.mpl.Trace;
import edu.mdsd.mpl.Variable;
import edu.mdsd.mpl.VariableDeclaration;
import edu.mdsd.mpl.VariableReference;
import edu.mdsd.mpl.WhileLoop;
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
		
		MILToMILBCompiler compiler = new MILToMILBCompiler();
		List<Byte> milbModel = compiler.compile(milModel);
		System.out.print(milbModel);
	}
	
	public MILModel compile(Resource mplResource) {
		MPLModel mplModel = (MPLModel) mplResource.getContents().get(0);
		return compile(mplModel);
	}
	
	public MILModel compile(MPLModel model) {
		MILModel milModel = MILCreationUtil.createMILModel();
		
		instructions = milModel.getInstructions();
		 
		Program program = model.getProgram();
		compileFunctionalUnit(program);
		
		LabelInstruction endProgramLabel = MILCreationUtil.createLabelInstruction("EndProgram");
		addUnconditionalJumpInstruction(endProgramLabel);
		 
		List<Operation> operations = model.getOperations();
		 
		for(Operation operation : operations) {
			compileOperation(operation);
			 
			FunctionalUnit unit = (FunctionalUnit) operation;
			compileFunctionalUnit(unit);
		}
		
		addJumpLabel(endProgramLabel);
		
		return milModel;
	}

	private void compileOperation(Operation operation) {
		LabelInstruction label = MILCreationUtil.createLabelInstruction(operation.getName());
		addJumpLabel(label);
		
		for(Variable parameter : operation.getParameters()) {
			addStoreInstruction(parameter);
		}		
	}

	private void compileFunctionalUnit(FunctionalUnit unit) {
		List<VariableDeclaration> variableDeclarations = unit.getVariableDeclarations();
		
		for(VariableDeclaration variableDeclaration : variableDeclarations) {
			compileVariableDeclaration(variableDeclaration);
		}
		
		Block functionalBody = unit.getFunctionalBody();
		if(functionalBody != null) {
			compileBlock(functionalBody);
		}
	}
	
	private void compileBlock(Block block) {
		if(block != null) {
			List<Statement> statements = block.getStatements();
			
			for(Statement statement : statements) {
				compileStatements(statement);
			}
		}
	}

	private void compileVariableDeclaration(VariableDeclaration variableDeclaration) {
		Variable variable = variableDeclaration.getVariable();
		
		if(variableDeclaration.getVariableInitialization() != null) {
			compileExpression(variableDeclaration.getVariableInitialization());
		} else {
			addLoadInstruction(0);
		}
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
		
		if(statement instanceof IfStatement) {
			IfStatement ifStatement = (IfStatement) statement;
			compileIfStatement(ifStatement);
			return;
		}
		
		if(statement instanceof ForLoop) {
			ForLoop forLoop = (ForLoop) statement;
			compileForLoop(forLoop);
			return;
		}
		
		if(statement instanceof WhileLoop) {
			WhileLoop whileLoop = (WhileLoop) statement;
			compileWhileLoop(whileLoop);
			return;
		}
		
		if(statement instanceof Trace) {
			Trace trace = (Trace) statement;
			compileTrace(trace);
			return;
		}
		
		if(statement instanceof ReturnStatement) {
			ReturnStatement returnStatement = (ReturnStatement) statement;
			compileReturnStatement(returnStatement);
			return;
		}
		
		throw new UnsupportedOperationException();
	}

	private void compileIfStatement(IfStatement ifStatement) {
		String uniqueID = createUniqueIdentifier();
		String endifMarker = "endif_" + uniqueID;
		String elseMarker = "else_" + uniqueID;
		
		LabelInstruction endifLabel = MILCreationUtil.createLabelInstruction(endifMarker);
		LabelInstruction elseLabel = MILCreationUtil.createLabelInstruction(elseMarker);
		
		compileComparison(ifStatement.getCondition());		
		addConditionalJumpInstruction(elseLabel);		
		compileBlock(ifStatement.getThenBlock());
		addUnconditionalJumpInstruction(endifLabel);		
		addJumpLabel(elseLabel);		
		compileBlock(ifStatement.getElseBlock());
		addJumpLabel(endifLabel);
	}
	
	
	
	private String createUniqueIdentifier() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	private void compileComparison(ComparisonExpression condition) {
		compileExpression(condition.getLeftHandSide());
		compileExpression(condition.getRightHandSide());
		
		addComparisonInstruction(condition.getComparisonOperator());
	}
	
	private void compileForLoop(ForLoop forLoop) {
		String uniqueID = createUniqueIdentifier();
		String headMarker = "headFor_" + uniqueID;
		String endForMarker = "endFor_" + uniqueID;
		
		LabelInstruction headLabel = MILCreationUtil.createLabelInstruction(headMarker);
		LabelInstruction endForLabel = MILCreationUtil.createLabelInstruction(endForMarker);
		
		compileAssignment(forLoop.getCounter());	
		addJumpLabel(headLabel);
		addLoadInstruction(forLoop.getCounter().getLeftHandSide().getVariable());
		compileExpression(forLoop.getBound());
		
		if(forLoop.getDirection() == ForLoopDirection.UP) {
			addComparisonInstruction(ComparisonOperator.LESS_THAN_EQUAL);
		} else {
			addComparisonInstruction(ComparisonOperator.GREATER_THAN_EQUAL);
		}
		
		addConditionalJumpInstruction(endForLabel);
		compileBlock(forLoop.getBody());
		
		incrementCounter(forLoop);
		
		addUnconditionalJumpInstruction(headLabel);
		addJumpLabel(endForLabel);
	}

	private void incrementCounter(ForLoop forLoop) {
		if(forLoop.getDirection() == ForLoopDirection.UP) {
			addLoadInstruction(1);
		} else {
			addLoadInstruction(-1);
		}
		addLoadInstruction(forLoop.getCounter().getLeftHandSide().getVariable());
		addAddInstruction();
		addStoreInstruction(forLoop.getCounter().getLeftHandSide().getVariable());
	}
	
	private void compileWhileLoop(WhileLoop whileLoop) {
		String uniqueID = createUniqueIdentifier();
		String headMarker = "headWhile_" + uniqueID;
		String endWhileMarker = "endWhile_" + uniqueID;
		
		LabelInstruction headLabel = MILCreationUtil.createLabelInstruction(headMarker);
		LabelInstruction endForLabel = MILCreationUtil.createLabelInstruction(endWhileMarker);
		
		addJumpLabel(headLabel);
		compileComparison(whileLoop.getCondition());
		
		addConditionalJumpInstruction(endForLabel);
		compileBlock(whileLoop.getBody());
		
		addUnconditionalJumpInstruction(headLabel);
		addJumpLabel(endForLabel);
	}
	
	private void compileTrace(Trace trace) {
		Variable variable = trace.getVarToPrint().getVariable();
		String variableName = variable.getName();
		
		addPrintInstruction("\"" + variableName + " = " + "\"");
		addLoadInstruction(variable);
		addYieldInstruction();
		addPrintInstruction("\"\\n\"");
	}

	private void compileExpressionStatement(ExpressionStatement expressionStatement) {
		Expression expression = expressionStatement.getExpression();
		compileExpression(expression);
		
		addStoreInstruction("");
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
		
		if(expression instanceof ArithmeticExpression) {
			ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;
			Expression operand1 = arithmeticExpression.getOperand1();
			Expression operand2 = arithmeticExpression.getOperand2();
			
			compileExpression(operand1);
			compileExpression(operand2);
			
			compileArithmeticExpression(arithmeticExpression);
			return;
		}
		
		if(expression instanceof ParenthesisExpression) {
			ParenthesisExpression parenthesisExpression = (ParenthesisExpression) expression;
			if(parenthesisExpression != null) {
				compileExpression(parenthesisExpression.getOperand());
			}
			
			return;
		}
		
		if(expression instanceof OperationExpression) {
			OperationExpression operationExpression = (OperationExpression) expression;
			
			List<Expression> parameters = operationExpression.getParameters();
			
			for(int i = parameters.size() - 1; i >= 0; i--) {
				compileExpression(parameters.get(i));
			}
			
			addCallInstruction(operationExpression);
			
			return;
		}
		
		throw new UnsupportedOperationException();
	}

	private void compileArithmeticExpression(ArithmeticExpression arithmeticExpression) {
		if(arithmeticExpression instanceof AddExpression) {
			addAddInstruction();
		} else if(arithmeticExpression instanceof SubExpression) {
			addSubInstruction();
		} else if(arithmeticExpression instanceof MulExpression) {
			addMulInstruction();
		} else if(arithmeticExpression instanceof DivExpression) {
			addDivInstruction();
		} else {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private void compileReturnStatement(ReturnStatement returnStatement) {
		if(returnStatement.getReturnValue() != null) {
			compileExpression(returnStatement.getReturnValue());
		} else {
			addLoadInstruction(0);
		}
		
		addReturnInstruction(returnStatement);
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
	
	private SubInstruction addSubInstruction() {
		SubInstruction subInstruction = MILCreationUtil.createSubInstruction();
		instructions.add(subInstruction);
		return subInstruction;		
	}
	
	private DivInstruction addDivInstruction() {
		DivInstruction divInstruction = MILCreationUtil.createDivInstruction();
		instructions.add(divInstruction);
		return divInstruction;		
	}
	
	private MulInstruction addMulInstruction() {
		MulInstruction mulInstruction = MILCreationUtil.createMulInstruction();
		instructions.add(mulInstruction);
		return mulInstruction;		
	}
	
	private CompareInstruction addComparisonInstruction(ComparisonOperator comparisonOperator) {
		CompareInstruction instruction;
		
		switch (comparisonOperator) {
		case EQUAL:
			instruction = MILCreationUtil.createEqualInstruction();
			break;
		case INEQUAL:
			instruction = MILCreationUtil.createNotEqualInstruction();
			break;
		case GREATER_THAN:
			instruction = MILCreationUtil.createGreaterThanInstruction();
			break;
		case GREATER_THAN_EQUAL:
			instruction = MILCreationUtil.createGreaterThanEqualInstruction();
			break;
		case LESS_THAN:
			instruction = MILCreationUtil.createLessThanInstruction();
			break;
		case LESS_THAN_EQUAL:
			instruction = MILCreationUtil.createLessThanEqualInstruction();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		
		instructions.add(instruction);
		return instruction;
	}
	
	private ConditionalJumpInstruction addConditionalJumpInstruction(LabelInstruction jumpTo) {
		ConditionalJumpInstruction jpcInstruction = MILCreationUtil.createConditionalJumpInstruction(jumpTo);
		instructions.add(jpcInstruction);
		return jpcInstruction;
	}
	
	private UnconditionalJumpInstruction addUnconditionalJumpInstruction(LabelInstruction jumpTo) {
		UnconditionalJumpInstruction jmpInstruction = MILCreationUtil.createUnconditionalJumpInstruction(jumpTo);
		instructions.add(jmpInstruction);
		return jmpInstruction;
	}
	
	private LabelInstruction addJumpLabel(LabelInstruction jumpLabel) {
		instructions.add(jumpLabel);
		return jumpLabel;
	}
	
	private PrintInstruction addPrintInstruction(String output) {
		PrintInstruction print = MILCreationUtil.createPrintInstruction(output);
		instructions.add(print);
		return print;
	}
	
	private YieldInstruciton addYieldInstruction() {
		YieldInstruciton yield = MILCreationUtil.createYieldInstruction();
		instructions.add(yield);
		return yield;
	}
	
	private ReturnInstruction addReturnInstruction(ReturnStatement returnStatement) {
		ReturnInstruction returnInstruction = MILCreationUtil.createReturnInstruction();
		instructions.add(returnInstruction);
		
		return returnInstruction;		
	}
	
	private CallInstruction addCallInstruction(OperationExpression operationExpression) {
		LabelInstruction label = MILCreationUtil.createLabelInstruction(operationExpression.getOperation().getName());
		CallInstruction callInstruction = MILCreationUtil.createCallInstruction(label);
		instructions.add(callInstruction);
		
		return callInstruction;		
	}
}
