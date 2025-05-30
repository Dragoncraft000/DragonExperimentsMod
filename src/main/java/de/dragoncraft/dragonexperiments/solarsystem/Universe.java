package de.dragoncraft.dragonexperiments.solarsystem;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class Universe {

    private static final List<CelestialBody> celestialBodies = new ArrayList<>();


    private static final CelestialBody MOON = new Planet("moon")
            .disableAtmosphere()
            .setRenderDetails(1737,1, Identifier.of(MOD_ID,"textures/shader/planet/moon.png"))
            .setOrbit(36300,30);
    private static final Planet EARTH = (Planet) new Planet("earth")
            .setAtmosphere(new Vec3d(55,130,224),5f,105,1.2f)
            .setRenderDetails(6378,1,Identifier.of(MOD_ID,"textures/shader/planet/earth.png"),Identifier.of(MOD_ID,"textures/shader/planet/clouds.png"))
            .setOrbit((int) (149_598_0 / 4f),365000000)
            .addOribitingPlanet(MOON);
    private static final CelestialBody SUN = new Star("sun")
            .setAtmosphere(new Vec3d(1,0.65,0),1,53,1)
            .setRenderDetails(50000,1,Identifier.of(MOD_ID,"textures/shader/planet/sun.png"))
            .disableOrbit()
            .addOribitingPlanet(EARTH);

    public static void initialize() {
            celestialBodies.add(SUN);
    }

    public static void tickBodies() {
        MOON.setOrbit(36300,1);
        EARTH.setOrbit(600000,1);
        celestialBodies.forEach(CelestialBody::tickPosition);
    }
    public static List<CelestialBody> getAllBodies() {
        List<CelestialBody> bodies = new ArrayList<>();
        celestialBodies.forEach(celestialBody -> celestialBody.addBodiesRecursive(bodies));
        return bodies;
    }



}
