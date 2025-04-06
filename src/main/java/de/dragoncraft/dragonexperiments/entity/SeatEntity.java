package de.dragoncraft.dragonexperiments.entity;

import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class SeatEntity extends Entity  {
    public SeatEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public void onPassengerLookAround(Entity passenger) {
        if (passenger instanceof PlayerEntity player) {
            BlockState state = this.getWorld().getBlockState(this.getBlockPos());

            if (state.contains(Properties.HORIZONTAL_FACING)) {
                Direction facing = state.get(Properties.HORIZONTAL_FACING);
                float yaw = facing.asRotation();

                player.setYaw(yaw);
                player.setHeadYaw(yaw);
                player.setPitch(0);
            }
        }
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void tick() {
        super.tick();
        if (getFirstPassenger() == null) {
            kill();
            discard();
        }
    }
    public void handlePlayerSteer(PlayerEntity player, Vector3f move, Vector3f steering) {
        ShipComponent component = ModComponents.SHIP_COMPONENT.get( player.getWorld());
        component.addAngularMomentum(move.mul(0.002f));

        if (steering.y == -100) {
            component.brakeShip();
            return;
        }
        component.accelerateLocal(steering.mul(2));

    }

}