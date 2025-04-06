#include veil:space_helper
#include dragon_experiments:space_utils

const int PRIMARY_STEPS = 30;
const int LIGHT_STEPS = 5;

#define RAY_BETA vec3(5.5e-3, 13.0e-3, 22.4e-3)
#define MIE_BETA vec3(21e-6)
#define ABSORPTION_BETA vec3(2.04e-5, 4.97e-5, 1.95e-6)
#define AMBIENT_BETA vec3(0.0)
#define G 0.001 /* mie scattering direction, or how big the blob around the sun is */

bool raySphereIntersect(vec3 rayOrigin, vec3 rayDir, vec3 sphereCenter, float sphereRadius, out vec2 result) {
    float sphereRadius2 = sphereRadius * sphereRadius;
    vec3 lvec = sphereCenter - rayOrigin;
    float tca = dot(lvec, rayDir);
    float d2 = dot(lvec, lvec) - tca * tca;
    if(d2 > sphereRadius2)   return false;
    float thc = sqrt(sphereRadius2 - d2);
    float t0 = tca - thc;
    float t1 = tca + thc;
    result = vec2(t0, t1);
    return (t0 < t1 && t1 >= 0);
}


float getPlanetAtmoDistance(vec3 p,vec3 PlanetPos,float PlanetSize,float ph) {
    return exp( -max( length(p - PlanetPos) -PlanetSize, 0.0 ) / ph );
}

vec3 getOpticDensity(vec3 p, vec3 light, vec3 planetPos,float planetSize,float atmoSize,float ph) {
    return vec3(0);
    vec3 lightDir = -normalize(planetPos - light);
    vec2 rayIntersection = vec2(0.);
    vec2 rayIntersection2 = vec2(0.);


    raySphereIntersect(p,lightDir,planetPos,atmoSize,rayIntersection);
    raySphereIntersect(p,lightDir,planetPos,planetSize,rayIntersection2);
    float dist = min(rayIntersection2.r,rayIntersection.r);
    vec3 accumulatedDensity = vec3(.0);
    float stepSize = dist / LIGHT_STEPS;
    for (int i = 0; i < LIGHT_STEPS;i++) {
        vec3 samplePoint = p + light * i * stepSize;
        float height = getPlanetAtmoDistance(samplePoint,planetPos,planetSize,ph);
        accumulatedDensity += height / LIGHT_STEPS;
    }

    return vec3(accumulatedDensity);
}

vec3 renderAtmosphere(vec3 p,vec3 rd,vec3 lightDirection, vec3 planetPos,float PlanetSize,float AtmoSize,float dist,float ph) {
    vec3 accumulatedDensity = vec3(.0);
    vec3 total_ray = vec3(0.0); // for rayleigh
    vec3 total_mie = vec3(0.0); // for mie
    vec3 lightDir = normalize(p - lightDirection);

    float mu = dot(rd, lightDir);
    float mumu = mu * mu;
    float gg = G * G;
    float phase_ray = 3.0 / (50.2654824574 /* (16 * pi) */) * (1.0 + mumu);
    bool allow_mie = false;
    float phase_mie = allow_mie ? 3.0 / (25.1327412287 /* (8 * pi) */) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * G, 1.5) * (2.0 + gg)) : 0.0;

    vec3 accumulatedColors = vec3(.0);
    float stepSize = dist / PRIMARY_STEPS;
    for (int i = 0; i < PRIMARY_STEPS;i++) {
        vec3 samplePoint = p + rd * i * stepSize;
        float height = getPlanetAtmoDistance(samplePoint,planetPos,PlanetSize,ph);
        //accumulatedDensity += height / PRIMARY_STEPS;

        vec3 rayDensity = getOpticDensity(samplePoint,lightDirection,planetPos,PlanetSize,AtmoSize,ph) / PRIMARY_STEPS;
        //accumulatedDensity += rayDensity;
        vec3 attn = exp(-RAY_BETA * 100 * (accumulatedDensity.x + rayDensity.x) - MIE_BETA * (accumulatedDensity.y + rayDensity.y) - ABSORPTION_BETA * (accumulatedDensity.z + rayDensity.z));

        total_ray += attn;
        total_mie += height * attn;
    }

    // calculate how much light can pass through the atmosphere
    vec3 opacity = exp(-(MIE_BETA * accumulatedDensity.y + RAY_BETA * accumulatedDensity.x + ABSORPTION_BETA * accumulatedDensity.z));

    return (
        phase_ray * RAY_BETA * total_ray // rayleigh color
        + phase_mie * MIE_BETA * total_mie // mie
        + accumulatedDensity.x * AMBIENT_BETA // and ambient
    ) * opacity; // now make sure the background is rendered correctly
    //    return vec3(getOpticDensity(p,lightDirection,planetPos,PlanetSize,AtmoSize,ph));
}



