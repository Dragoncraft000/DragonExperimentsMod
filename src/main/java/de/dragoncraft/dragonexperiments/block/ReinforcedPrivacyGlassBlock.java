package de.dragoncraft.dragonexperiments.block;

import de.dragoncraft.dragonexperiments.block.privacyglass.ReinforcedToggleGlassBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReinforcedPrivacyGlassBlock extends PrivacyGlassBlock {

    public ReinforcedPrivacyGlassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedToggleGlassBlockEntity(pos,state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }

}
