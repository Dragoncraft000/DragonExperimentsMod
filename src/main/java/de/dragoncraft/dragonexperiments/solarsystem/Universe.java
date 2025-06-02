package de.dragoncraft.dragonexperiments.solarsystem;

import lombok.Getter;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Universe {

    @Getter
    private World physicalWorld;
    private List<CelestialBody> celestialBodies = new ArrayList<>();

    public Universe() {}
    public Universe(World physicalWorld) {
        this.physicalWorld = physicalWorld;
    }

    public void reconstruct(Universe newUniverse) {
        celestialBodies = newUniverse.celestialBodies;
    }

    public Universe addBody(CelestialBody body) {
        celestialBodies.add(body);
        return this;
    }

    public void tickUniverse() {
        if (physicalWorld == null) {
            return;
        }
        tickBodies(physicalWorld.getTime());
    }

    public void tickBodies(long universeTime) {
        celestialBodies.forEach((celestialBody) -> celestialBody.tickPosition(universeTime));
    }
    public List<CelestialBody> getAllBodies() {
        List<CelestialBody> bodies = new ArrayList<>();
        celestialBodies.forEach(celestialBody -> celestialBody.addBodiesRecursive(bodies));
        return bodies;
    }

    public CelestialBody getCelestialBody(String name) {
        List<CelestialBody> bodies = getAllBodies();
        for (CelestialBody body : bodies) {
            if (Objects.equals(body.getBodyName(), name)) {
                return body;
            }
        }
        return null;
    }



}
