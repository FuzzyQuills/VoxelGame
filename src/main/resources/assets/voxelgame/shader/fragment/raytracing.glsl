#version 430 core
out vec4 FragColor;

uniform vec2 WindowSize;
uniform vec3 CameraPosition;
uniform float FocalLength;
uniform mat4 CameraView;
uniform int viewDistance;

uniform int RenderDistance;
uniform int RegionDimensions;

uniform sampler3D VoxelTexture;

#define VFOV 90.0
#define PI 3.14159
#define DEG_TO_RAD PI / 180.0
#define MAX_STEPS 128

struct Ray{
    vec3 origin;
    vec3 direction;
};

vec3 MultiplyDirection(mat4 view, vec3 dir){
    return vec3(
        view[0][0] * dir.x + view[0][1] * dir.y + view[0][2] * dir.z,
        view[1][0] * dir.x + view[1][1] * dir.y + view[1][2] * dir.z,
        view[2][0] * dir.x + view[2][1] * dir.y + view[2][2] * dir.z
    );
}

vec3 ray_at(Ray ray, float t){
    return ray.origin + t * ray.direction;
}

bool hitVoxels(Ray r, out vec4 color){
    int step;
    float step_dist = 1.0;
    while(step < MAX_STEPS){

        vec4 voxel = texture(VoxelTexture, r.at() / ((float)RenderDistance * (float)RegionDimensions));
        step += 1 * (int)voxel.w;
    }

    return step != MAX_STEPS;
}

vec4 ray_color(Ray r){
    vec4 color = vec4(0.0);
    if(hitVoxels(r, color))
        return color;
    vec3 norm_dir = normalize(r.direction);
    float t = 0.5 * (norm_dir.y + 1.0);
    return (1.0 - t) * vec4(1.0, 1.0, 1.0, 1.0) + t * vec4(0.5, 0.7, 1.0, 1.0);
}

void main(){
    float aspectRatio = WindowSize.x / WindowSize.y;

    float theta = VFOV * DEG_TO_RAD;
    float h = tan(theta * 0.5);
    float viewportHeight = 2.0 * h;
    float viewportWidth = aspectRatio * viewportHeight;

    vec3 origin = vec3(0.0, 0.0, 0.0);


    vec3 horizontal = vec3(viewportWidth, 0.0, 0.0);
    vec3 vertical = vec3(0.0, viewportHeight, 0.0);
    vec3 lower_left_corner = origin - horizontal * 0.5 - vertical * 0.5 - vec3(0.0, 0.0, FocalLength);

    vec2 uv = gl_FragCoord.xy / WindowSize;
    vec3 direction = lower_left_corner + uv.x * horizontal + uv.y * vertical - origin;

    Ray r = Ray(CameraPosition, MultiplyDirection(CameraView, direction));
    FragColor = ray_color(r);
}