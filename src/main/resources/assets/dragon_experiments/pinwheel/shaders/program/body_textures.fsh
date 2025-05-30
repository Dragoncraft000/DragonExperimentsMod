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

    vec3 rd = viewDirFromUv(texCoord);
    rd = rotateByQuaternion(rd,ShipRotation);
    vec3 ro = rotateByQuaternion(VeilCamera.CameraPosition,ShipRotation) + ShipPos;


    vec2 planetHit = rsi(ro - PlanetPos,rd,PlanetSize);
    bool rayhitPlanet = true;

    float t = raymarchPlanet(ro,rd,PlanetPos,PlanetSize);
    float sceneDist = length(ro + worldPos.rgb / worldPos.w);
    if (planetHit.y > sceneDist && depthSample < 1) {
            rayhitPlanet = false;
    }
    if (hitPlanet(t,depthSample,ro,worldPos) && rayhitPlanet) {
        vec3 p = ro + rd * t;
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

}





