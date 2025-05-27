package de.dragoncraft.dragonexperiments.block.privacyglass;

import de.dragoncraft.dragonexperiments.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ReinforcedToggleGlassBlockEntity extends ToggleGlassBlockEntity {

    public ReinforcedToggleGlassBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.REINFORCED_GLASS_BLOCK_ENTITY_TYPE, pos, state);
    }
}