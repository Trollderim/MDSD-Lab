cmake_minimum_required (VERSION 3.7)
project (MIL-B)

set (CMAKE_CXX_STANDARD 17)

set (CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -Os")

add_executable(MIL-B-Interpreter main.cpp MilbInterpreter.cpp MilbInterpreter.h StackFrame.cpp StackFrame.h PrimeFinder.cpp PrimeFinder.h)