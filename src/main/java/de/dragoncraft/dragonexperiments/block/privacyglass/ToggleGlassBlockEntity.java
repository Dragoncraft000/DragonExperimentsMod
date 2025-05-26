package de.dragoncraft.dragonexperiments.block.privacyglass;

import de.dragoncraft.dragonexperiments.entity.ModEntities;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ToggleGlassBlockEntity extends BlockEntity {
    @Getter
    private int updateId = -1;

    private boolean pendingTransparency = false;
    @Getter
    private int pendingUpdateId = -1;

    public ToggleGlassBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.TOGGLE_GLASS_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void setUpdateId(int id) {
        this.updateId = id;
    }

    public void setPendingUpdate(boolean transparency, int updateId) {
        this.pendingTransparency = transparency;
        this.pendingUpdateId = updateId;
    }

    public boolean getPendingTransparency() {
        return pendingTransparency;
    }

}