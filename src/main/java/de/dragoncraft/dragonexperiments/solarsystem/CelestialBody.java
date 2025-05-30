package de.dragoncraft.dragonexperiments.solarsystem;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.utils.InterpolationUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class CelestialBody {

    public static int PLANET_DISTANCE_DIVIDER = 1;
    public static int PLANET_SCALE_DIVIDER = 1;



    public static int MAX_RECURSION_LEVELS = 10;

    private final String bodyName;
    @Getter
    private Identifier textureName;
    @Getter
    private Identifier upperLayerTextureName;

    @Setter
    private boolean rendered;

    private boolean staticPosition = false;

    private int orbitDistance = 10000;
    private double orbitTime = 10;

    private int radius;
    private double rotationTime;

    private float atmosphereSize = 50;
    private Vec3d atmosphereRayleighCoeffiecents = new Vec3d(55,130,224);
    private float atmosphereMieCoeffiecent = 105f;

    private float atmosphereRayleighScaleHeight = 5;
    private float atmosphereMieScaleHeight = 1.2f;
    private final List<CelestialBody> orbitingCelestialBodies = new ArrayList<>();

    private double currentOrbitAngle = 0;

    @Getter
    protected Vec3d currentPosition = new Vec3d(0,0,0);
    @Getter
    protected Vec3d lastPosition = new Vec3d(0,0,0);

    @Getter
    protected float currentRotation = 0;


    public CelestialBody(String bodyName) {
        this.bodyName = bodyName;
        this.rendered = false;
    }



    public CelestialBody addOribitingPlanet(CelestialBody celestialBody) {
        if (orbitingCelestialBodies.contains(celestialBody)) {
            return this;
        }
        orbitingCelestialBodies.add(celestialBody);
        return this;
    }
    public CelestialBody disableOrbit() {
        staticPosition = true;
        orbitDistance = 0;
        orbitTime = Double.MAX_VALUE;
        return this;
    }

    public CelestialBody setRenderDetails(int radius,int rotationTime,Identifier texture) {
        this.rendered = true;
        this.radius = radius;
        this.rotationTime = rotationTime;
        this.textureName = texture;
        return this;
    }
    public CelestialBody setRenderDetails(int radius,int rotationTime,Identifier texture,Identifier upperLayerTextureName) {
        this.rendered = true;
        this.radius = radius;
        this.rotationTime = rotationTime;
        this.textureName = texture;
        this.upperLayerTextureName = upperLayerTextureName;
        return this;
    }

    public CelestialBody setOrbit(int orbitDistance, double orbitTime) {
        this.orbitDistance = orbitDistance;
        this.orbitTime = orbitTime;
        return this;
    }
    public CelestialBody setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight, float mieCoeffiecent, float mieScaleHeight, float atmosphereSize) {
        this.atmosphereRayleighCoeffiecents = rayleighCoeffiecents;
        this.atmosphereRayleighScaleHeight = rayleighScaleHeight;
        this.atmosphereMieCoeffiecent = mieCoeffiecent;
        this.atmosphereMieScaleHeight = mieScaleHeight;
        this.atmosphereSize = atmosphereSize;
        return this;
    }
    public CelestialBody setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight, float mieCoeffiecent, float mieScaleHeight) {
        return setAtmosphere(rayleighCoeffiecents,rayleighScaleHeight,mieCoeffiecent,mieScaleHeight,atmosphereSize);
    }

    public CelestialBody disableAtmosphere() {
        return setAtmosphere(new Vec3d(0,0,0),0,0,0,0);
    }


    private void addReferenceFrame(CelestialBody parent) {
        currentPosition = currentPosition.add(parent.currentPosition);
    }

    public void tickPosition() {
        tickPosition(1);
    }

    public void tickPosition(int recursions) {
        if (recursions > MAX_RECURSION_LEVELS) {
            return;
        }
        lastPosition = currentPosition;
        if (!staticPosition) {
            currentOrbitAngle += (1 / orbitTime) / 24000;
            if (currentOrbitAngle > 360) {
                currentOrbitAngle -= 360;
            }
            double radians = Math.toRadians(currentOrbitAngle);
            currentPosition = new Vec3d(Math.sin(radians) * orbitDistance  / PLANET_DISTANCE_DIVIDER,0,Math.cos(radians) * orbitDistance / PLANET_DISTANCE_DIVIDER);
        }
        orbitingCelestialBodies.forEach((celestialBody -> {
            celestialBody.tickPosition(recursions +1);
            celestialBody.addReferenceFrame(this);
        }));
    }

    public boolean renderBody(PostPipeline pipeline,int textureId,int upperLayerTexture,float subtick) {
        if (!rendered) {
            return false;
        }

        ShaderProgram body = VeilRenderSystem.renderer().getShaderManager().getShader(getBodyShader());
        if (body == null) {
            return false;
        }
        body.setSampler("PlanetTexture",textureId == -1 ? 0 : textureId);
        body.setSampler("UpperLayerTexture",upperLayerTexture == -1 ? 1 : upperLayerTexture);
        body.setInt("useUpperLayer", upperLayerTexture == -1 ? 0 : 1);
        body.setInt("useBaseLayer", textureId == -1 ? 0 : 1);
        body.setVector("PlanetPos", InterpolationUtils.interpolateLinear(lastPosition.toVector3f(),currentPosition.toVector3f(),subtick));
        body.setFloat("PlanetSize", (float) radius / PLANET_SCALE_DIVIDER);
        body.setFloat("AtmosphereSize", atmosphereSize);
        body.setFloat("PlanetRotationSpeed", (float) rotationTime);
        body.setVector("AtmosphereRayleighCoeffiecents",atmosphereRayleighCoeffiecents.toVector3f().mul(1e-4f));
        body.setFloat("AtmosphereMieCoeffiecent", atmosphereMieCoeffiecent * 1e-4f);
        body.setFloat("AtmosphereBrightness", 1f);
        body.setFloat("AtmosphereRayleighScaleHeight",atmosphereRayleighScaleHeight);
        body.setFloat("AtmosphereMieScaleHeight",atmosphereMieScaleHeight);
        return true;
    }




    public void addBodiesRecursive(List<CelestialBody> bodies) {
        bodies.add(this);
        if (orbitingCelestialBodies.isEmpty()) {
            return;
        }
        orbitingCelestialBodies.forEach((b) -> b.addBodiesRecursive(bodies));
    }

    public Identifier getBodyShader() {
        return Identifier.of(DragonExperiments.MOD_ID, "planet");
    }


}
