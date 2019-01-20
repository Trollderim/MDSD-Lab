//
// Created by phillipp on 17.01.19.
//

#include "MilbInterpreter.h"

#include <exception>
#include <cmath>
#include <iostream>
#include <sstream>

std::unordered_map<int, int> MilbInterpreter::interpretByteCode(const std::vector<unsigned char> &byteStream) {
    auto itAfterStrings = extractStringsFromBytestream(byteStream);
    stackFrames.push(StackFrame(itAfterStrings));

    for(auto it = itAfterStrings; it != byteStream.end(); it++) {
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
                auto value = stackFrames.top().getVariableRegisterValue(*(it + 1));
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
                stackFrames.top().setVariableRegisterValue(variableIndex, value);
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
            case JUMP_CONDITIONAL:
            {
                const auto comparison = popFromStack();
                if(comparison) {
                    it += 4;
                    break;
                }
            }
            case JUMP_UNCONDITIONAL:
            {
                const auto jumpTo = loadInteger(it + 1);
                it = itAfterStrings + (jumpTo - 1);
                break;
            }
            case EQUAL:
                applyComparison(OP_EQUAL);
                break;
            case INEQUAL:
                applyComparison(OP_INEQUAL);
                break;
            case LESSTHAN:
                applyComparison(OP_LESSTHAN);
                break;
            case LESSTHANEQUAL:
                applyComparison(OP_LESSTHANEQUAL);
                break;
            case GREATERTHAN:
                applyComparison(OP_GREATERTHAN);
                break;
            case GREATERTHANEQUAL:
                applyComparison(OP_GREATERTHANEQUAL);
                break;
            case YIELD:
            {
                const auto value = popFromStack();
                std::cout << value;
                break;
            }
            case PRINT:
            {
                const auto stringLocation = loadInteger(it + 1);
                std::cout << stringConstants[stringLocation];
                it += 4;
                break;
            }
            case CALL_OPERATION:
            {
                const auto jumpTo = loadInteger(it + 1);
                stackFrames.push(StackFrame(it + sizeof(int)));
                it = itAfterStrings + (jumpTo - 1);
                break;
            }
            case RETURN:
            {
                it = stackFrames.top().getReturnAddress();
                stackFrames.pop();
                break;
            }
            default:
                auto stream = std::stringstream();
                stream << "Unsuported operation in bytestream. Operator: ";
                stream << (int) *it;

                throw std::logic_error(stream.str());
        }
    }

    return stackFrames.top().getVariableRegister();
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

__gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>>
MilbInterpreter::extractStringsFromBytestream(const std::vector<unsigned char> &byteStream) {
    auto countStrings = loadInteger(byteStream.begin());

    auto it = byteStream.begin() + sizeof(int);

    for(auto i = 0; i < countStrings; i++) {
        const auto stringConstant = loadString(it);
        stringConstants.push_back(stringConstant);
        it += stringConstant.length() + 1;
    }

    return it;
}

std::string MilbInterpreter::loadString(
        __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> iterator) {
    std::stringstream stringConstant;

    while(*iterator != 0) {
        stringConstant.put(*iterator);
        iterator++;
    }

    return stringConstant.str();
}

void MilbInterpreter::applyComparison(ComparisonOperator comparison) {
    const auto op2 = popFromStack();
    const auto op1 = popFromStack();

    int result = 0;

    switch (comparison) {
        case OP_EQUAL:
            result = op1 == op2;
            break;
        case OP_INEQUAL:
            result = op1 != op2;
            break;
        case OP_LESSTHAN:
            result = op1 < op2;
            break;
        case OP_LESSTHANEQUAL:
            result = op1 <= op2;
            break;
        case OP_GREATERTHAN:
            result = op1 > op2;
            break;
        case OP_GREATERTHANEQUAL:
            result = op1 >= op2;
            break;
        default:
            break;
    }

    operandStack.push(result);
}
