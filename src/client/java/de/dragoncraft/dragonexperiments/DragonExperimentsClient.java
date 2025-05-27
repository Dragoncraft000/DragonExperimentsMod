package de.dragoncraft.dragonexperiments;

import de.dragoncraft.dragonexperiments.block.ModBlocks;
import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.render.ShaderManager;
import de.dragoncraft.dragonexperiments.render.entity.ShipSeatEntityRender;
import de.dragoncraft.dragonexperiments.ship.ShipController;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.fabric.event.FabricVeilRenderLevelStageEvent;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class DragonExperimentsClient implements ClientModInitializer {

	@Getter
	private static ShipController shipController;

	@Override
	public void onInitializeClient() {
		ShaderManager.initialize();
		KeybindManager.initialize();

		FabricVeilRenderLevelStageEvent.EVENT.register((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
				ShaderManager.updateSpaceShader();
			}
		});

		shipController = new ShipController();
		ClientTickEvents.START_CLIENT_TICK.register((client) -> shipController.updateLastPos());

		EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, ShipSeatEntityRender::new);


		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PRIVACY_GLASS_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_PRIVACY_GLASS_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLEAR_GLASS_BLOCK, RenderLayer.getTranslucent());
	}
}