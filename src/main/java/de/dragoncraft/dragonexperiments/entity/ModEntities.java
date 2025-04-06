package de.dragoncraft.dragonexperiments.entity;

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

    public static void initialize() {
    }

}
