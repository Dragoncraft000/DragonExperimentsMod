package de.dragoncraft.dragonexperiments.solarsystem;

import java.util.ArrayList;
import java.util.List;

public class Universe {

    private final List<CelestialBody> celestialBodies = new ArrayList<>();




    public Universe addBody(CelestialBody body) {
        celestialBodies.add(body);
        return this;
    }

    public void tickBodies() {
        celestialBodies.forEach(CelestialBody::tickPosition);
    }
    public List<CelestialBody> getAllBodies() {
        List<CelestialBody> bodies = new ArrayList<>();
        celestialBodies.forEach(celestialBody -> celestialBody.addBodiesRecursive(bodies));
        return bodies;
    }



}
