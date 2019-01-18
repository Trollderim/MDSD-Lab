//
// Created by phillipp on 17.01.19.
//

#include "MilbInterpreter.h"

#include <exception>
#include <cmath>

std::unordered_map<int, int> MilbInterpreter::interpretByteCode(const std::vector<char> &byteStream) {
    for(auto it = byteStream.begin(); it != byteStream.end(); it++) {
        if(*it == -96) {
            break;
        }

        switch (*it) {
            case Bytecodes::LOAD_CONSTANT:
            {
                auto value = loadInteger(it + 1);
                operandStack.push(value);
                it += 4;
            }
                break;
            case Bytecodes::LOAD_VARIABLE:
            {
                auto value = variableRegister[*(it + 1)];
                operandStack.push(value);
                it++;
            }
                break;
            case Bytecodes::STORE_DUMP:
            {
                operandStack.pop();
            }
            break;
            case Bytecodes::STORE_VARIABLE:
            {
                const auto value = operandStack.top();
                operandStack.pop();
                const auto variableIndex = *(it + 1);
                variableRegister[variableIndex] = value;
                it++;
            }
            break;
            case Bytecodes::ARITHMETIC_ADD:
            {
                const auto op1 = operandStack.top();
                operandStack.pop();
                const auto op2 = operandStack.top();
                operandStack.pop();
                operandStack.push(op1 + op2);
            }
            break;
            default:
                throw std::logic_error("Unsuported operation in bytestream.");
        }
    }

    return variableRegister;
}

int MilbInterpreter::loadInteger(
        __gnu_cxx::__normal_iterator<const char *, std::vector<char, std::allocator<char>>> iterator) {
    int value = (iterator[0] << 24) | (iterator[1] << 16) | (iterator[2] << 8) | (iterator[3]);

    return value;
}
