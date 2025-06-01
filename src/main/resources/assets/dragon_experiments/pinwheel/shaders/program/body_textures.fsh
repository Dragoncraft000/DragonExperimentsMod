#include veil:space_helper
#include dragon_experiments:space_utils

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


    vec2 planetHit = rsi(ro - PlanetPos,rd,PlanetSize);
    bool rayhitPlanet = true;

    float sceneDist = length((VeilCamera.CameraPosition) - realWorldPos.rgb / realWorldPos.w);
    if (planetHit.x > sceneDist && depthSample < 1) {
            rayhitPlanet = false;
    }
    if (hitPlanet(planetHit.x,depthSample,ro,worldPos) && rayhitPlanet) {
        vec3 p = ro + rd * planetHit.x;
        vec3 normal = genNormal(p,PlanetPos,PlanetSize);
        float diffLight = genLight(p,normal,LightPosition);
        if (length(PlanetPos - LightPosition) < 1000) {
            diffLight = 1;
        }
        vec3 texDir = normalize(p - PlanetPos);
        vec2 planetTexCoord = normalToSpherical(texDir);
        vec4 albedo = texture(PlanetTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed,0)) * useBaseLayer;
        vec4 clouds = texture(UpperLayerTexture, planetTexCoord + vec2(GameTime * PlanetRotationSpeed * 0.6,0)) * useUpperLayer;
        fragColor = vec4(mix((albedo.rgb * diffLight),(clouds.rgb * diffLight),clouds.r * 0.9),1);
        //fragColor = vec4(((albedo.rgb * diffLight) + (clouds.rgb * 0.3 * diffLight)),1);
    }

}





