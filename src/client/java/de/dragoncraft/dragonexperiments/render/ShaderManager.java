package de.dragoncraft.dragonexperiments.render;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import de.dragoncraft.dragonexperiments.solarsystem.CelestialBody;
import de.dragoncraft.dragonexperiments.solarsystem.Universe;
import de.dragoncraft.dragonexperiments.utils.InterpolationUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class ShaderManager {

    private static final Identifier SPACE_WORLD = Identifier.of(DragonExperiments.MOD_ID, "space");
    private static final Identifier SPACE_RENDER_PIPELINE = Identifier.of(DragonExperiments.MOD_ID, "space_shader");
    private static final Identifier BODY_RENDER_PIPELINE = Identifier.of(DragonExperiments.MOD_ID, "planet");

    private static final Identifier COMPOSE_PIPELINE = Identifier.of(DragonExperiments.MOD_ID, "compose");

    private static boolean registered = false;

    private static long lastUpdateTick = 0;

    private static Vec3d currentPos;

    private static Vec3d targetPos;

    private static Quaternionf currentRot;
    private static Quaternionf targetRot;

    public static void initialize() {

    }

    public static void updateSpaceShader() {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        if (VeilRenderSystem.renderer() == null) {
            return;
        }
        if (!MinecraftClient.getInstance().player.getEntityWorld().getRegistryKey().getValue().equals(SPACE_WORLD)) {
            if (registered) {
                VeilRenderSystem.renderer().getPostProcessingManager().remove(SPACE_RENDER_PIPELINE);
                VeilRenderSystem.renderer().getPostProcessingManager().remove(BODY_RENDER_PIPELINE);
                registered = false;
            }
            return;
        }
        PostPipeline backgroundPipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(BODY_RENDER_PIPELINE);
        PostPipeline composePipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(COMPOSE_PIPELINE);
        if (pipeline == null || backgroundPipeline == null) {
            return;
        }
        ShipComponent component = ModComponents.SHIP_COMPONENT.get( MinecraftClient.getInstance().player.getWorld());

        if (currentPos == null || currentRot == null) {
            currentPos = component.getShipPosition();
            currentRot = component.getShipRotation();
        }
        if (component.getShipRotation() != targetRot || component.getShipPosition() != targetPos || targetPos == null || targetRot == null) {
            targetRot = component.getShipRotation();
            targetPos = component.getShipPosition();
            lastUpdateTick = MinecraftClient.getInstance().player.getWorld().getTime();
        }

        int interpolationTime = 20;

        float currentInterpolationTime =
                (MinecraftClient.getInstance().player.getWorld().getTime() - lastUpdateTick
                        + MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false)) / interpolationTime;
        currentInterpolationTime = Math.min(currentInterpolationTime,1);


        currentPos = InterpolationUtils.interpolateLinear(currentPos,targetPos,currentInterpolationTime);
        backgroundPipeline.setVector("ShipPos",currentPos.toVector3f());
        pipeline.setVector("ShipPos",currentPos.toVector3f());

        currentRot =  new Quaternionf(currentRot).slerp(targetRot, currentInterpolationTime);

        Vector4f rot = new Vector4f(currentRot.x,currentRot.y,currentRot.z,currentRot.w);
        pipeline.setVector("ShipRotation",rot);
        pipeline.setVector("LightPosition",new Vector3f(0,10000000,0));
        backgroundPipeline.setVector("ShipRotation",rot);

        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(backgroundPipeline,false);
        List<CelestialBody> bodiesToRender = Universe.getAllBodies();
        float finalCurrentInterpolationTime = currentInterpolationTime;

        // CPU Render Sorting
        // Does not work well with larger planets and close distances

/*        bodiesToRender.sort((c1,c2) -> {
            double dist1 = c1.getCurrentPosition().subtract(currentPos).length();
            double dist2 = c2.getCurrentPosition().subtract(currentPos).length();
            if (dist1 == dist2) {
                return 0;
            }else if (dist1 > dist2) {
                return -1;
            }
            return 1;
        });*/
        bodiesToRender.forEach(celestialBody -> {
            int textureId = -1;
            if (celestialBody.getTextureName() != null) {
                TextureManager manager = MinecraftClient.getInstance().getTextureManager();
                manager.bindTexture(celestialBody.getTextureName());
                textureId = manager.getTexture(celestialBody.getTextureName()).getGlId();
            }
            int upperLayerTextureId = -1;
            if (celestialBody.getUpperLayerTextureName() != null) {
                TextureManager manager = MinecraftClient.getInstance().getTextureManager();
                manager.bindTexture(celestialBody.getUpperLayerTextureName());
                upperLayerTextureId = manager.getTexture(celestialBody.getUpperLayerTextureName()).getGlId();
            }
            if (!celestialBody.renderBody(pipeline, textureId,upperLayerTextureId,finalCurrentInterpolationTime)) {
                return;
            }
            VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline,false);
        });
        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(composePipeline,true);
    }
}