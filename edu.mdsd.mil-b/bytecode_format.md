# Bytecode format for MIL-B
This bytecode is supposed to be space efficient for the MIL language. It is intended to consist of different codes for the various instructions.

THe variables are given by their respective index with one char length (allowing maximum of 256 local variables).

The strings are the first group they are null terminated strings, which are preceded by an integer with their respective counts.

The jump labels are the second group. They are preceded by the number of the jump labels.

## Load instructions
01 - Load with a constant integer
02 - Load with a variable

## Store instructions
04 - store without parameters
05 - store into a variable

## Arithmetic instructions
- A0 - add expression
- A1 - sub expression
- A2 - mul expression
- A3 - div expression

## Jump instructions
- B0 - Unconditional Jump
- B1 - Conditional Jump

## Comparison operators
- C0 - Equal 
- C1 - Inequal
- C2 - LessThan
- C3 - LessThanEqual 
- C4 - GreaterThan 
- C5 - GreaterThanEqual 
