package de.dragoncraft.dragonexperiments.item;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
	public static Item register(Item item, String id) {
		Identifier itemID = Identifier.of(DragonExperiments.MOD_ID, id);
		return Registry.register(Registries.ITEM, itemID, item);
	}
	public static void initialize() {}

	public static final Item TEST_ITEM = register(new TestItem(new Item.Settings()), "test_item");
}