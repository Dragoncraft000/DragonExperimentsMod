#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh
#include dragon_experiments:star_scattering

#include dragon_experiments:celestial/celestial_body_uniforms

uniform sampler2D SunTexture;
uniform sampler2D EarthTexture;
uniform sampler2D EarthClouds;
uniform sampler2D MoonTexture;
uniform sampler2D MercuryTexture;
uniform sampler2D VenusTexture;
uniform sampler2D VenusClouds;
uniform sampler2D MarsTexture;
uniform sampler2D JupiterTexture;

in vec2 texCoord;

uniform float defaultScaleSize = 127.;
out vec4 fragColor;

vec4 getSamplerById(int texturedId,vec2 texCoord) {
    switch (texturedId) {
        case 1:
            return texture(SunTexture,texCoord);
        case 2:
            return texture(EarthTexture,texCoord);
        case 3:
            return texture(EarthClouds,texCoord);
        case 4:
            return texture(MoonTexture,texCoord);
        case 5:
            return texture(MercuryTexture,texCoord);
        case 6:
            return texture(VenusTexture,texCoord);
        case 7:
            return texture(VenusClouds,texCoord);
        case 8:
            return texture(MarsTexture,texCoord);
        case 9:
            return texture(JupiterTexture,texCoord);
        default:
            return vec4(1);
    }
}


void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    fragColor = baseColor;
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    gl_FragDepth = depthSample;

    vec4 worldPos = -screenToWorldSpace(texCoord,depthSample) + vec4(ShipPos,0);
    vec4 realWorldPos = -screenToWorldSpace(texCoord,depthSample);
    vec3 rd = viewDirFromUv(texCoord);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro = rotateByQuaternion(VeilCamera.CameraPosition - ShipOrigin,ShipRotation) + ShipPos;

    float closestDepth = 4200000000;
    float maxDepthPass = 4200000000;
    float sceneDist = length((VeilCamera.CameraPosition) - realWorldPos.rgb / realWorldPos.w);
    for (int i = 0; i < PlanetCount;i++) {
        vec2 planetHit = rsi(ro - PlanetPositions[i],rd,PlanetSizes[i]);
        bool rayhitPlanet = true;

        if (planetHit.x > sceneDist && depthSample < 1) {
               rayhitPlanet = false;
        }
        if (planetHit.x > closestDepth && planetHit.x < maxDepthPass) {
            rayhitPlanet = false;
        }

        if (hitPlanet(planetHit.x,depthSample,ro,worldPos) && rayhitPlanet) {
            fragColor = vec4(1);
            closestDepth = planetHit.x;
            vec3 p = ro + rd * planetHit.x;
            vec3 normal = genNormal(p,PlanetPositions[i],PlanetSizes[i]);
            float diffLight = genLight(p,normal,LightPosition);
            if (length(PlanetPositions[i] - LightPosition) < 1000) {
                diffLight = 1;
            }
            vec3 texDir = normalize(p - PlanetPositions[i]);
            vec2 planetTexCoord = normalToSpherical(texDir);
            vec4 clouds = vec4(0);
            vec4 albedo = vec4(0);
            albedo = getSamplerById(PlanetTextures[i], planetTexCoord + vec2(GameTime * 1,0));

            int t = UpperLayerTextures[i];
            if (t != 0) {
                clouds = getSamplerById(UpperLayerTextures[i], planetTexCoord + vec2(GameTime * 1,0));
            }
            fragColor = vec4(mix((albedo.rgb * diffLight),(clouds.rgb * diffLight),clouds.r * 0.9),1);
            //fragColor = vec4(((albedo.rgb * diffLight) + (clouds.rgb * 0.3 * diffLight)),1);
        }

        if (AtmosphereTypes[i] == 1 || AtmosphereTypes[i] == 2) {
            float sizeMod = PlanetSizes[i] / defaultScaleSize;
            float atmoSize = PlanetSizes[i] + AtmosphereSizes[i] * sizeMod;
            vec2 atmoHit = rsi(ro - PlanetPositions[i],rd,atmoSize);

            if (atmoHit.y < 0) {
                continue;
            }
            if (atmoHit.x < 0 && atmoHit.y > 0) {
                atmoHit.x = 0;
            }

            if ((atmoHit.y > sceneDist || atmoHit.y > closestDepth) && depthSample < 1) {
                continue;
            }
            if (atmoHit.x > atmoHit.y) {
                continue;
            }
            float atmoT = (atmoHit.x);
            vec3 atmoP = ro + rd * atmoT;

            if (AtmosphereTypes[i] == 1) {
                vec3 color = atmosphere(
                    rd,           // normalized ray direction
                    atmoP - PlanetPositions[i],               // ray origin
                    LightPosition - PlanetPositions[i],                        // position of the sun
                    SunBrightness,                           // intensity of the sun
                    PlanetSizes[i],                         // radius of the celestialBody in meters
                    atmoSize,                         // radius of the atmosphere in meters
                    AtmosphereRayleighCoefficients[i] / sizeMod, // Rayleigh scattering coefficient
                    AtmosphereMieCoefficients[i] / sizeMod,                          // Mie scattering coefficient
                    AtmosphereRayleighScaleHeights[i] * sizeMod,                            // Rayleigh scale height
                    AtmosphereMieScaleHeights[i] * sizeMod,                          // Mie scale height
                    0.758                           // Mie preferred scattering direction
                ) * AtmosphereBrightnesses[i];
                color = max(color,vec3(0));
                color = 1.0 - exp(-1.0 * color);
                fragColor.rgb = fragColor.rgb + color;
            }
            if (AtmosphereTypes[i] == 2) {
                vec3 color = vec3(renderAtmosphere(atmoP - PlanetPositions[i],rd,PlanetSizes[i],atmoSize,sizeMod * 5) / (sizeMod * 10)) * AtmosphereRayleighCoefficients[i] * AtmosphereBrightnesses[i];
                color = 1.0 - exp(-1.0 * color);
                fragColor.rgb = fragColor.rgb + color;
            }


        }


    }
}





