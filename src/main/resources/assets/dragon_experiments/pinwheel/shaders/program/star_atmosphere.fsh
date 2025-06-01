#include veil:space_helper

#include dragon_experiments:space_utils
#include dragon_experiments:star_scattering

#include dragon_experiments:celestial/celestial_body_uniforms

uniform float AtmosphereBrightness = 1;
uniform vec3 AtmosphereColor;
uniform float AtmosphereSize = 1;
uniform float AtmosphereFalloff = 1;

in vec2 texCoord;

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

    vec2 planetHit = star_rsi(ro - PlanetPos,rd,PlanetSize);
    float sceneDist = length(ro + worldPos.rgb / worldPos.w);

    if (AtmosphereSize == 0) {
        return;
    }

    float sizeMod = PlanetSize / 100;
    float atmoSize = PlanetSize + AtmosphereSize * sizeMod;
    vec2 atmoHit = star_rsi(ro - PlanetPos,rd,atmoSize);
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
    if (atmoT > planetHit.x) {
        return;
    }

    //vec3 color = vec3(0.5);
    vec3 color = vec3(renderAtmosphere(atmoP - PlanetPos,rd,PlanetSize,atmoSize,sizeMod * 5) / (sizeMod * 10)) * AtmosphereColor;
    color = 1.0 - exp(-1.0 * color);
    fragColor.rgb = fragColor.rgb + color;
}