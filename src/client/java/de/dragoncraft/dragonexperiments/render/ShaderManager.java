package de.dragoncraft.dragonexperiments.render;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.DragonExperimentsClient;
import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import de.dragoncraft.dragonexperiments.ship.ShipController;
import de.dragoncraft.dragonexperiments.utils.InterpolationUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShaderManager {

    private static final Identifier SPACE_WORLD = Identifier.of(DragonExperiments.MOD_ID, "space");
    private static final Identifier SPACE_RENDER_PIPELINE = Identifier.of(DragonExperiments.MOD_ID, "space_shader");

    private static final Identifier PLANET_SHADER = Identifier.of(DragonExperiments.MOD_ID, "planet");
    private static boolean planetEnabled = true;
    private static boolean registered = false;

    private static Vector3f lastRot = new Vector3f(0);

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
                registered = false;
            }
            return;
        }
        ensureRegistered();
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        ShaderProgram earth = VeilRenderSystem.renderer().getShaderManager().getShader(PLANET_SHADER);
        if (pipeline != null) {
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
            pipeline.setVector("ShipPos",currentPos.toVector3f());

            currentRot =  new Quaternionf(currentRot).slerp(targetRot, currentInterpolationTime);

            Vector4f rot = new Vector4f(currentRot.x,currentRot.y,currentRot.z,currentRot.w);
            pipeline.setVector("ShipRotation",rot);
        }
        if (earth != null) {
            long time = System.nanoTime();
            double speedMod = 1e-9;
            double xPos = Math.sin(time * speedMod);
            double zPos = Math.cos(time * speedMod);
            float orbitDistance = 0;
            earth.setVector("PlanetPos",(float) xPos * orbitDistance,0,(float) zPos * orbitDistance);
            earth.setFloat("PlanetSize", 6378f);
            earth.setFloat("AtmosphereSize", 50f);
            earth.setFloat("PlanetRotationSpeed", 1f);
            earth.setVector("AtmosphereRayleighCoeffiecents",5.5e-3f, 13.0e-3f, 22.4e-3f);
            earth.setFloat("AtmosphereMieCoeffiecent", (21e-3f * 0.5f));
            earth.setFloat("AtmosphereBrightness", 0.9f);
            earth.setFloat("AtmosphereRayleighScaleHeight",(5f));
            earth.setFloat("AtmosphereMieScaleHeight",1.2f);

        }
        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline);
    }


    public static void ensureRegistered() {
        if (registered) return;

        boolean added = VeilRenderSystem.renderer().getPostProcessingManager().add(SPACE_RENDER_PIPELINE);
        if (added) {
            System.out.println("Space pipeline registered.");
            registered = true;
        } else {
            System.err.println("Failed to register Space pipeline!");
        }
    }

    public static void toggleSpaceShader() {
        planetEnabled = !planetEnabled;
    }
}