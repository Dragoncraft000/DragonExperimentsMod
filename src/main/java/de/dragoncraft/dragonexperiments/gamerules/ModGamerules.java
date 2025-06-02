package de.dragoncraft.dragonexperiments.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {

    public static final GameRules.Key<GameRules.IntRule> SHIP_ACCELERATION =
            GameRuleRegistry.register("shipAcceleration", GameRules.Category.MISC, GameRuleFactory.createIntRule(20000,1));
    public static final GameRules.Key<GameRules.IntRule> SHIP_BRAKE =
            GameRuleRegistry.register("shipBrake", GameRules.Category.MISC, GameRuleFactory.createIntRule(20000,1));
    public static final GameRules.Key<GameRules.IntRule> SHIP_ROTATION_SENSITIVITY =
            GameRuleRegistry.register("shipRotationSensitivity", GameRules.Category.MISC, GameRuleFactory.createIntRule(200,1));

    public static void initialize() {}
}
