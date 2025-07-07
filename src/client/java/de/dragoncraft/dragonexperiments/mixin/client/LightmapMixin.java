package de.dragoncraft.dragonexperiments.mixin.client;

import de.dragoncraft.dragonexperiments.render.ShaderManager;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapMixin {

    @ModifyArg(
            method = "update(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Vector3f;mul(F)Lorg/joml/Vector3f;"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyBrightness(F)F"),
                    to = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;add(Lorg/joml/Vector3fc;)Lorg/joml/Vector3f;")
            ),
            index = 0
    )
    private float modifySkyBrightnessArgument(float originalP) {
        if (ShaderManager.getCurrentCelestialBody() == null) {
            return originalP;
        }
        return ShaderManager.getPlanetSkyLight();
    }
}