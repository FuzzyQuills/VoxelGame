mkdir -p build && cd build
cmake -DCMAKE_BUILD_TYPE=RelWithDebInfo ..
cmake --build .
cd ..
./build_shaders.sh
cp -r ./assets ./build/assets