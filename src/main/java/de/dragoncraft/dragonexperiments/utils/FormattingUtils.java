package de.dragoncraft.dragonexperiments.utils;

import net.minecraft.util.math.Vec3d;

public class FormattingUtils {


    public static String formatVec3d(Vec3d input) {

        return "(" + (int) input.x + ", " + (int) input.y + ", " + (int) input.z + ")";
    }
    public static String formatVelocity(int input) {
        if (input > 50) {
            return (int) (input * 0.001 * 20) + "km/s";
        }
        return input * 20 + "m/s";
    }

}
