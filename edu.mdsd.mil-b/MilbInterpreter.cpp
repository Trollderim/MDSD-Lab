//
// Created by phillipp on 17.01.19.
//

#include "MilbInterpreter.h"

#include <exception>

std::unordered_map<std::string, int> MilbInterpreter::interpretByteCode(const std::vector<char> &byteStream) {
    auto resultMap = std::unordered_map<std::string, int>();

    for(auto it = byteStream.begin(); it != byteStream.end(); it++) {
        switch (*it) {
            default:
                throw std::logic_error("Unsuported operation in bytestream.");
        }
    }

    return resultMap;
}
