package de.dragoncraft.dragonexperiments.item;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final RegistryKey<ItemGroup> MAIN_MOD_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(DragonExperiments.MOD_ID, "item_group"));
    public static final ItemGroup MAIN_MOD_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.COMMAND_BLOCK))
            .displayName(Text.translatable("itemGroup.dragon_experiments_group"))
            .build();

    public static void initialize() {

        // Register the group.
        Registry.register(Registries.ITEM_GROUP, MAIN_MOD_GROUP_KEY, MAIN_MOD_GROUP);
        ItemGroupEvents.modifyEntriesEvent(MAIN_MOD_GROUP_KEY).register((itemGroup) -> {
                    itemGroup.add(ModItems.TEST_ITEM);
                    itemGroup.add(ModBlocks.TEST_BLOCK);
                    itemGroup.add(ModBlocks.SHIP_SEAT_BLOCK);
        });
    }
}
