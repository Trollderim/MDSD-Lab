//
// Created by phillipp on 20.01.19.
//

#ifndef MIL_B_STACKFRAME_H
#define MIL_B_STACKFRAME_H


#include <unordered_map>
#include <vector>

class StackFrame {
public:
    explicit StackFrame(__gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> returnAddressNew) :
        returnAddress(returnAddressNew) {

    }

    __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> getReturnAddress() {
        return returnAddress;
    }

    std::unordered_map<int, int> getVariableRegister() {
        return variableRegister;
    }

    int getVariableRegisterValue(int index);
    void setVariableRegisterValue(int index, int value);

private:
    std::unordered_map<int, int> variableRegister;
    __gnu_cxx::__normal_iterator<const unsigned char *, std::vector<unsigned char, std::allocator<unsigned char>>> returnAddress;
};


#endif //MIL_B_STACKFRAME_H
