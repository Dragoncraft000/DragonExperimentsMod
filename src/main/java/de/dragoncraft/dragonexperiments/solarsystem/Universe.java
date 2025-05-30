package de.dragoncraft.dragonexperiments.solarsystem;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class Universe {

    private static final List<CelestialBody> celestialBodies = new ArrayList<>();


    private static final CelestialBody MOON = new CelestialBody("moon")
            .setRenderDetails(1737,1, Identifier.of(MOD_ID,"textures/shader/planet/moon.png"))
            .setOrbit(36300,30)
            .disableAtmosphere();
    private static final CelestialBody EARTH = new CelestialBody("earth")
            .setRenderDetails(6378,1,Identifier.of(MOD_ID,"textures/shader/planet/earth.png"),Identifier.of(MOD_ID,"textures/shader/planet/clouds.png"))
            .setOrbit((int) (149_598_0 / 4f),365000000)
            .setAtmosphere(new Vec3d(55,130,224),5f,105,1.2f)
            .addOribitingPlanet(MOON);
    private static final CelestialBody SUN = new CelestialBody("sun")
            .disableAtmosphere()
            .setRenderDetails(50000,1,Identifier.of(MOD_ID,"textures/shader/planet/sun.png"))
            .disableOrbit()
            .addOribitingPlanet(EARTH);

    public static void initialize() {
            celestialBodies.add(SUN);
    }

    public static void tickBodies() {

        EARTH.setOrbit(600000,1);

        celestialBodies.forEach(CelestialBody::tickPosition);
    }
    public static List<CelestialBody> getAllBodies() {
        List<CelestialBody> bodies = new ArrayList<>();
        celestialBodies.forEach(celestialBody -> celestialBody.addBodiesRecursive(bodies));
        return bodies;
    }



}
