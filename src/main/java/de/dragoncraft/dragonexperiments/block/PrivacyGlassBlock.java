package de.dragoncraft.dragonexperiments.block;

import de.dragoncraft.dragonexperiments.block.privacyglass.ToggleGlassBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PrivacyGlassBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty TRANSPARENT = BooleanProperty.of("closed");
    private static int nextUpdateId = 1;

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentState, Direction direction) {
        return state.getBlock() == adjacentState.getBlock();
    }

    public PrivacyGlassBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TRANSPARENT, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRANSPARENT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        int updateId = nextUpdateId++;
        boolean newTransparency = !state.get(TRANSPARENT);
        propagateToggle((ServerWorld) world, pos, newTransparency, updateId);
        return ActionResult.SUCCESS;
    }

    private void propagateToggle(ServerWorld world, BlockPos pos, boolean newTransparency, int updateId) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ToggleGlassBlockEntity glassBe)) return;
        if (glassBe.getUpdateId() >= updateId) return;
        glassBe.setUpdateId(updateId);
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(TRANSPARENT, newTransparency), Block.NOTIFY_ALL);
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.BLOCKS,
            0.5f, 1.8f + world.random.nextFloat() * 0.2f);
        if (state.getBlock() instanceof PrivacyGlassBlock) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.offset(dir);
                BlockEntity neighborBe = world.getBlockEntity(neighbor);
                if (neighborBe instanceof ToggleGlassBlockEntity neighborGlass &&
                        neighborGlass.getUpdateId() != updateId) {
                    neighborGlass.setPendingUpdate(newTransparency, updateId);
                    world.scheduleBlockTick(neighbor, this, 1);
                }
            }
        }
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ToggleGlassBlockEntity glassBe)) return;

        int updateId = glassBe.getPendingUpdateId();
        boolean newTransparency = glassBe.getPendingTransparency();

        propagateToggle(world, pos, newTransparency, updateId);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToggleGlassBlockEntity(pos,state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(TRANSPARENT) ? 15 : 0;
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        boolean powered = world.isReceivingRedstonePower(pos);
        ToggleGlassBlockEntity be = (ToggleGlassBlockEntity) world.getBlockEntity(pos);
        if (be == null) return;
        if (powered && !be.wasRedstonePowered()) {
            be.setRedstonePowered(true);
            int updateId = nextUpdateId++;
            boolean newTransparency = !state.get(PrivacyGlassBlock.TRANSPARENT);
            propagateToggle((ServerWorld) world, pos, newTransparency, updateId);
        } else if (!powered && be.wasRedstonePowered()) {
            be.setRedstonePowered(false);
        }
    }



}


