#include veil:fog
#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

const float FogStart = 1;
const float FogEnd = 100;
uniform vec4 FogColor;
uniform int FogShape;
uniform float GameTime;
uniform float skyLight;

in vec2 texCoord;

out vec4 fragColor;

float random(vec3 seed) {
    return fract(sin(dot(seed, vec3(12.9898,78.233,85.1472))) * 43758.5453);
}

float random(vec2 seed) {
    return fract(sin(dot(seed, vec2(12.9898,78.233))) * 43758.5453);
}

float random(float seed) {
    return fract(sin(seed) * 43758.5453);
}

float noise(float n) {
    float i = floor(n);
    float f = fract(n);
    return mix(random(i), random(i + 1.0), smoothstep(0.0, 1.0, f));
}

float noise(vec2 p){
	vec2 ip = floor(p);
	vec2 u = fract(p);
	u = u*u*(3.0-2.0*u);

	float res = mix(
		mix(random(ip),random(ip+vec2(1.0,0.0)),u.x),
		mix(random(ip+vec2(0.0,1.0)),random(ip+vec2(1.0,1.0)),u.x),u.y);
	return res*res;
}

float noise(vec3 p){
    vec3 ip = floor(p);
	vec3 u = fract(p);
	u = u*u*(3.0-2.0*u);

	float res = mix(mix(
		mix(random(ip+vec3(0.0,0.0,0.0)),random(ip+vec3(1.0,0.0,0.0)),u.x),
		mix(random(ip+vec3(0.0,1.0,0.0)),random(ip+vec3(1.0,1.0,0.0)),u.x),u.y),
        mix(
		mix(random(ip+vec3(0.0,0.0,1.0)),random(ip+vec3(1.0,0.0,1.0)),u.x),
		mix(random(ip+vec3(0.0,1.0,1.0)),random(ip+vec3(1.0,1.0,1.0)),u.x),u.y),u.z);
	return res*res;
}

vec3 viewPosFromDepthSample(float depth, vec2 uv) {
    vec4 positionCS = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    vec4 positionVS = VeilCamera.IProjMat * positionCS;
    positionVS /= positionVS.w;

    return positionVS.xyz;
}

float rayPlane(vec3 rayOrigin, vec3 rayDir, vec3 pointOnPlane, vec3 planeNormal, float epsilon) {
    float denom = dot(planeNormal, rayDir);
    if (abs(denom) > epsilon) {
        return dot(pointOnPlane - rayOrigin, planeNormal) / denom;
    }

    return -1.0;
}

float rayPlane(vec3 rayOrigin, vec3 rayDir, vec3 pointOnPlane, vec3 planeNormal) {
    return rayPlane(rayOrigin, rayDir, pointOnPlane, planeNormal, 1e-6);
}

const int steps = 25;
float density = 0.25;

const float maxHeight = 300;

void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    vec3 pos = viewPosFromDepthSample(depthSample, texCoord);
    vec3 rd = viewDirFromUv(texCoord);

    float vertexDistance = length(pos);
    vec3 ro = VeilCamera.CameraPosition;
    float pd = max(0,rayPlane(ro,rd,vec3(0,maxHeight,0),vec3(0,1,0)));
    if (rd.y > 0) {
        if (pd < 0) {
            fragColor = baseColor;
            return;
        }
        pd = 0;
    }
    if (pd > vertexDistance) {
        fragColor = baseColor;
        return;
        pd = 0;
    }
    vec3 fogRo = ro + rd * pd;
    vertexDistance -= pd;
    vertexDistance = min(vertexDistance,500);
    float stepDist = vertexDistance / steps;
    float accumulatedDensity = 0;
    float intensity = 0;
    for (int i = 0; i < steps;i++) {
        vec3 p = fogRo + rd * i * stepDist;
        float densitySample = noise(p * 0.1);

        float heightMod = clamp(1.5 - abs(p.y + 60) * 0.01,0,1);
        float distanceMod = min(1,1.5 - length(ro - p) * 0.003);
        accumulatedDensity += densitySample * stepDist * heightMod * distanceMod;
    }
    density *= 0.25 + skyLight * 0.75;
    intensity = accumulatedDensity * density;
    intensity = 1.0 - exp(-1.0 * intensity);
    fragColor = mix(baseColor,vec4(vec3(max(0.01,.9)),1),clamp(intensity,0,1));


}
