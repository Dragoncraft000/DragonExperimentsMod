#include veil:space_helper
#include dragon_experiments:space_utils
#include veil:blend

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform float GameTime;
vec3 LightPosition;
uniform vec3 ShipPos = vec3(1000,1000,0);
uniform vec3 ShipOrigin = vec3(0,0,0);
uniform vec4 ShipRotation = vec4(0,0,0,0);

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;

    vec3 rd = vec3(0,1,0);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro = ShipPos;
    if (depthSample < 1) {
        float diffLight = min(genLightSimple(ro,rd,LightPosition) + 0.4,1);
        diffLight = min(diffLight,1);
        diffLight = max(diffLight,1);
        fragColor = baseColor * diffLight;
        return;
    }
    fragColor = baseColor;
}
