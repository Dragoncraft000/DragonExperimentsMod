#include veil:fog
#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

const float FogStart = 1;
const float FogEnd = 100;
uniform vec4 FogColor;
uniform int FogShape;
uniform float GameTime;

in vec2 texCoord;

out vec4 fragColor;

vec3 viewPosFromDepthSample(float depth, vec2 uv) {
    vec4 positionCS = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    vec4 positionVS = VeilCamera.IProjMat * positionCS;
    positionVS /= positionVS.w;

    return positionVS.xyz;
}


void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    vec3 pos = viewPosFromDepthSample(depthSample, texCoord);

    float vertexDistance = fog_distance(pos, FogShape);

    float dist = abs(VeilCamera.CameraPosition.y - 260);

    float atmoFade = 1.5 - dist / 80;
    atmoFade = max(atmoFade,0);
    if (depthSample == 1) {
        fragColor = mix(baseColor,vec4(1.),atmoFade);
        return;
    }
    fragColor = mix(baseColor,linear_fog(baseColor, vertexDistance, FogStart, FogEnd,vec4(1.)),atmoFade);
}
