mkdir -p build && cd build
cmake -DCMAKE_BUILD_TYPE=Debug ..
make
cd ..
./build_shaders.sh
cp -r ./assets ./build/assets