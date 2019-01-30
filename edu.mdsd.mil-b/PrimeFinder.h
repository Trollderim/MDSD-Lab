//
// Created by phillipp on 20.01.19.
//

#ifndef MIL_B_PRIMEFINDER_H
#define MIL_B_PRIMEFINDER_H


#include <iostream>

class PrimeFinder {
public:
    static void findPrimes() {
        int foundPrimes = 0;
        int i = 2;

        while(foundPrimes < 1000) {
            if(isPrime(i)) {
                std::cout << "i = " << i << std::endl;
                foundPrimes++;
            }

            i++;
        }
    }

private:

    static int mod(int x, int m) {
        while(x >= m) {
            x = x - m;
        }

        return x;
    }

    static bool isPrime(int n) {
        if(n <= 1) {
            return false;
        }

        for(int i = 2; i < n; i++) {
            if(mod(n, i) == 0) {
                return false;
            }
        }

        return true;
    }
};


#endif //MIL_B_PRIMEFINDER_H
