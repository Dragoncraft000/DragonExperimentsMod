#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D SolidDepthSampler;
uniform float GameTime;

uniform float PlanetSize = 1000;
uniform float AtmosphereSize = 1100;
uniform vec3 PlanetPos = vec3(0,0,0);
uniform vec3 LightPosition = vec3(1000,0,0);
uniform float SunBrightness = 22;
uniform float AtmosphereBrightness = 0.9;
uniform float AtmosphereCompression = 0;
uniform vec3 AtmosphereRayleighCoeffiecents = vec3(5.5e-3, 13.0e-3, 22.4e-3);
uniform float AtmosphereMieCoeffiecent = (21e-100 * 0.5) / 1.5;
uniform float AtmosphereRayleighScaleHeight = 10;
uniform float AtmosphereMieScaleHeight = 8;
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

    vec2 uv = texCoord - vec2(0.5,0.5);
    vec3 rd = viewDirFromUv(texCoord);
    vec3 ro =  VeilCamera.CameraPosition + vec3(0,900,0);
    float sceneDist = length(ro - worldPos.rgb);
    vec2 atmoHit = rsi(ro,rd,1100);
    if (atmoHit.y < 0) {
        return;
    }
    if (atmoHit.y > sceneDist && depthSample < 1) {
        return;
    }
    if (atmoHit.x > atmoHit.y) {
        return;
    }
    float atmoT = (atmoHit.x);
    vec3 atmoP = ro + rd * atmoT;

    vec3 color = atmosphere(
        rd,           // normalized ray direction
        ro,               // ray origin
        vec3(1000,0,0),                        // position of the sun
        22,                           // intensity of the sun
        1000,                         // radius of the planet in meters
        1100,                         // radius of the atmosphere in meters
        AtmosphereRayleighCoeffiecents, // Rayleigh scattering coefficient
        AtmosphereMieCoeffiecent,                          // Mie scattering coefficient
        AtmosphereRayleighScaleHeight,                            // Rayleigh scale height
        AtmosphereMieScaleHeight,                          // Mie scale height
        0.758                           // Mie preferred scattering direction
    );
    color = 1.0 - exp(-1.0 * color);
    fragColor.rgb = fragColor.rgb + color ;
        //fragColor.rgb = vec3(getPlanetAtmoDistance(atmoP,PlanetPos,PlanetSize,7));

}








