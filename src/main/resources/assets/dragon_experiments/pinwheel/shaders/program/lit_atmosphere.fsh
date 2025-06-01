#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh

#include dragon_experiments:celestial/celestial_body_uniforms

uniform float AtmosphereSize;
uniform float AtmosphereCompression = 0;

uniform float AtmosphereBrightness;
uniform vec3 AtmosphereRayleighCoeffiecents = vec3(5.5e-3, 13.0e-3, 22.4e-3);
uniform float AtmosphereMieCoeffiecent = (21e-3 * 0.5) / 1.5;
uniform float AtmosphereRayleighScaleHeight = 8;
uniform float AtmosphereMieScaleHeight = 8;


in vec2 texCoord;
uniform float defaultScaleSize = 127.;
out vec4 fragColor;
void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    fragColor = baseColor;
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    gl_FragDepth = depthSample;
    vec4 worldPos = -screenToWorldSpace(texCoord,depthSample) + vec4(ShipPos,0);


    vec3 rd = viewDirFromUv(texCoord);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro = rotateByQuaternion(VeilCamera.CameraPosition - ShipOrigin,ShipRotation) + ShipPos;

    vec2 t = rayleigh_rsi(ro - PlanetPos,rd,PlanetSize);
    float sceneDist = length(ro + worldPos.rgb / worldPos.w);

    if (AtmosphereSize == 0 || (AtmosphereRayleighScaleHeight == 0 && AtmosphereMieScaleHeight == 0)) {
        return;
    }

    float sizeMod = PlanetSize / defaultScaleSize;
    float atmoSize = PlanetSize + AtmosphereSize * sizeMod;
    vec2 atmoHit = rayleigh_rsi(ro - PlanetPos,rd,atmoSize);
    if (atmoHit.y < 0) {
        return;
    }
    if (atmoHit.x < 0 && atmoHit.y > 0) {
        atmoHit.x = 0;
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
        atmoP - PlanetPos,               // ray origin
        LightPosition - PlanetPos,                        // position of the sun
        SunBrightness,                           // intensity of the sun
        PlanetSize,                         // radius of the celestialBody in meters
        atmoSize - AtmosphereCompression,                         // radius of the atmosphere in meters
        (AtmosphereRayleighCoeffiecents * 1) / sizeMod, // Rayleigh scattering coefficient
        AtmosphereMieCoeffiecent / sizeMod,                          // Mie scattering coefficient
        AtmosphereRayleighScaleHeight * sizeMod,                            // Rayleigh scale height
        AtmosphereMieScaleHeight * sizeMod,                          // Mie scale height
        0.758                           // Mie preferred scattering direction
    ) * AtmosphereBrightness;

    color = 1.0 - exp(-1.0 * color);
    fragColor.rgb = fragColor.rgb + color;
    //fragColor.rgb = vec3(getPlanetAtmoDistance(atmoP,PlanetPos,PlanetSize,7));
}