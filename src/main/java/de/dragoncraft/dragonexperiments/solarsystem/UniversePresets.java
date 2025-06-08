package de.dragoncraft.dragonexperiments.solarsystem;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class UniversePresets {
    public static Universe smallUniverse() {
        return new Universe().addBody(new Star("sun")
                .setAtmosphere(new Vec3d(1,0.65,0.1),2,53,1)
                .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/sun.png"))
                .disableOrbit()
                .setPhysicalDetails(20_000,1,0,10000)
                .addOrbitingBody(new Planet("mercury")
                        .disableAtmosphere()
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/mercury.png"))
                        .setPhysicalDetails( 700,0.1,75_000,88))
                .addOrbitingBody(new Planet("venus")
                        .setAtmosphere(new Vec3d(20,5,0),5f,105,1.2f,1f)
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/venus.png"),Identifier.of(MOD_ID,"textures/shader/planet/venus_clouds.png"))
                        .setPhysicalDetails(2760,0.1,200_000,225))
                .addOrbitingBody(new Planet("earth")
                        .setAtmosphere(new Vec3d(55,130,224),5f,105,1.2f,2)
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/earth.png"),Identifier.of(MOD_ID,"textures/shader/planet/clouds.png"))
                        .setPhysicalDetails(2760,0.05,300_000,10)
                        .addOrbitingBody(new Planet("moon")
                                .disableAtmosphere()
                                .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/moon.png"))
                                .setPhysicalDetails(600,-3,14000,3)))
                .addOrbitingBody(new Planet("mars")
                        .setAtmosphere(new Vec3d(224,70,55),3f,0.7f)
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/mars.png"))
                        .setPhysicalDetails(1400,1,450_000,687))
                .addOrbitingBody(new Planet("jupiter")
                        .setAtmosphere(new Vec3d(140,220,800),2f,200,0.5f,2f)
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/jupiter.png"))
                        .setPhysicalDetails(12_000,1,900_000,4333)
                .addOrbitingBody(new Planet("hibenja")
                        .setAtmosphere(new Vec3d(224,70,55),3f,0.00000001f,10,0.7f)
                        .setRenderDetails(Identifier.of(MOD_ID,"textures/shader/planet/mars.png"))
                        .setPhysicalDetails(300,1,1_000_000_000,1000000000)))
        );
    }

    //Realistic Sun Atmosphere (use planet type)
    //setAtmosphere(new Vec3d(100,65,10),20,53,200)
}
