package de.dragoncraft.dragonexperiments.render;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.components.ModComponents;
import de.dragoncraft.dragonexperiments.components.ShipComponent;
import de.dragoncraft.dragonexperiments.solarsystem.CelestialBody;
import de.dragoncraft.dragonexperiments.solarsystem.Planet;
import de.dragoncraft.dragonexperiments.solarsystem.Star;
import de.dragoncraft.dragonexperiments.utils.InterpolationUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class ShaderManager {

    private static final Identifier SPACE_WORLD = Identifier.of(MOD_ID, "space");
    private static final Identifier SPACE_RENDER_PIPELINE = Identifier.of(MOD_ID, "space_shader");
    private static final Identifier CELESTIAL_BODY_SHADER = Identifier.of(MOD_ID, "celestial_body");

    private static boolean registered = false;

    private static Vec3d currentPos;

    private static Vec3d targetPos;

    private static Quaternionf currentRot;
    private static Quaternionf targetRot;

    private static int lastUniverseHash = 0;

    public static void initialize() {

    }


    private static void updateShipPosition() {
        if (MinecraftClient.getInstance().player == null) {
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
        }

        float time = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration() * 5;

        currentPos = InterpolationUtils.interpolateLinear(currentPos,targetPos,time * 0.05f);
        currentRot =  new Quaternionf(currentRot).slerp(targetRot, time * 0.05f);
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
        updateShipPosition();

        Vector4f rot = new Vector4f(currentRot.x,currentRot.y,currentRot.z,currentRot.w);

        List<CelestialBody> bodiesToRender = DragonExperiments.universe.getAllBodies();

        bodiesToRender.sort((c1,c2) -> {
            double dist1 = c1.getCurrentPosition().subtract(currentPos).lengthSquared();
            double dist2 = c2.getCurrentPosition().subtract(currentPos).lengthSquared();
            if (dist1 == dist2) {
                return 0;
            }else if (dist1 > dist2) {
                return -1;
            }
            return 1;
        });

        if (DragonExperiments.universe != null && DragonExperiments.universe.getHash() != lastUniverseHash) {
            PlanetTextureLoader.loadTextures(DragonExperiments.universe.getAllTextures(),4096,2048,true);
            lastUniverseHash = DragonExperiments.universe.getHash();
        }



        ShipComponent component = ModComponents.SHIP_COMPONENT.get( MinecraftClient.getInstance().player.getWorld());
        PlanetsUniformContainer container = new PlanetsUniformContainer(100);
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        if (pipeline == null) {
            return;
        }
        pipeline.setVector("ShipOrigin",component.getShipOrigin().toVector3f());
        pipeline.setVector("ShipPos",currentPos.toVector3f());
        pipeline.setVector("ShipRotation",rot);
        pipeline.setVector("LightPosition",new Vector3f(0,0,0));

        for (int i = 0; i < bodiesToRender.size();i++) {
            addPlanetRenderingData(bodiesToRender.get(i),container,i);
        }
        pipeline.setInt("PlanetCount",bodiesToRender.size());

        pipeline.setInts("UseTextures", container.useTexture);
        pipeline.setInts("UseUpperLayerTextures", container.useUpperLayer);
        pipeline.setVectors("PlanetPositions", container.planetPositions);
        pipeline.setFloats("PlanetSizes", container.planetSizes);

        pipeline.setInts("AtmosphereTypes", container.atmosphereTypes);
        pipeline.setFloats("AtmosphereSizes", container.atmosphereSizes);
        pipeline.setVectors("AtmosphereRayleighCoefficients", container.atmosphereRayleighCoefficients);
        pipeline.setFloats("AtmosphereRayleighScaleHeights", container.atmosphereRayleighScaleHeight);
        pipeline.setFloats("AtmosphereMieCoefficients", container.atmosphereMieCoefficients );
        pipeline.setFloats("AtmosphereMieScaleHeights", container.atmosphereMieScaleHeight);
        pipeline.setFloats("AtmosphereBrightnesses", container.atmosphereBrightnesses);

        pipeline.setInts("PlanetTextures",container.planetTextureIds);
        pipeline.setInts("UpperLayerTextures",container.upperLayerTextureIds);

        ShaderProgram program = VeilRenderSystem.renderer().getShaderManager().getShader(CELESTIAL_BODY_SHADER);
        if (program == null) {
            return;
        }
        program.setSampler("PlanetTexturesSampler",PlanetTextureLoader.getTextureArrayId());

        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline,true);
    }

    private static void addPlanetRenderingData(CelestialBody celestialBody,PlanetsUniformContainer container,int id) {
        container.planetTextureIds[id] = PlanetTextureLoader.getLayerIndex(celestialBody.getTextureName());
        container.upperLayerTextureIds[id] = PlanetTextureLoader.getLayerIndex(celestialBody.getUpperLayerTextureName());
        if (container.planetTextureIds[id] == -1) {
            container.useTexture[id] = 0;
            container.planetTextureIds[id] = 0;
        }
        if (container.upperLayerTextureIds[id] == -1) {
            container.useUpperLayer[id] = 0;
            container.upperLayerTextureIds[id] = 0;
        }
        Vec3d newPos = InterpolationUtils.interpolateLinear(celestialBody.getLastRenderedPosition(), celestialBody.getCurrentPosition(), 0.01f);
        celestialBody.setLastRenderedPosition(newPos);
        container.planetPositions[id] = newPos.toVector3f();
        container.planetSizes[id] = celestialBody.getRadius();

        if (celestialBody instanceof Planet planet) {
            container.atmosphereTypes[id] = 1;
            container.atmosphereSizes[id] = planet.getAtmosphereSize();
            container.atmosphereBrightnesses[id] = planet.getAtmosphereBrightness();
            container.atmosphereRayleighCoefficients[id] = planet.getAtmosphereRayleighCoeffiecents().toVector3f().mul(1e-4f);
            container.atmosphereRayleighScaleHeight[id] = planet.getAtmosphereRayleighScaleHeight();
            container.atmosphereMieCoefficients[id] = planet.getAtmosphereMieCoeffiecent() * 1e-4f;
            container.atmosphereMieScaleHeight[id] = planet.getAtmosphereMieScaleHeight();
        }
        if (celestialBody instanceof Star star) {
            container.atmosphereTypes[id] = 2;
            container.atmosphereSizes[id] = star.getAtmosphereSize();
            container.atmosphereBrightnesses[id] = star.getAtmosphereBrightness();
            container.atmosphereRayleighCoefficients[id] = star.getColor().toVector3f();
            container.atmosphereRayleighScaleHeight[id] = star.getAtmosphereFalloff();
        }
    }
}