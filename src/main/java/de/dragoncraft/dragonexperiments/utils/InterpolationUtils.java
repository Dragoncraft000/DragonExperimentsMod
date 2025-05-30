package de.dragoncraft.dragonexperiments.utils;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class InterpolationUtils {
    public static double lerp(double a, double b, float t) {
        return a + t * (b - a);
    }

    public static Vec3d interpolateLinear(Vec3d start, Vec3d end, float t) {
        double x = lerp(start.getX(), end.getX(), t);
        double y = lerp(start.getY(), end.getY(), t);
        double z = lerp(start.getZ(), end.getZ(), t);

        return new Vec3d(x, y, z);
    }
    public static Vector3f interpolateLinear(Vector3f start, Vector3f end, float t) {
        double x = lerp(start.x, end.x, t);
        double y = lerp(start.y(), end.y, t);
        double z = lerp(start.z(), end.z, t);

        return new Vector3f((float) x, (float) y, (float) z);
    }

}
