#include veil:space_helper
#include dragon_experiments:space_utils

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D BackgroundTexture;
uniform vec4 ShipRotation = vec4(0,0,0,0);
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    vec4 pos = screenToLocalSpace(texCoord, depthSample);

    vec3 rd = normalize(pos.xyz);
    rd = rotateByQuaternion(rd,ShipRotation);
    if (depthSample < 1) {

        fragColor = baseColor;
        gl_FragDepth = depthSample;
        return;
    }
    gl_FragDepth = 1;
    vec2 planetTexCoord = normalToSpherical(rd);
    fragColor = texture(BackgroundTexture, planetTexCoord);
}
