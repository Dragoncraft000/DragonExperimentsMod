package de.dragoncraft.dragonexperiments.ship;

import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class ShipController {

    @Getter@Setter
    private Vec3d lastPos = new Vec3d(0,0,0);

    public void updateLastPos() {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        ShipComponent component = ModComponents.SHIP_COMPONENT.get( MinecraftClient.getInstance().player.getWorld());
        lastPos = component.getShipPosition();
    }


    public void move(Vec3d movement) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        ShipComponent component = ModComponents.SHIP_COMPONENT.get( MinecraftClient.getInstance().player.getWorld());
        component.setShipPos(component.getShipPosition().add(movement));
    }

    public void accelerate(Vec3d acceleration) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        //ClientPlayNetworking.send();
        System.out.println("Accelerate");
        ShipComponent component = ModComponents.SHIP_COMPONENT.get( MinecraftClient.getInstance().player.getWorld());
        component.setShipVelocity(component.getShipVelocity().add(acceleration));
    }

}
