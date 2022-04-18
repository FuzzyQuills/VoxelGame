#version 430 core
out vec4 FragColor;

in vec3 mvVertexNormal;
in vec3 mvVertexPos;

struct Attenuation{
    float constant;
    float linear;
    float exponent;
};

struct Material{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight{
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform vec3 camera_pos;


vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textCoord){
    ambientC = material.ambient * (1 - material.hasTexture); // + texture(texture_sampler, textCoord
    diffuseC = material.diffuse * (1 - material.hasTexture) + ambientC * material.hasTexture;
    specularC = material.specular * (1 - material.hasTexture) + ambientC * material.hasTexture;
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
    vec4 diffuseColor = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 specColor = vec4(0.0, 0.0, 0.0, 0.0);

    // Diffuse Light
    vec3 light_direction = light.position - position;
    vec3 to_light_source = normalize(light_direction);
    float diffuseFactor  = max(dot(normal, to_light_source), 0.0);
    diffuseColor = diffuseC * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    // specular light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_source = -to_light_source;
    vec3 reflected_light = normalize(reflect(from_light_source, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * light.intensity * specularFactor * material.reflectance * vec4(light.color, 1.0);

    // Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return (diffuseColor + specColor) / attenuationInv;
}

void main() {
    setupColors(material, vec2(0.0, 0.0));
    vec4 diffuseSpecularComp = calcPointLight(pointLight, mvVertexPos, mvVertexNormal);

    FragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
    //FragColor = vec4(mvVertexNormal, 1.0);
}
