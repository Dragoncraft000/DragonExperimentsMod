package de.dragoncraft.dragonexperiments.networking.ship;

import de.dragoncraft.dragonexperiments.entity.SeatEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class SeatInputReciever {

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(SeatInputPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            context.server().execute(() -> {
                if (player.hasVehicle() && player.getVehicle() instanceof SeatEntity seat) {
                    seat.handlePlayerSteer(player,payload.mouseMove(),payload.steering());
                }
            });
        });
    }
}
