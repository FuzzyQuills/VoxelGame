vertshadersdir=`ls ./assets/shaders/glsl/*.vert`
fragshadersdir=`ls ./assets/shaders/glsl/*.frag`
targetdir="./assets/shaders/SPIR-V/"

shaders="$fragshadersdir $vertshadersdir"

for shader in $shaders
do
    target="$targetdir$(basename $shader).spv"
    if [ "$shader" -nt "$target" ]; then
        echo "compiling $shader -> $target"
        glslc $shader -o $target
    fi
done