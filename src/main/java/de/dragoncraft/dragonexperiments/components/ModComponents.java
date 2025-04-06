package de.dragoncraft.dragonexperiments.components;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class ModComponents implements WorldComponentInitializer {

    public static final ComponentKey<ShipComponent> SHIP_COMPONENT =
            ComponentRegistry.getOrCreate(Identifier.of(MOD_ID, "ship-controller"), ShipComponent.class);
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(SHIP_COMPONENT, ShipComponent::new);
    }
}
