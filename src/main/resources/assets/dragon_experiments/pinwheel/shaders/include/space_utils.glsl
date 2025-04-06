#include veil:space_helper
#include dragon_experiments:raymarching

const int MAX_STEPS = 200;
const float MAX_DISTANCE = 10000000;
const float HIT_DISTANCE = 0.01;

mat2 rot2D(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat2(c,-s,s,c);
}

vec3 rotateByQuaternion(vec3 v, vec4 q) {
    vec3 t = 2.0 * cross(q.xyz, v);
    return v + q.w * t + cross(q.xyz, t);
}

bool raySphere(vec3 rayOrigin, vec3 rayDir, vec3 sphereCenter, float sphereRadius, out vec2 result) {
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

vec2 normalToSpherical(vec3 normal) {
    float planetXCoord = atan(normal.x, normal.z);
    float planetYCoord = asin(normal.y);

    vec2 base = vec2(planetXCoord,planetYCoord) / 3.14159265;

    base = base * vec2(0.5,-1) + vec2(0,0.5);
    return base;
}

float getPlanetDistance(vec3 p,vec3 PlanetPos,float PlanetSize) {
    return sdSphere(p - PlanetPos,PlanetSize);
}

float raymarchPlanet(vec3 ro,vec3 rd,vec3 PlanetPos, float PlanetSize) {
    float t = 0.;
    vec3 p = ro;
    int steps = 0;
    for (int i = 0; i < MAX_STEPS; i++) {
        p = ro + rd * t;
        float d = getPlanetDistance(p,PlanetPos,PlanetSize);
        t += d;
        if (d < HIT_DISTANCE && d > 0) {
            break;
        }
        if (t > MAX_DISTANCE) break;
        steps = i;

    }
    return t;
}

bool hitPlanet(float t,float depth,vec3 ro,vec4 worldPos) {
    if (t > MAX_DISTANCE || depth > t) {
        return false;
    }
    float sceneDist = length(ro - worldPos.rgb);
    if (t > sceneDist && depth < 1) {
        return false;
    }
    return true;
}


float raymarchPlanetSteps(vec3 ro,vec3 rd,vec3 PlanetPos, float PlanetSize) {
    float t = 0.;
    vec3 p = ro;
    int steps = 0;
    for (int i = 0; i < MAX_STEPS; i++) {
        p = ro + rd * t;
        float d = getPlanetDistance(p,PlanetPos,PlanetSize);
        t += d;
        if (d < HIT_DISTANCE && d > 0) {
            break;
        }
        if (t > MAX_DISTANCE) break;
        steps = i;
    }
    return steps;
}

vec3 genNormal(vec3 p,vec3 PlanetPos,float PlanetSize)
    {
    float d = getPlanetDistance(p,PlanetPos,PlanetSize); //very close to 0

    vec2 e = vec2(.01, 0.0);

    vec3 n = vec3
    (
       d - getPlanetDistance(p - e.xyy,PlanetPos,PlanetSize),
       d - getPlanetDistance(p - e.yxy,PlanetPos,PlanetSize),
       d - getPlanetDistance(p - e.yyx,PlanetPos,PlanetSize)
    );

    return normalize(n);
}

float genLight(vec3 p,vec3 normal,vec3 lightPos) {
    vec3 l = normalize(lightPos-p);
    //return max(dot(normal,l),0);
    return (dot(normal,l) +1) / 2.;
}

float raymarchAtmosphere(vec3 ro,vec3 rd,vec3 PlanetPos, float Size) {
    float t = 0.;
    vec3 p = ro;
    int steps = 0;
    for (int i = 0; i < MAX_STEPS; i++) {
        p = ro + rd * t;
        float d = getPlanetDistance(p,PlanetPos,Size);
        t += d;
        if (d < HIT_DISTANCE) {
            break;
        }
        if (t > MAX_DISTANCE) break;
        steps = i;

    }
    return t;
}
