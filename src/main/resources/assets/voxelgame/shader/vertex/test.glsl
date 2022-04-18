#version 430 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNorm;

out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vec4 mvPos = model * vec4(aPos, 1.0);
    gl_Position = projection * view * mvPos;

    mvVertexNormal = aNorm;//normalize(model * vec4(aNorm, 0.0)).xyz;

    mvVertexPos = mvPos.xyz;
}
