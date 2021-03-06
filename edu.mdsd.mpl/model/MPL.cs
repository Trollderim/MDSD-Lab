SYNTAXDEF mpl
FOR <http://mdsd.edu/mpl/1.0>
START MPLModel

OPTIONS {
	reloadGeneratorModel = "true";
	defaultTokenName = "IDENTIFIER_TOKEN";
	overrideBuilder = "false";
}

TOKENS {
	DEFINE IDENTIFIER_TOKEN $('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'_'|'0'..'9')*$;
	DEFINE INTEGER_TOKEN $('-')?('0'..'9')+$;
	
	DEFINE SL_COMMENT $'//'(~('\n'|'\r'|'\uffff'))* $;
	DEFINE ML_COMMENT $'/*'.*'*/'$;
}

TOKENSTYLES {
	"IDENTIFIER_TOKEN" COLOR #6A3E3E;
	"INTEGER_TOKEN" COLOR #0000C0;
	"SL_COMMENT", "ML_COMMENT" COLOR #3F7F5F;
}

RULES {
	MPLModel ::= program !1 (operations)*;
	
	Program ::= "Program" #1 name[] (!1"Variables" !1 variableDeclarations ("," #1 variableDeclarations)* ".")? 
		functionalBody
		"End" ".";
		
	Function ::= "Function" #1 name[] "(" (!1 parameters ("," #1 parameters)*)? ")"
		(!1"Variables" !1 variableDeclarations ("," #1 variableDeclarations)* ".")?
		functionalBody
		"End" ".";
		
	Procedure ::= "Procedure" #1 name[] "(" (!1 parameters ("," #1 parameters)*)? ")"
		(!1"Variables" !1 variableDeclarations ("," #1 variableDeclarations)* ".")?
		functionalBody
		"End" ".";
		
	Block ::= (!1 statements)*;
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	OperationExpression ::= operation[] "(" (!1 parameters ("," #1 parameters)*)? ")";
	
	VariableDeclaration ::= variable (":=" variableInitialization)?;
	Variable ::= name[];
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	VariableReference ::= variable[];
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	LiteralValue ::= rawValue[INTEGER_TOKEN]; 
	
	@Operator(type="binary_left_associative", weight="1", superclass="Expression")
	AddExpression ::= operand1 #1 "+" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="1", superclass="Expression")
	SubExpression ::= operand1 #1 "-" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="2", superclass="Expression")
	DivExpression ::= operand1 #1 "/" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="2", superclass="Expression")
	MulExpression ::= operand1 #1 "*" #1 operand2;
	
	@Operator(type="unary_prefix", weight="4", superclass="Expression")
	UnaryMinusExpression ::= "-" operand;
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	ParenthesisExpression ::= "(" operand ")";
	
//	@Operator(type="primitive", weight="5", superclass="Statement")
	Assignment ::= leftHandSide #1 ":=" #1 rightHandSide ".";
	
	ExpressionStatement ::= expression ".";
	
	IfStatement ::= "If" "(" condition ")" "Then" thenBlock ("Else" elseBlock)? "End" ".";
	
	WhileLoop ::= "While" "(" condition ")" "Do" body "End" ".";
	
	ComparisonExpression ::= leftHandSide 
								comparisonOperator [EQUAL : "=", INEQUAL : "<>", LESS_THAN : "<", LESS_THAN_EQUAL: "<=", GREATER_THAN: ">", GREATER_THAN_EQUAL: ">="] 
								rightHandSide;
								
	ForLoop ::= "For" counter (direction[UP : "Up", DOWN: "Down"])? "To" bound "Do" body "End" ".";
	
//	@Operator(type="primitive", weight="5", superclass="Statement")
	Trace ::= "Trace" "(" varToPrint ")" ".";
	
	ReturnStatement ::= "Return" returnValue? ".";
}
