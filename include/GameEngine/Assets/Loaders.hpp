#pragma once
#include <fstream>

#include <vector>
#include <string>

std::string getExecutablePath();
std::string getExecutableDir();
std::string mergePaths(std::string pathA, std::string pathB);
bool checkIfFileExists (const std::string& filePath);

std::vector<char> readFile(const std::string& filename);