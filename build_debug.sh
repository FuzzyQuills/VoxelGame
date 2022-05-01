mkdir -p build && cd build
cmake -DCMAKE_BUILD_TYPE=RelWithDebInfo ..
cmake --build . || exit /b
cd ..
./build_shaders.sh
cp -r ./assets ./build/assets