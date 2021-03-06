//
// Created by phillipp on 06.01.19.
//

#include <fstream>
#include <iostream>
#include <vector>
#include <zconf.h>
#include <array>
#include "MilbInterpreter.h"
#include "PrimeFinder.h"

int main() {
    clock_t begin = clock();

    std::ifstream input("../Test.milb", std::ifstream::binary);

    if(input) {
        input.seekg(0, input.end);
        const long length = input.tellg();
        input.seekg(0, input.beg);

        auto * buffer = new char [length];

        std::cout << "Reading " << length << " characters... ";

        input.read(buffer, length);

        if(input) {
            std::cout << "all characters read succesfully." << std::endl;
        } else {
            std::cout << "error: only " << input.gcount() << " could be read" << std::endl;
            return 0;
        }

        const auto byteVector = std::vector<unsigned char>(buffer, buffer + length);

        for(auto byte : byteVector) {
            std::cout << (int) byte;
        }
        std::cout << std::endl;

        input.close();

        auto interpreter = MilbInterpreter();
        auto result = interpreter.interpretByteCode(byteVector);

        for(const auto entry : result) {
            std::cout << entry.first << " = " << entry.second << std::endl;
        }
    }

    //PrimeFinder::findPrimes();

    clock_t end = clock();
    double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
    std::cout << "Time: " << elapsed_secs << std::endl;

    return 0;
}