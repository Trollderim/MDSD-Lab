# Bytecode format for MIL-B
This bytecode is supposed to be space efficient for the MIL language. It is intended to consist of different codes for the various instructions.

The arguments are given by a trailing argument number. This points to the number of the given variable. Those are enumerated in declaration order.

E.g: If you want to address the variable a, which is the first, you have to write: <load variable bytecode> 00.

To allow more than 256 variables, there will be an extension in the future. For now there is a limitation for 128 local variables + parameters.

To store constants, there will be a reference of constants at the end of the program. This is going to be there also for strings etc.

The beginning is done with a simple instruction set. This is expanded in the future.

## Load instructions
01 - Load with a constant integer
02 - Load with a variable

## Store instructions
04 - store without parameters
05 - store into a variable

## Arithmetic instructions
0A - add expression

## Special sections
F0 - 4 byte constants
F1 - string constants