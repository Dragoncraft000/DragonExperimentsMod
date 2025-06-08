package de.dragoncraft.dragonexperiments.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import de.dragoncraft.dragonexperiments.solarsystem.CelestialBody;
import de.dragoncraft.dragonexperiments.solarsystem.UniversePresets;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ModCommands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("resetuniverse").executes(context -> {
                DragonExperiments.universe.reconstruct(UniversePresets.smallUniverse());
                context.getSource().sendFeedback(() -> Text.literal("Universe has been reconstructed"), false);
                return 1;
            }));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("movetoplanet")
                    .then(CommandManager.argument("celestialBody", StringArgumentType.greedyString())
                            .executes(context -> {
                                String name = StringArgumentType.getString(context, "celestialBody");
                                ServerCommandSource source = context.getSource();

                                if (DragonExperiments.universe == null) {
                                    source.sendFeedback(() -> Text.literal("No Universe loaded"), false);
                                    return 1;
                                }
                                CelestialBody body = DragonExperiments.universe.getCelestialBody(name);
                                if (body == null) {
                                    source.sendFeedback(() -> Text.literal(name + " can't be found"), false);
                                    return 1;
                                }
                                ShipComponent component = ModComponents.SHIP_COMPONENT.get(DragonExperiments.universe.getPhysicalWorld());
                                System.out.println(DragonExperiments.universe.getPhysicalWorld().getRegistryKey());
                                component.setShipPos(body.getCurrentPosition());
                                source.sendFeedback(() -> Text.literal("Moved Position of Ship to " + name), false);
                                return 1;
                            })
                    )
            );
            dispatcher.register(CommandManager.literal("teleporttoplanet")
                    .then(CommandManager.argument("celestialBody", StringArgumentType.greedyString())
                            .executes(context -> {
                                String name = StringArgumentType.getString(context, "celestialBody");
                                ServerCommandSource source = context.getSource();

                                if (DragonExperiments.universe == null) {
                                    source.sendFeedback(() -> Text.literal("No Universe loaded"), false);
                                    return 1;
                                }
                                CelestialBody body = DragonExperiments.universe.getCelestialBody(name);
                                if (body == null) {
                                    source.sendFeedback(() -> Text.literal(name + " can't be found"), false);
                                    return 1;
                                }
                                ShipComponent component = ModComponents.SHIP_COMPONENT.get(DragonExperiments.universe.getPhysicalWorld());
                                System.out.println(DragonExperiments.universe.getPhysicalWorld().getRegistryKey());
                                component.setShipPos(body.getCurrentPosition());
                                component.setShipVelocity(new Vec3d(0,0,0));
                                source.sendFeedback(() -> Text.literal("Teleported Ship to " + name), false);
                                return 1;
                            })
                    )
            );
        });

    }


}
