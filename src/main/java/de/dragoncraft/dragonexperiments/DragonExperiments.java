package de.dragoncraft.dragonexperiments;

import de.dragoncraft.dragonexperiments.block.ModBlocks;
import de.dragoncraft.dragonexperiments.commands.ModCommands;
import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.gamerules.ModGamerules;
import de.dragoncraft.dragonexperiments.item.ModItemGroups;
import de.dragoncraft.dragonexperiments.item.ModItems;
import de.dragoncraft.dragonexperiments.networking.ship.SeatInputPayload;
import de.dragoncraft.dragonexperiments.networking.ship.SeatInputReciever;
import de.dragoncraft.dragonexperiments.solarsystem.Universe;
import de.dragoncraft.dragonexperiments.solarsystem.UniversePresets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DragonExperiments implements ModInitializer {
	public static final String MOD_ID = "dragon_experiments";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Universe universe;
	public static RegistryKey<World> spaceWorld = null;

	@Override
	public void onInitialize() {

		TickTaskScheduler.init();
		ModItemGroups.initialize();
		ModItems.initialize();
		ModBlocks.initialize();
		ModEntities.initialize();
		ModGamerules.initialize();
		ModCommands.initialize();
		PayloadTypeRegistry.playC2S().register(SeatInputPayload.ID, SeatInputPayload.CODEC);
		SeatInputReciever.initialize();
		ServerWorldEvents.LOAD.register((event,world) -> {

			if (!Objects.equals(world.getRegistryKey().getValue(), Identifier.of(MOD_ID, "space"))) {
				return;
			}
			spaceWorld = world.getRegistryKey();
			universe = new Universe(world);
			universe.reconstruct(UniversePresets.smallUniverse());
		});
		ServerTickEvents.START_SERVER_TICK.register((id) -> {
			if (universe == null) return;
			universe.tickUniverse();
		});
	}
}