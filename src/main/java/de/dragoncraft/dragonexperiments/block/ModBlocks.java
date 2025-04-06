package de.dragoncraft.dragonexperiments.block;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.item.TestItem;
import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

	public static final Block TEST_BLOCK = registerBlock(new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)),"test_block");

	public static final Block SHIP_SEAT_BLOCK = registerBlock(new ShipSeatBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).strength(1.0f).nonOpaque()),"ship_seat");
	private static Item registerBlockItem(Block block, String id) {
		return Registry.register(Registries.ITEM, Identifier.of(DragonExperiments.MOD_ID,id),
				new BlockItem(block,new Item.Settings()));
	}

	private static Block registerBlock(Block block, String id) {
		registerBlockItem(block,id);
		return Registry.register(Registries.BLOCK,Identifier.of(DragonExperiments.MOD_ID,id),block);
	}
	public static void initialize() {}
}