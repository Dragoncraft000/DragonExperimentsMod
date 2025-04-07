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
uniform float AtmosphereMieCoeffiecent = (21e-3 * 0.5) / 1.5;
uniform float AtmosphereRayleighScaleHeight = 8;
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
        return;

    vec2 uv = texCoord - vec2(0.5,0.5);
    vec3 rd = viewDirFromUv(texCoord);
    vec3 ro =  VeilCamera.CameraPosition;

    float standardSize = 127.;
    float sizeMod = PlanetSize / defaultScaleSize;
    float atmoSize = PlanetSize + AtmosphereSize * sizeMod;
    float sceneDist = length(ro - worldPos.rgb);
    vec2 atmoHit = rsi(ro - PlanetPos,rd,atmoSize);
    if (atmoHit.y > sceneDist && depthSample < 1) {
        return;
    }
    if (atmoHit.x > atmoHit.y) {
       return;
    }

    float atmoT = abs(atmoHit.x);

    vec3 atmoP = ro + rd * atmoT;

        vec3 color = atmosphere(
            rd,           // normalized ray direction
            atmoP - PlanetPos,               // ray origin
            LightPosition - PlanetPos,                        // position of the sun
            SunBrightness,                           // intensity of the sun
            PlanetSize,                         // radius of the planet in meters
            atmoSize - AtmosphereCompression,                         // radius of the atmosphere in meters
            (AtmosphereRayleighCoeffiecents * AtmosphereBrightness) / sizeMod, // Rayleigh scattering coefficient
            AtmosphereMieCoeffiecent / sizeMod,                          // Mie scattering coefficient
            AtmosphereRayleighScaleHeight * sizeMod,                            // Rayleigh scale height
            AtmosphereMieScaleHeight * sizeMod,                          // Mie scale height
            0.758                           // Mie preferred scattering direction
        );
        color = 1.0 - exp(-1.0 * color);
        fragColor.rgb = fragColor.rgb + color ;
        //fragColor.rgb = vec3(getPlanetAtmoDistance(atmoP,PlanetPos,PlanetSize,7));

}








