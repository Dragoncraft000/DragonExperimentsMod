#include veil:fog
#include veil:space_helper
#include dragon_experiments:noise

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform float GameTime;

const float FogStart = -10;
const float FogEnd = 100;
uniform vec4 FogColor;
uniform int FogShape;

in vec2 texCoord;

out vec4 fragColor;


void main() {

    float dist = length(texCoord - vec2(0.5,0.5));
    vec2 offset = vec2(0.5,0.5) * sin(1 - dist);

    vec4 baseColor = texture(DiffuseSampler0, texCoord + offset);


    fragColor = baseColor;
}
