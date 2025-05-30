uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D PlanetTexture;
uniform sampler2D UpperLayerTexture;
uniform int useUpperLayer;
uniform int useBaseLayer;

uniform float GameTime;

// System Specifc Uniforms
vec3 LightPosition;
uniform float SunBrightness = 22;
uniform vec3 ShipPos = vec3(0,0,0);
uniform vec4 ShipRotation = vec4(0,0,0,0);

// Planet Specific Uniforms
uniform float PlanetSize;
uniform vec3 PlanetPos;
uniform float PlanetRotationSpeed;