package de.dragoncraft.dragonexperiments;

import de.dragoncraft.dragonexperiments.block.ModBlocks;
import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.item.ModItemGroups;
import de.dragoncraft.dragonexperiments.item.ModItems;
import de.dragoncraft.dragonexperiments.networking.ship.SeatInputPayload;
import de.dragoncraft.dragonexperiments.networking.ship.SeatInputReciever;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DragonExperiments implements ModInitializer {
	public static final String MOD_ID = "dragon_experiments";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItemGroups.initialize();
		ModItems.initialize();
		ModBlocks.initialize();
		ModEntities.initialize();
		PayloadTypeRegistry.playC2S().register(SeatInputPayload.ID, SeatInputPayload.CODEC);
		SeatInputReciever.initialize();
	}
}