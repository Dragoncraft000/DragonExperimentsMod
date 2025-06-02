package de.dragoncraft.dragonexperiments.solarsystem;

import lombok.Getter;
import net.minecraft.util.math.Vec3d;

@Getter
public class Star extends CelestialBody {

    public static Vec3d[] temperatureColors = new Vec3d[]{
            new Vec3d(255 / 255f,42 / 255f,40 / 255f),
            new Vec3d(255 / 255f,174 / 255f,70 / 255f),
            new Vec3d(255 / 255f,195 / 255f,72 / 255f),
            new Vec3d(255 / 255f,42 / 255f,40 / 255f),
            new Vec3d(255 / 255f,42 / 255f,40 / 255f),
    };




    protected Vec3d color = new Vec3d(0,0,0);
    protected float atmosphereBrightness = 1;
    protected float atmosphereSize = 50;
    protected float atmosphereFalloff = 50;

    public Star(String bodyName) {
        super(bodyName);
    }

    public Star setAtmosphere(Vec3d color,float brigtness,float size,float falloff) {
        this.color = color;
        this.atmosphereBrightness = brigtness;
        this.atmosphereSize = size;
        this.atmosphereFalloff = falloff;
        return this;
    }
}
