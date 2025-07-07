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
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.List;

import static de.dragoncraft.dragonexperiments.DragonExperiments.MOD_ID;

public class ShaderManager {

    private static final Identifier SPACE_WORLD = Identifier.of(MOD_ID, "space");
    private static final Identifier SPACE_RENDER_PIPELINE = Identifier.of(MOD_ID, "space_shader");
    private static final Identifier CELESTIAL_BODY_SHADER = Identifier.of(MOD_ID, "celestial_body");

    private static Vec3d currentPos;

    private static Vec3d targetPos;

    private static Quaternionf currentRot;
    private static Quaternionf targetRot;

    @Getter
    private static int lastUniverseHash = 0;

    @Getter
    private static float planetSkyLight = 0;

    @Getter
    private static CelestialBody currentCelestialBody = null;

    private static double interpolatedWorldTime = -1;


    public static void updateCurrentCelestialBody() {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        updateCurrentCelestialBody(MinecraftClient.getInstance().player.clientWorld);
    }
    public static void updateCurrentCelestialBody(World world) {
        Identifier key = world.getRegistryKey().getValue();
        currentCelestialBody = DragonExperiments.universe.getDimensionLinkedBody(key);
    }


    public static void initialize() {

    }

    public static void renderSpaceView() {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        if (VeilRenderSystem.renderer() == null) {
            return;
        }
        if (!MinecraftClient.getInstance().player.getEntityWorld().getRegistryKey().getValue().equals(SPACE_WORLD)) {
            return;
        }
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        updateShipPosition();
        setShipSettings(currentPos,currentRot,pipeline);
        writePlanetUniforms(currentPos);
        renderSpaceShader(currentPos);
    }
    public static void renderCurrentPlanetView() {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        if (VeilRenderSystem.renderer() == null) {
            return;
        }
        if (currentCelestialBody == null) {
            return;
        }

        updatePlanetWorldLight(currentCelestialBody);
        renderPlanetView(currentCelestialBody);
    }
    private static double getPlanetRotationTime(CelestialBody body) {
        double targetTime = DragonExperiments.universe.getPhysicalWorld().getTime();
        float step = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration() * 5;
        interpolatedWorldTime = InterpolationUtils.lerp(interpolatedWorldTime,targetTime,step * 0.05f);
        double time = interpolatedWorldTime;
        time /= 24000;
        time /= body.getRotationTime();
        return time * 360 + 180;
    }
    private static Quaternionf getPlanetViewRotation(CelestialBody body) {
        double time = getPlanetRotationTime(body);
        Quaternionf rotation = new Quaternionf(new AxisAngle4d(Math.PI * 0.5,new Vector3f(1,0,0)));
        rotation.rotateAxis((float) Math.toRadians(time),new Vector3f(0,0,1));
        return rotation;
    }

    public static void renderPlanetView(CelestialBody planet) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        if (VeilRenderSystem.renderer() == null) {
            return;
        }
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        if (pipeline == null) {
            return;
        }
        double time = getPlanetRotationTime(planet);
        Quaternionf rotation = getPlanetViewRotation(planet);
        Vector3d pos = new Vector3d(0, 0, planet.getRadius() + 0.1);
        pos.rotateY(-(float) Math.toRadians(time));
        Vec3d origin = MinecraftClient.getInstance().player.getPos();
        origin = new Vec3d(origin.x,1,origin.z);
        Vec3d newPos = InterpolationUtils.interpolateLinear(planet.getLastRenderedPosition(), planet.getCurrentPosition(), 0.01f);
        setShipSettings(newPos.add(new Vec3d(pos.x,pos.y,pos.z)),rotation ,origin,pipeline);
        pipeline.getOrCreateUniform("HidePlanetsIfNear").setInt(MinecraftClient.getInstance().options.getClampedViewDistance() * 16 - 16 * -100);
        renderSpaceShader((planet.getLastRenderedPosition().add(new Vec3d(pos.x,pos.y,pos.z))));
    }

    public static void updatePlanetWorldLight(CelestialBody planet) {
        Vec3d lightDir = planet.getLastRenderedPosition().normalize().multiply(-1);
        Vector3f normal = new Vector3f(0,1,0).rotate(getPlanetViewRotation(planet));
        planetSkyLight = MathHelper.clamp(normal.dot(lightDir.toVector3f()) +0.2f,-0.2f,1);
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

        currentPos = InterpolationUtils.interpolateLinear(currentPos,targetPos,Math.min(time * 0.05f,1));
        if (Double.isNaN(currentPos.x) || Double.isNaN(currentPos.y) || Double.isNaN(currentPos.z)) {
            currentPos = targetPos.add(0,0,0);
        }
        currentRot =  new Quaternionf(currentRot).slerp(targetRot, time * 0.05f);

        CelestialBody referenceFrameBody = DragonExperiments.universe.getCelestialBody(component.getReferenceFrame());
        if (referenceFrameBody != null && referenceFrameBody.isHasCollision()) {
            Vec3d planetPos = InterpolationUtils.interpolateLinear(referenceFrameBody.getLastRenderedPosition(), referenceFrameBody.getCurrentPosition(), 0.01f);
            double distanceToReference = currentPos.distanceTo(planetPos);
            if (distanceToReference < referenceFrameBody.getRadius() + 5) {
                Vec3d relativePos = currentPos.subtract(planetPos).normalize().multiply(referenceFrameBody.getRadius()+ 5 );
                currentPos = planetPos.add(relativePos) ;
            }
        }
    }

    public static boolean updateTextures() {
        if (DragonExperiments.universe != null && DragonExperiments.universe.getHash() != lastUniverseHash) {
            PlanetTextureLoader.loadTextures(DragonExperiments.universe.getAllTextures(),4096,2048,false);
            lastUniverseHash = DragonExperiments.universe.getHash();
        }

        return PlanetTextureLoader.texturesLoaded;
    }

    private static void setShipSettings(Vec3d shipPosition,Quaternionf shipRotation,PostPipeline pipeline) {
        Vector4f rot = new Vector4f(shipRotation.x,shipRotation.y,shipRotation.z,shipRotation.w);
        ShipComponent component = DragonExperiments.universe.getPhysicalWorld().getComponent(ModComponents.SHIP_COMPONENT);
        if (pipeline == null) {
            return;
        }
        pipeline.getOrCreateUniform("ShipOrigin").setVector(component.getShipOrigin().toVector3f());
        pipeline.getOrCreateUniform("ShipPos").setVector(shipPosition.toVector3f());
        pipeline.getOrCreateUniform("ShipRotation").setVector(rot);
        pipeline.getOrCreateUniform("LightPosition").setVector(new Vector3f(0,0,0));
    }
    private static void setShipSettings(Vec3d shipPosition,Quaternionf shipRotation,Vec3d shipOrigin,PostPipeline pipeline) {
        Vector4f rot = new Vector4f(shipRotation.x,shipRotation.y,shipRotation.z,shipRotation.w);
        if (pipeline == null) {
            return;
        }
        pipeline.getOrCreateUniform("ShipOrigin").setVector(shipOrigin.toVector3f());
        pipeline.getOrCreateUniform("ShipPos").setVector(shipPosition.toVector3f());
        pipeline.getOrCreateUniform("ShipRotation").setVector(rot);
        pipeline.getOrCreateUniform("LightPosition").setVector(new Vector3f(0,0,0));
    }

    private static void setPlanetSettings(List<CelestialBody> bodiesToRender) {
        PlanetsUniformContainer container = new PlanetsUniformContainer(100);
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        if (pipeline == null) {
            return;
        }

        for (int i = 0; i < bodiesToRender.size();i++) {
            addPlanetRenderingData(bodiesToRender.get(i),container,i);
        }
        pipeline.getOrCreateUniform("PlanetCount").setInt(bodiesToRender.size());


        pipeline.getOrCreateUniform("UseTextures").setInts(container.useTexture);
        pipeline.getOrCreateUniform("UseUpperLayerTextures").setInts(container.useUpperLayer);
        pipeline.getOrCreateUniform("PlanetPositions").setVectors(container.planetPositions);
        pipeline.getOrCreateUniform("PlanetSizes").setFloats(container.planetSizes);
        pipeline.getOrCreateUniform("PlanetRotationSpeeds").setFloats(container.planetRotationSpeeds);

        pipeline.getOrCreateUniform("AtmosphereTypes").setInts(container.atmosphereTypes);
        pipeline.getOrCreateUniform("AtmosphereSizes").setFloats(container.atmosphereSizes);
        pipeline.getOrCreateUniform("AtmosphereRayleighCoefficients").setVectors(container.atmosphereRayleighCoefficients);
        pipeline.getOrCreateUniform("AtmosphereRayleighScaleHeights").setFloats(container.atmosphereRayleighScaleHeight);
        pipeline.getOrCreateUniform("AtmosphereMieCoefficients").setFloats(container.atmosphereMieCoefficients );
        pipeline.getOrCreateUniform("AtmosphereMieScaleHeights").setFloats(container.atmosphereMieScaleHeight);
        pipeline.getOrCreateUniform("AtmosphereBrightnesses").setFloats(container.atmosphereBrightnesses);

        pipeline.getOrCreateUniform("PlanetTextures").setInts(container.planetTextureIds);
        pipeline.getOrCreateUniform("UpperLayerTextures").setInts(container.upperLayerTextureIds);

        ShaderProgram program = VeilRenderSystem.renderer().getShaderManager().getShader(CELESTIAL_BODY_SHADER);
        if (program == null) {
            return;
        }
        program.setSampler("PlanetTexturesSampler",PlanetTextureLoader.getTextureArrayId());
    }

    public static void writePlanetUniforms(Vec3d shipPosition) {
        List<CelestialBody> bodiesToRender = DragonExperiments.universe.getAllBodies();

        bodiesToRender.sort((c1,c2) -> {
            double dist1 = c1.getCurrentPosition().subtract(shipPosition).lengthSquared();
            double dist2 = c2.getCurrentPosition().subtract(shipPosition).lengthSquared();
            if (dist1 == dist2) {
                return 0;
            }else if (dist1 > dist2) {
                return -1;
            }
            return 1;
        });

        if (!updateTextures()) {return;}
        setPlanetSettings(bodiesToRender);
    }


    public static void renderSpaceShader(Vec3d shipPosition) {
        writePlanetUniforms(shipPosition);
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(SPACE_RENDER_PIPELINE);
        if (pipeline == null) {
            return;
        }
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
        container.planetRotationSpeeds[id] = (float) celestialBody.getRotationTime();

        if (celestialBody instanceof Planet planet) {
            container.atmosphereTypes[id] = 1;
            container.atmosphereSizes[id] = planet.getAtmosphereSize();
            container.atmosphereBrightnesses[id] = planet.getAtmosphereBrightness();
            container.atmosphereRayleighCoefficients[id] = planet.getAtmosphereRayleighCoeffiecents().toVector3f().mul(1e-4f);
            container.atmosphereRayleighScaleHeight[id] = planet.getAtmosphereRayleighScaleHeight();
            container.atmosphereMieCoefficients[id] = planet.getAtmosphereMieCoefficient() * 1e-4f;
            container.atmosphereMieScaleHeight[id] = planet.getAtmosphereMieScaleHeight();
            if (planet.equals(currentCelestialBody)) {
                container.atmosphereMieScaleHeight[id] = planet.getOnWorldMieScaleHeightOverride();
                container.atmosphereMieCoefficients[id] = planet.getOnWorldMieCoefficientOverride()* 1e-4f ;
                container.atmosphereBrightnesses[id] = planet.getOnWorldAtmosphereBrightnessOverride();
            }
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