SYNTAXDEF mil
FOR <http://mdsd.edu/mil/1.0>
START MILModel

OPTIONS {
	reloadGeneratorModel = "true";
	defaultTokenName = "IDENTIFIER_TOKEN";
	
	overrideLaunchConfigurationDelegate = "false";
}

TOKENS {
	DEFINE IDENTIFIER_TOKEN $('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'_'|'0'..'9')*$;
	DEFINE INTEGER_TOKEN $('-')?('0'..'9')+$;
	DEFINE STRING_TOKEN $('"')(.)*('"')$;
	
	DEFINE SL_COMMENT $'//'(~('\n'|'\r'|'\uffff'))* $;
	DEFINE ML_COMMENT $'/*'.*'*/'$;
}

TOKENSTYLES {
	"IDENTIFIER_TOKEN" COLOR #6A3E3E;
	"INTEGER_TOKEN" COLOR #0000C0;
	"STRING_TOKEN" COLOR #0000C0;
	"SL_COMMENT", "ML_COMMENT" COLOR #3F7F5F;
}

RULES {
	MILModel ::= (instructions !0)*;
	
	LabelInstruction ::= name[] ":";
	
	LoadInstruction ::= "lod" #1 value;
	StoreInstruction ::= "sto" #1 registerReference?;
	
	AddInstruction ::= "add";
	SubInstruction ::= "sub";
	MulInstruction ::= "mul";
	DivInstruction ::= "div";
	
	NegateInstruction ::= "neg";
	
	ConditionalJumpInstruction ::= "jpc" jumpTo[];
	UnconditionalJumpInstruction ::= "jmp" jumpTo[];
	
	EqualInstruction ::= "eq" ;
	NotEqualInstruction ::= "neq";
	LessThanInstruction ::= "lt";
	LessThanEqualInstruction ::= "leq";
	GreaterThanInstruction ::= "gt";
	GreaterThanEqualInstruction ::= "geq";
	
	YieldInstruciton ::= "yld";
	PrintInstruction ::= "prt" output[STRING_TOKEN];
	
	CallInstruction ::= "cal" operationName[];
	ReturnInstruction ::= "ret";
	
	ConstantInteger ::= rawValue[INTEGER_TOKEN];
	RegisterReference ::= address[];
}
