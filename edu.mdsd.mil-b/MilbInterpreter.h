//
// Created by phillipp on 17.01.19.
//

#ifndef MIL_B_MILBINTERPRETER_H
#define MIL_B_MILBINTERPRETER_H


#include <stack>
#include <unordered_map>
#include <vector>

class MilbInterpreter {
public:
    std::unordered_map<int, int> interpretByteCode(const std::vector<unsigned char>&);

private:
    enum Bytecodes {
        LOAD_CONSTANT = 0x1,
        LOAD_VARIABLE = 0x2,
        STORE_DUMP = 0x4,
        STORE_VARIABLE = 0x05,
        ARITHMETIC_ADD = 0xA0,
        ARITHMETIC_SUB = 0xA1,
        ARITHMETIC_MUL = 0xA2,
        ARITHMETIC_DIV = 0xA3,
        JUMP_UNCONDITIONAL = 0xB0,
        JUMP_CONDITIONAL = 0xB1,
        EQUAL = 0xC0,
        INEQUAL = 0xC1,
        LESSTHAN = 0xC2,
        LESSTHANEQUAL = 0xC3,
        GREATERTHAN = 0xC4,
        GREATERTHANEQUAL = 0xC5,
        YIELD = 0xD0,
        PRINT = 0xD1,
        CALL_OPERATION = 0xE0,
        RETURN = 0xE1,
    };

    enum ArithmeticOperation {
        ADD = 0,
        SUB = 1,
        MUL = 2,
        DIV = 3,
    };

    enum ComparisonOperator {
        OP_EQUAL = 0,
        OP_INEQUAL = 1,
        OP_LESSTHAN = 2,
        OP_LESSTHANEQUAL = 3,
        OP_GREATERTHAN = 4,
        OP_GREATERTHANEQUAL = 5,
    };

    std::stack<int> operandStack;
    std::unordered_map<int, int> variableRegister;

    std::vector<std::string> stringConstants;

    int loadInteger(__gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> iterator);

    int popFromStack();

    void appliyArithmeticOperation(ArithmeticOperation operation);

    __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> extractStringsFromBytestream(const std::vector<unsigned char> &byteStream);

    std::string loadString(
            __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> iterator);

    void applyComparison(ComparisonOperator comparison);
};


#endif //MIL_B_MILBINTERPRETER_H
