package de.dragoncraft.dragonexperiments.solarsystem;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import foundry.veil.api.client.render.post.PostPipeline;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Planet extends CelestialBody {

    protected float atmosphereSize = 50;
    protected Vec3d atmosphereRayleighCoeffiecents = new Vec3d(55,130,224);
    protected float atmosphereMieCoeffiecent = 105f;

    protected float atmosphereRayleighScaleHeight = 5;
    protected float atmosphereMieScaleHeight = 1.2f;

    protected float atmosphereBrightness = 2f;

    public Planet(String bodyName) {
        super(bodyName);
    }

    public Planet setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight, float mieCoeffiecent, float mieScaleHeight,float atmosphereBrightness, float atmosphereSize) {
        this.atmosphereRayleighCoeffiecents = rayleighCoeffiecents;
        this.atmosphereRayleighScaleHeight = rayleighScaleHeight;
        this.atmosphereMieCoeffiecent = mieCoeffiecent;
        this.atmosphereMieScaleHeight = mieScaleHeight;
        this.atmosphereSize = atmosphereSize;
        this.atmosphereBrightness = atmosphereBrightness;
        return this;
    }
    public Planet setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight, float mieCoeffiecent, float mieScaleHeight,float atmosphereBrightness) {
        if (atmosphereSize == 0) {
            this.atmosphereSize = 50;
        }
        return setAtmosphere(rayleighCoeffiecents,rayleighScaleHeight,mieCoeffiecent,mieScaleHeight,atmosphereBrightness,atmosphereSize);
    }

    public Planet setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight) {
        return setAtmosphere(rayleighCoeffiecents,rayleighScaleHeight,1e-10f,1,atmosphereBrightness);
    }
    public Planet setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight,float atmosphereBrightness) {
        return setAtmosphere(rayleighCoeffiecents,rayleighScaleHeight,1e-10f,1,atmosphereBrightness,atmosphereSize);
    }
    public Planet setAtmosphere(Vec3d rayleighCoeffiecents, float rayleighScaleHeight,float atmosphereBrightness,float atmosphereSize) {
        return setAtmosphere(rayleighCoeffiecents,rayleighScaleHeight,1e-10f,1,atmosphereBrightness,atmosphereSize);
    }

    public Planet disableAtmosphere() {
        return setAtmosphere(new Vec3d(0,0,0),0,0,0,0);
    }

    @Override
    public Identifier getBodyPipeline() {
        return Identifier.of(DragonExperiments.MOD_ID, "planet");
    }

    @Override
    public boolean renderBody(PostPipeline pipeline, int textureId, int upperLayerTexture, float subtick) {
        if (!super.renderBody(pipeline,textureId,upperLayerTexture,subtick)) {
            return false;
        }
        pipeline.setFloat("AtmosphereSize", atmosphereSize);
        pipeline.setVector("AtmosphereRayleighCoeffiecents",atmosphereRayleighCoeffiecents.toVector3f().mul(1e-4f));
        pipeline.setFloat("AtmosphereMieCoeffiecent", atmosphereMieCoeffiecent * 1e-4f);
        pipeline.setFloat("AtmosphereBrightness", atmosphereBrightness);
        pipeline.setFloat("AtmosphereRayleighScaleHeight",atmosphereRayleighScaleHeight);
        pipeline.setFloat("AtmosphereMieScaleHeight",atmosphereMieScaleHeight);
        return true;
    }

}
