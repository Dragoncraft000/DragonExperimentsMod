#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D SolidDepthSampler;
uniform sampler2D EarthTexture;
uniform sampler2D CloudsTexture;

uniform float GameTime;


// System Specifc Uniforms
vec3 LightPosition = vec3(100000,400, 0);
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
    vec4 pos = screenToLocalSpace(texCoord, depthSample) + vec4(ShipPos,0);
    vec4 worldPos = screenToWorldSpace(texCoord,depthSample) + vec4(ShipPos,0);


    vec2 uv = texCoord - vec2(0.5,0.5);
    vec3 rd = viewDirFromUv(texCoord);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro =  VeilCamera.CameraPosition + ShipPos;

    float t = raymarchPlanet(ro,rd,PlanetPos,PlanetSize);
    fragColor = baseColor;
    if (hitPlanet(t,depthSample,ro,worldPos)) {
        vec3 p = ro + rd * t;
        vec3 normal = genNormal(p,PlanetPos,PlanetSize);
        float diffLight = genLight(p,normal,LightPosition);
        vec3 texDir = normalize(p - PlanetPos);
        vec2 planetTexCoord = normalToSpherical(texDir);
        vec4 albedo = texture(EarthTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed,0));
        vec4 clouds = texture(CloudsTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed * 0.6,0));
        fragColor = vec4(((albedo.rgb * diffLight) + (clouds.rgb * 0.3 * diffLight)),1);
    }
float standardSize = 127.;
    float sizeMod = PlanetSize / defaultScaleSize;
    float atmoSize = PlanetSize + AtmosphereSize * sizeMod;
    float atmoT = raymarchAtmosphere(ro,rd,PlanetPos,atmoSize);
    vec3 atmoP = ro + rd * max(atmoT,1);
    float sceneDist = length(ro - worldPos.rgb);
    vec2 atmoHit = rsi(ro - PlanetPos,rd,atmoSize);
    if (atmoHit.y > sceneDist && depthSample < 1) {
        return;
    }
    if (atmoHit.x > atmoHit.y) {
        return;
    }
    if (atmoT < t) {
        vec3 normal = genNormal(atmoP,PlanetPos,PlanetSize);
        float diffLight = genLight(atmoP,normal,LightPosition);
        fragColor.a = 1;
        if (atmoHit.y < sceneDist) {

        }
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
       // color *= 1.5;
        color = 1.0 - exp(-1.0 * color);
        fragColor.rgb = fragColor.rgb + color ;
        //fragColor.rgb = vec3(getPlanetAtmoDistance(atmoP,PlanetPos,PlanetSize,7));
    }
}








