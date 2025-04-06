#version 150

float cubic_in_out(float t) {
    t = 1.0 - t;
    t = 1.0 - t*t;
    return t*t;
}
vec2 cubic_in_out(vec2 v) {
    return vec2(
        cubic_in_out(v.x),
        cubic_in_out(v.y)
    );
}
vec3 cubic_in_out(vec3 v) {
    return vec3(
        cubic_in_out(v.x),
        cubic_in_out(v.y),
        cubic_in_out(v.z)
    );
}