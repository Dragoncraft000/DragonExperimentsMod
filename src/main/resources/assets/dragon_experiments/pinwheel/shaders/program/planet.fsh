#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D PlanetTexture;
uniform sampler2D UpperLayerTexture;
uniform int useUpperLayer;
uniform int useBaseLayer;

uniform float GameTime;

// System Specifc Uniforms
vec3 LightPosition;
uniform float SunBrightness = 22;
uniform vec3 ShipPos = vec3(0,0,0);
uniform vec4 ShipRotation = vec4(0,0,0,0);

// Planet Specific Uniforms
uniform float PlanetSize;
uniform float AtmosphereSize;
uniform float AtmosphereCompression = 0;
uniform vec3 PlanetPos;
uniform float PlanetRotationSpeed;

uniform float AtmosphereBrightness = 0.9;
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
    vec4 worldPos = -screenToWorldSpace(texCoord,depthSample) + vec4(ShipPos,0);
    gl_FragDepth = depthSample;
    vec2 uv = texCoord - vec2(0.5,0.5);
    vec3 rd = viewDirFromUv(texCoord);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro = rotateByQuaternion(VeilCamera.CameraPosition,ShipRotation) + ShipPos;

    vec2 planetHit = rsi(ro - PlanetPos,rd,PlanetSize);
    bool rayhitPlanet = true;

    float t = raymarchPlanet(ro,rd,PlanetPos,PlanetSize);
    fragColor = baseColor;

    float sceneDist = length(ro + worldPos.rgb / worldPos.w);
    if (planetHit.y > sceneDist && depthSample < 1) {
            rayhitPlanet = false;
    }
    if (hitPlanet(t,depthSample,ro,worldPos) && rayhitPlanet) {
        vec3 p = ro + rd * t;
        gl_FragDepth = worldToScreenSpace(vec4(worldPos - vec4(ShipPos,0))).z;
        vec3 normal = genNormal(p,PlanetPos,PlanetSize);
        float diffLight = genLight(p,normal,LightPosition);
        if (length(PlanetPos - LightPosition) < 1000) {
            diffLight = 1;
        }
        vec3 texDir = normalize(p - PlanetPos);
        vec2 planetTexCoord = normalToSpherical(texDir);
        vec4 albedo = texture(PlanetTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed,0)) * useBaseLayer;
        vec4 clouds = texture(UpperLayerTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed * 0.6,0)) * useUpperLayer;
        fragColor = vec4(((albedo.rgb * diffLight) + (clouds.rgb * 0.3 * diffLight)),1);
    }
    if (AtmosphereSize == 0 || (AtmosphereRayleighScaleHeight == 0 && AtmosphereMieScaleHeight == 0)) {
        return;
    }


float standardSize = 127.;

    float sizeMod = PlanetSize / defaultScaleSize;
    float atmoSize = PlanetSize + AtmosphereSize * sizeMod;
    vec2 atmoHit = rsi(ro - PlanetPos,rd,atmoSize);
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
    if (atmoT > t) {
        return;
    }
    vec3 color = atmosphere(
        rd,           // normalized ray direction
        atmoP - PlanetPos,               // ray origin
        LightPosition - PlanetPos,                        // position of the sun
        SunBrightness,                           // intensity of the sun
        PlanetSize,                         // radius of the celestialBody in meters
        atmoSize - AtmosphereCompression,                         // radius of the atmosphere in meters
        (AtmosphereRayleighCoeffiecents * AtmosphereBrightness) / sizeMod, // Rayleigh scattering coefficient
        AtmosphereMieCoeffiecent / sizeMod,                          // Mie scattering coefficient
        AtmosphereRayleighScaleHeight * sizeMod,                            // Rayleigh scale height
        AtmosphereMieScaleHeight * sizeMod,                          // Mie scale height
        0.758                           // Mie preferred scattering direction
    );

    color = 1.0 - exp(-1.0 * color);
    fragColor.rgb = fragColor.rgb + color;
    //fragColor.rgb = vec3(getPlanetAtmoDistance(atmoP,PlanetPos,PlanetSize,7));

}








