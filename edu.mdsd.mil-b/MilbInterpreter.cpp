//
// Created by phillipp on 17.01.19.
//

#include "MilbInterpreter.h"

#include <exception>
#include <cmath>
#include <sstream>

std::unordered_map<int, int> MilbInterpreter::interpretByteCode(const std::vector<unsigned char> &byteStream) {
    for(auto it = byteStream.begin(); it != byteStream.end(); it++) {
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
                appliyArithmeticOperation(ADD);
                break;
            case Bytecodes::ARITHMETIC_SUB:
                appliyArithmeticOperation(SUB);
                break;
            case Bytecodes::ARITHMETIC_DIV:
                appliyArithmeticOperation(DIV);
                break;
            case Bytecodes::ARITHMETIC_MUL:
                appliyArithmeticOperation(MUL);
                break;
            default:
                auto stream = std::stringstream();
                stream << "Unsuported operation in bytestream. Operator: ";
                stream << (int) *it;

                throw std::logic_error(stream.str());
        }
    }

    return variableRegister;
}

void MilbInterpreter::appliyArithmeticOperation(ArithmeticOperation operation) {
    const auto op2 = popFromStack();
    const auto op1 = popFromStack();

    switch (operation) {
        case ADD:
            operandStack.push(op1 + op2);
            break;
        case SUB:
            operandStack.push(op1 - op2);
            break;
        case MUL:
            operandStack.push(op1 * op2);
            break;
        case DIV:
            operandStack.push(op1 / op2);
            break;
        default:
            break;
    }
}

int MilbInterpreter::popFromStack() {
    const auto op1 = operandStack.top();
    operandStack.pop();
    return op1;
}

int MilbInterpreter::loadInteger(
        __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> iterator) {
    int value = (iterator[0] << 24) | (iterator[1] << 16) | (iterator[2] << 8) | (iterator[3]);

    return value;
}
