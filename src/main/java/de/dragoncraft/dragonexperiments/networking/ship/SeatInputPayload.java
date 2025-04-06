package de.dragoncraft.dragonexperiments.networking.ship;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.networking.NetworkingConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector3f;

import java.util.UUID;

public record SeatInputPayload(UUID playerId, String action, Vector3f mouseMove,Vector3f steering) implements CustomPayload {
    public static final Id<SeatInputPayload> ID = new CustomPayload.Id<>(NetworkingConstants.SHIP_SEAT_INPUT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SeatInputPayload> CODEC = PacketCodec.tuple(
             Uuids.PACKET_CODEC, SeatInputPayload::playerId,
             PacketCodecs.STRING, SeatInputPayload::action,
             PacketCodecs.VECTOR3F,SeatInputPayload::mouseMove,
            PacketCodecs.VECTOR3F,SeatInputPayload::steering,
            SeatInputPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static SeatInputPayload decode(PacketByteBuf buf) {
        return new SeatInputPayload(buf.readUuid(), buf.readString(32767),buf.readVector3f(),buf.readVector3f());
    }

    public void encode(PacketByteBuf buf) {
        buf.writeUuid(playerId);
        buf.writeString(action,32767);
        buf.writeVector3f(mouseMove);
        buf.writeVector3f(steering);

    }
}
