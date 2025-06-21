package de.dragoncraft.dragonexperiments.solarsystem;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class CelestialBody {

    public static int MAX_RECURSION_LEVELS = 10;

    @Getter
    protected final String bodyName;
    @Getter
    protected Identifier textureName;
    @Getter
    protected Identifier upperLayerTextureName;

    @Setter
    protected boolean rendered;
    @Getter
    protected boolean hasCollision = true;

    protected boolean staticPosition = false;

    protected int orbitDistance = 10000;
    protected double orbitTime = 10;
    protected double orbitOffset = 0;
    @Getter
    protected int radius;
    @Getter
    protected double rotationTime;

    protected final List<CelestialBody> orbitingCelestialBodies = new ArrayList<>();

    protected double currentOrbitAngle = 0;

    @Getter
    protected Vec3d currentPosition = new Vec3d(0,0,0);
    @Getter
    protected Vec3d lastPosition = new Vec3d(0,0,0);

    @Getter
    protected float currentRotation = 0;
    @Getter @Setter
    protected Vec3d lastRenderedPosition = new Vec3d(0,0,0);

    protected double planetMass;



    public CelestialBody(String bodyName,boolean hasCollision) {
        this.bodyName = bodyName;
        this.rendered = false;
        this.hasCollision = hasCollision;
    }
    public CelestialBody(String bodyName) {
        this.bodyName = bodyName;
        this.rendered = false;
    }



    public CelestialBody addOrbitingBody(CelestialBody celestialBody) {
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

    public CelestialBody setRenderDetails(Identifier texture) {
        this.rendered = true;
        this.textureName = texture;
        return this;
    }
    public CelestialBody setRenderDetails(Identifier texture, Identifier upperLayerTextureName) {
        this.rendered = true;
        this.textureName = texture;
        this.upperLayerTextureName = upperLayerTextureName;
        return this;
    }

    public CelestialBody setPhysicalDetails(int radius,double rotationTime, int orbitDistance, double orbitTime) {
        return setPhysicalDetails(radius,rotationTime,orbitDistance,orbitTime,0);
    }
    public CelestialBody setPhysicalDetails(int radius,double rotationTime, int orbitDistance, double orbitTime,double orbitOffset) {
        this.radius = radius;
        this.rotationTime = rotationTime;
        this.orbitDistance = orbitDistance;
        this.orbitTime = orbitTime;
        this.orbitOffset = orbitOffset;
        return this;
    }


    private void addReferenceFrame(CelestialBody parent) {
        currentPosition = currentPosition.add(parent.currentPosition);
    }

    public Vec3d getPlanetVelocity() {
        return currentPosition.subtract(lastPosition);
    }

    public void tickPosition(long universeTime) {
        tickPosition(1,universeTime);
    }

    public void tickPosition(int recursions,long universeTime) {
        if (recursions > MAX_RECURSION_LEVELS) {
            return;
        }
        lastPosition = new Vec3d(currentPosition.x,currentPosition.y,currentPosition.z);
        if (!staticPosition) {
            currentOrbitAngle = (orbitOffset + universeTime * (360 / (orbitTime * 24000))) % 360;
            double radians = Math.toRadians(currentOrbitAngle);
            currentPosition = new Vec3d(Math.sin(radians) * orbitDistance,0,Math.cos(radians) * orbitDistance);
        }
        orbitingCelestialBodies.forEach((celestialBody -> {
            celestialBody.tickPosition(recursions +1,universeTime);
            celestialBody.addReferenceFrame(this);
        }));
    }

    public void addBodiesRecursive(List<CelestialBody> bodies) {
        bodies.add(this);
        if (orbitingCelestialBodies.isEmpty()) {
            return;
        }
        orbitingCelestialBodies.forEach((b) -> b.addBodiesRecursive(bodies));
    }
}
