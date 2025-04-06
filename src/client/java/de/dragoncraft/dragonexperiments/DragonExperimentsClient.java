package de.dragoncraft.dragonexperiments;

import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.render.ShaderManager;
import de.dragoncraft.dragonexperiments.render.entity.ShipSeatEntityRender;
import de.dragoncraft.dragonexperiments.ship.ShipController;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.fabric.event.FabricVeilRenderLevelStageEvent;
import foundry.veil.impl.client.render.pipeline.PostPipelineContext;
import foundry.veil.platform.VeilEventPlatform;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class DragonExperimentsClient implements ClientModInitializer {

	private static final Identifier SPACE_RENDER_PIPELINE = Identifier.of(DragonExperiments.MOD_ID, "space_shader");

	@Getter
	private static ShipController shipController;

	@Override
	public void onInitializeClient() {
		ShaderManager.initialize();
		KeybindManager.initialize();
			// This works for pipeline-specific uniforms
/*		VeilEventPlatform.INSTANCE.preVeilPostProcessing((pipelineName, pipeline, context) -> {
			if (SPACE_RENDER_PIPELINE.equals(pipelineName)) {
				ShaderManager.updateSpaceShader(pipelineName,pipeline,context);
			}
		});*/
		FabricVeilRenderLevelStageEvent.EVENT.register((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {

				ShaderManager.updateSpaceShader(deltaTracker.getTickDelta(true));
			}
		});

		shipController = new ShipController();
		ClientTickEvents.START_CLIENT_TICK.register((client) -> shipController.updateLastPos());

		EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, ShipSeatEntityRender::new);

	}
}