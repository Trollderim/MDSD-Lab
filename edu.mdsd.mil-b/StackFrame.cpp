//
// Created by phillipp on 20.01.19.
//

#include "StackFrame.h"

void StackFrame::setVariableRegisterValue(int index, int value) {
    variableRegister[index] = value;
}

int StackFrame::getVariableRegisterValue(int index) {
    return variableRegister[index];
}
