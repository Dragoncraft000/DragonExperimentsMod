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
    protected boolean rendered;

    protected boolean staticPosition = false;

    protected int orbitDistance = 10000;
    protected double orbitTime = 10;

    protected int radius;
    protected double rotationTime;

    protected final List<CelestialBody> orbitingCelestialBodies = new ArrayList<>();

    protected double currentOrbitAngle = 0;

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
        lastPosition = new Vec3d(currentPosition.x,currentPosition.y,currentPosition.z);
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
        ShaderProgram body = VeilRenderSystem.renderer().getShaderManager().getShader(Identifier.of(DragonExperiments.MOD_ID, "body_textures"));
        if (body == null) {
            return false;
        }
        body.setSampler("PlanetTexture",textureId == -1 ? 0 : textureId);
        body.setSampler("UpperLayerTexture",upperLayerTexture == -1 ? 1 : upperLayerTexture);
        pipeline.setInt("useUpperLayer", upperLayerTexture == -1 ? 0 : 1);
        pipeline.setInt("useBaseLayer", textureId == -1 ? 0 : 1);
        pipeline.setVector("PlanetPos", InterpolationUtils.interpolateLinear(lastPosition.toVector3f(),currentPosition.toVector3f(),subtick));
        pipeline.setFloat("PlanetSize", (float) radius / PLANET_SCALE_DIVIDER);

        return true;
    }




    public void addBodiesRecursive(List<CelestialBody> bodies) {
        bodies.add(this);
        if (orbitingCelestialBodies.isEmpty()) {
            return;
        }
        orbitingCelestialBodies.forEach((b) -> b.addBodiesRecursive(bodies));
    }

    public Identifier getBodyPipeline() {
        return Identifier.of(DragonExperiments.MOD_ID, "celestial_body");
    }


}
