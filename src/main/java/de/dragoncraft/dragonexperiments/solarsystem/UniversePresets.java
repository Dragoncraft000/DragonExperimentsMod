package de.dragoncraft.dragonexperiments.solarsystem;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class UniversePresets {
    public static Universe SMALL_SOLAR_SYSTEM = new Universe()
            .addBody(new Star("sun")
                    .setAtmosphere(new Vec3d(1,0.65,0.1),1,53,1)
                    .setRenderDetails(20000,1, Identifier.of(MOD_ID,"textures/shader/planet/sun.png"))
                    .disableOrbit()
                    .addOrbitingBody(new Planet("mercury")
                            .disableAtmosphere()
                            .setRenderDetails(700,1,Identifier.of(MOD_ID,"textures/shader/planet/mercury.png"))
                            .setOrbit(75000,0.1))
                    .addOrbitingBody(new Planet("venus")
                            .setAtmosphere(new Vec3d(20,5,0),5f,105,1.2f,1f)
                            .setRenderDetails(2760,1,Identifier.of(MOD_ID,"textures/shader/planet/venus.png"),Identifier.of(MOD_ID,"textures/shader/planet/venus_clouds.png"))
                            .setOrbit(200000,0.1))
                    .addOrbitingBody(new Planet("earth")
                            .setAtmosphere(new Vec3d(55,130,224),5f,105,1.2f,2)
                            .setRenderDetails(2760,1,Identifier.of(MOD_ID,"textures/shader/planet/earth.png"),Identifier.of(MOD_ID,"textures/shader/planet/clouds.png"))
                            .setOrbit(300000,0.1)
                            .addOrbitingBody(new Planet("moon")
                                    .disableAtmosphere()
                                    .setRenderDetails(600,1, Identifier.of(MOD_ID,"textures/shader/planet/moon.png"))
                                    .setOrbit(14000,30)))
                    .addOrbitingBody(new Planet("mars")
                            .setAtmosphere(new Vec3d(224,70,55),3f,0.7f)
                            .setRenderDetails(1400,1,Identifier.of(MOD_ID,"textures/shader/planet/mars.png"))
                            .setOrbit(450000,0.1))
            );

}
