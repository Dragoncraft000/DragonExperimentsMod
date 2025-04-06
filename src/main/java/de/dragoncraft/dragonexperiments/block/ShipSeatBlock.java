package de.dragoncraft.dragonexperiments.block;

import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.entity.SeatEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ShipSeatBlock extends Block {
    public ShipSeatBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.getVehicle() == null) {
                SeatEntity seat = ModEntities.SEAT_ENTITY.create(world);
                if (seat != null) {
                    seat.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 0, 0);
                    seat.setInvisible(true);
                    seat.setNoGravity(true);
                    world.spawnEntity(seat);
                    player.startRiding(seat);
                } else {
                    System.out.println("Failed to create seat entity!");
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.125f, 0.0f, 0.125f, 0.875f, 0.5f, 0.875f); // Example: A half-block seat
    }

}
