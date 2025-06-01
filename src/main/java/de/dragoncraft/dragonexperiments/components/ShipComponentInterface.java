package de.dragoncraft.dragonexperiments.components;

import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface ShipComponentInterface extends ComponentV3 , AutoSyncedComponent, ServerTickingComponent {
    Vec3d getShipPosition();

    Vec3d getShipOrigin();
    Quaternionf getShipRotation();
    Vec3d getShipVelocity();
    Quaternionf getShipAngularVelocity();
}

