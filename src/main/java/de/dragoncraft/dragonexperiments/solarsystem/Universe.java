package de.dragoncraft.dragonexperiments.solarsystem;

import lombok.Getter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class Universe {


    @Getter
    private int hash = 1;

    @Getter
    private World physicalWorld;
    private List<CelestialBody> celestialBodies = new ArrayList<>();

    private final Map<String,CelestialBody> cachedBodyNames = new HashMap<>();

    private Map<Identifier,String> dimensionCelestialBodies = new HashMap<>();

    public Universe() {}
    public Universe(World physicalWorld) {
        this.physicalWorld = physicalWorld;
    }

    public void reconstruct(Universe newUniverse) {
        celestialBodies = newUniverse.celestialBodies;
        dimensionCelestialBodies = newUniverse.dimensionCelestialBodies;
        updateBodyNames();
        hash++;
    }

    public Universe addBody(CelestialBody body) {
        celestialBodies.add(body);
        updateBodyNames();
        return this;
    }
    public Universe addDimensionLinkedBody(Identifier dimension,String bodyName) {
        dimensionCelestialBodies.put(dimension,bodyName);
        return this;
    }
    public CelestialBody getDimensionLinkedBody(Identifier dimension) {
        System.out.println(Arrays.toString(dimensionCelestialBodies.keySet().toArray()));
        if (!dimensionCelestialBodies.containsKey(dimension)) {
            return null;
        }
        System.out.println(dimension);
        return getCelestialBody(dimensionCelestialBodies.get(dimension));
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
    public CelestialBody getNearestBody(Vec3d current) {
        List<CelestialBody> sorted = getAllBodies();
        sorted.sort(Comparator.comparingDouble(c -> c.getCurrentPosition().squaredDistanceTo(new Vec3d(current.x,current.y,current.z))));
        return sorted.getFirst();
    }

    private void updateBodyNames() {
        cachedBodyNames.clear();
        getAllBodies().forEach((c) -> cachedBodyNames.put(c.getBodyName(),c));
    }

    public CelestialBody getCachedCelestialBody(String name) {
        return cachedBodyNames.get(name);
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
    public String[] getAllBodyNames() {
        return cachedBodyNames.keySet().toArray(new String[0]);
    }

    public List<Identifier> getAllTextures() {
        List<CelestialBody> bodies = getAllBodies();
        List<Identifier> textures = new ArrayList<>();
        for (CelestialBody body : bodies) {
            if (body.getTextureName() != null && !textures.contains(body.getTextureName())) {
                textures.add(body.getTextureName());
            }
            if (body.getUpperLayerTextureName() != null && !textures.contains(body.getUpperLayerTextureName())) {
                textures.add(body.getUpperLayerTextureName());
            }
        }
        return textures;
    }

}
