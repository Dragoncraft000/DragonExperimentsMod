#include veil:space_helper
#include dragon_experiments:space_utils
#include dragon_experiments:rayleigh
#include dragon_experiments:star_scattering

#include dragon_experiments:celestial/celestial_body_uniforms


in vec2 texCoord;

uniform float defaultScaleSize = 127.;
out vec4 fragColor;


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
            albedo = texture(PlanetTexturesSampler, vec3(planetTexCoord + vec2(GameTime * 1,0),PlanetTextures[i]));

            int t = UpperLayerTextures[i];
            if (t != 0) {
               clouds = texture(PlanetTexturesSampler, vec3(planetTexCoord + vec2(GameTime * 1,0), UpperLayerTextures[i]));
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





