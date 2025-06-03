package de.dragoncraft.dragonexperiments.solarsystem;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class UniversePresets {
    public static Universe smallUniverse() {
        return new Universe().addBody(new Star("sun")
                .setAtmosphere(new Vec3d(1,0.65,0.1),2,53,1)
                .setRenderDetails(20_000,1, Identifier.of(MOD_ID,"textures/shader/planet/sun.png"))
                .disableOrbit()
                .addOrbitingBody(new Planet("mercury")
                        .disableAtmosphere()
                        .setRenderDetails(700,1,Identifier.of(MOD_ID,"textures/shader/planet/mercury.png"))
                        .setOrbit(75_000,88))
                .addOrbitingBody(new Planet("venus")
                        .setAtmosphere(new Vec3d(20,5,0),5f,105,1.2f,1f)
                        .setRenderDetails(2760,1,Identifier.of(MOD_ID,"textures/shader/planet/venus.png"),Identifier.of(MOD_ID,"textures/shader/planet/venus_clouds.png"))
                        .setOrbit(200_000,225))
                .addOrbitingBody(new Planet("earth")
                        .setAtmosphere(new Vec3d(55,130,224),5f,105,1.2f,2)
                        .setRenderDetails(2760,1,Identifier.of(MOD_ID,"textures/shader/planet/earth.png"),Identifier.of(MOD_ID,"textures/shader/planet/clouds.png"))
                        .setOrbit(300_000,365)
                        .addOrbitingBody(new Planet("moon")
                                .disableAtmosphere()
                                .setRenderDetails(600,1, Identifier.of(MOD_ID,"textures/shader/planet/moon.png"))
                                .setOrbit(14000,30)))
                .addOrbitingBody(new Planet("mars")
                        .setAtmosphere(new Vec3d(224,70,55),3f,0.7f)
                        .setRenderDetails(1400,1,Identifier.of(MOD_ID,"textures/shader/planet/mars.png"))
                        .setOrbit(450_000,687))
                .addOrbitingBody(new Planet("jupiter")
                        .setAtmosphere(new Vec3d(140,220,800),2f,200,0.5f,2f)
                        .setRenderDetails(12_000,1,Identifier.of(MOD_ID,"textures/shader/planet/jupiter.png"))
                        .setOrbit(900_000,4333)
                .addOrbitingBody(new Planet("hibenja")
                        .setAtmosphere(new Vec3d(224,70,55),3f,0.7f)
                        .setRenderDetails(300,1,Identifier.of(MOD_ID,"textures/shader/planet/mars.png"))
                        .setOrbit(1_000_000_000,1000000000)))
        );
    }

    //Realistic Sun Atmosphere (use planet type)
    //setAtmosphere(new Vec3d(100,65,10),20,53,200)
}
