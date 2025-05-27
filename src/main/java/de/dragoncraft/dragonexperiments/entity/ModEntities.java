package de.dragoncraft.dragonexperiments.entity;

import de.dragoncraft.dragonexperiments.block.ModBlocks;
import de.dragoncraft.dragonexperiments.block.privacyglass.ReinforcedToggleGlassBlockEntity;
import de.dragoncraft.dragonexperiments.block.privacyglass.ToggleGlassBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class ModEntities {

    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "seat_entity"),
            EntityType.Builder.create(SeatEntity::new,SpawnGroup.MISC)
                    .dimensions(0.0F, 0.0F)
                    .build()
    );

    public static final BlockEntityType<ToggleGlassBlockEntity> TOGGLE_GLASS_BLOCK_ENTITY_TYPE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(MOD_ID, "toggle_privacy_glass_entity"),
                    BlockEntityType.Builder.create(ToggleGlassBlockEntity::new,ModBlocks.PRIVACY_GLASS_BLOCK).build(null)
            );
    public static final BlockEntityType<ReinforcedToggleGlassBlockEntity> REINFORCED_GLASS_BLOCK_ENTITY_TYPE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(MOD_ID, "reinforced_toggle_privacy_glass_entity"),
                    BlockEntityType.Builder.create(ReinforcedToggleGlassBlockEntity::new,ModBlocks.REINFORCED_PRIVACY_GLASS_BLOCK).build(null)
            );

    public static void initialize() {
    }

}
