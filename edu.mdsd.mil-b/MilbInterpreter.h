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
    std::unordered_map<std::string, int> interpretByteCode(const std::vector<char>&);

private:
    std::stack<int> operandStack;
};


#endif //MIL_B_MILBINTERPRETER_H
