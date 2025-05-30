
vec2 star_rsi(vec3 r0, vec3 rd, float sr) {
    // ray-sphere intersection that assumes
    // the sphere is centered at the origin.
    // No intersection when result.x > result.y
    float a = dot(rd, rd);
    float b = 2.0 * dot(rd, r0);
    float c = dot(r0, r0) - (sr * sr);
    float d = (b*b) - 4.0*a*c;
    if (d < 0.0) return vec2(1e12,-1e12);
    return vec2(
    (-b - sqrt(d))/(2.0*a),
    (-b + sqrt(d))/(2.0*a)
    );
}


float renderAtmosphere(vec3 r0,vec3 rd,float planetSize,float atmoSize,float scaleHeight) {
    int steps = 4;
    vec2 p = star_rsi(r0, rd, atmoSize);
    if (p.y < 0.0) return 0;

    vec2 planetP = star_rsi(r0, rd, planetSize);
    p.y = min(p.y, planetP.x);
    float dist = (p.y - p.x);
    float stepSize = dist / float(steps);

    float density = 0;
    for (int i = 0; i < steps; i++) {
        vec3 pos = r0 + rd * (i * stepSize);
        float height = length(pos) - planetSize;
        float odStep = exp(-height / scaleHeight) * stepSize;
        density += odStep;
    }
    return density;
}



