uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

uniform float GameTime;

// System Specifc Uniforms
vec3 LightPosition;
uniform float SunBrightness = 22;
uniform vec3 ShipPos = vec3(0,0,0);
uniform vec3 ShipOrigin = vec3(0,0,0);
uniform vec4 ShipRotation = vec4(0,0,0,0);
uniform int PlanetCount;

// Planet Specific Uniforms
uniform float[50] PlanetSizes;
uniform vec3[50] PlanetPositions;
uniform float[50] PlanetRotationSpeed;
uniform int[50] PlanetTextures;
uniform int[50] UpperLayerTextures;

uniform int[50] AtmosphereTypes;
uniform float[50] AtmosphereSizes;
uniform float AtmosphereCompression = 0;

uniform float[50] AtmosphereBrightnesses;
uniform vec3[50] AtmosphereRayleighCoefficients;
uniform float[50] AtmosphereMieCoefficients;
uniform float[50] AtmosphereRayleighScaleHeights;
uniform float[50] AtmosphereMieScaleHeights;
uniform sampler2DArray PlanetTexturesSampler;