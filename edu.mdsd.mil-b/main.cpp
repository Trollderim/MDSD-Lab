//
// Created by phillipp on 06.01.19.
//

#include <fstream>
#include <iostream>
#include <vector>
#include <zconf.h>
#include <array>

int main() {
    std::ifstream input("Test.milb", std::ifstream::binary);

    if(input) {
        input.seekg(0, input.end);
        const long length = input.tellg();
        input.seekg(0, input.beg);

        auto * buffer = new char [length];

        std::cout << "Reading " << length << " characters... ";

        input.read(buffer, length);

        if(input) {
            std::cout << "all characters read succesfully.";
        } else {
            std::cout << "error: only " << input.gcount() << " could be read";
        }

        const auto byteVector = std::vector<char>(buffer, buffer + length);

        for(auto byte : byteVector) {
            std::cout << (int) byte;
        }

        input.close();

    }

    return 0;
}