#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D SolidDepthSampler;
uniform float GameTime;

in vec2 texCoord;

const float defaultScaleSize = 127.;
out vec4 fragColor;
void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    vec4 pos = screenToLocalSpace(texCoord, depthSample);
    vec4 worldPos = screenToWorldSpace(texCoord,depthSample);
    fragColor.rgb = baseColor.rgb;
    fragColor.a = 1;

    //fragColor = vec4(length(pos) * 0.01);
    float depth = pow(worldToScreenSpace(worldPos).z,100);
    //fragColor = vec4(depth -depthSample) * 100;
    fragColor = vec4(pow(depthSample,100));
}








