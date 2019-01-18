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
    std::unordered_map<int, int> interpretByteCode(const std::vector<char>&);

private:
    enum Bytecodes {
        LOAD_CONSTANT = 0x1,
        LOAD_VARIABLE = 0x2,
        STORE_DUMP = 0x4,
        STORE_VARIABLE = 0x05,
        ARITHMETIC_ADD = 0xA0,
    };

    std::stack<int> operandStack;
    std::unordered_map<int, int> variableRegister;

    int loadInteger(__gnu_cxx::__normal_iterator<const char *, std::vector<char, std::allocator<char>>> iterator);
};


#endif //MIL_B_MILBINTERPRETER_H
