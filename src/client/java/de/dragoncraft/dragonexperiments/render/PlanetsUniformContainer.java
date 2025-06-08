package de.dragoncraft.dragonexperiments.render;

import org.joml.Vector3f;

import java.util.Arrays;

public class PlanetsUniformContainer {


    public int[] planetTextureIds;
    public int[] upperLayerTextureIds;
    public int[] useTexture;
    public int[] useUpperLayer;
    public Vector3f[] planetPositions;
    public float[] planetSizes;
    public float[] planetRotationSpeeds;

    public int[] atmosphereTypes;
    public float[] atmosphereSizes;
    public Vector3f[] atmosphereRayleighCoefficients;
    public float[] atmosphereRayleighScaleHeight;
    public float[] atmosphereMieCoefficients;
    public float[] atmosphereMieScaleHeight;
    public float[] atmosphereBrightnesses;

    public PlanetsUniformContainer(int amount) {
        planetTextureIds = new int[amount];
        Arrays.fill(planetTextureIds,0);
        upperLayerTextureIds = new int[amount];
        Arrays.fill(upperLayerTextureIds,0);
        useTexture = new int[amount];
        Arrays.fill(useTexture,1);
        useUpperLayer = new int[amount];
        Arrays.fill(useUpperLayer,1);
        planetPositions = new Vector3f[amount];
        Arrays.fill(planetPositions,new Vector3f(0,0,0));
        planetSizes = new float[amount];
        Arrays.fill(planetSizes,0);
        planetSizes = new float[amount];
        Arrays.fill(planetSizes,0);
        planetRotationSpeeds = new float[amount];
        Arrays.fill(planetRotationSpeeds,10000);

        atmosphereTypes = new int[amount];
        Arrays.fill(atmosphereTypes,0);
        atmosphereSizes = new float[amount];
        Arrays.fill(atmosphereSizes,0);
        atmosphereRayleighCoefficients = new Vector3f[amount];
        Arrays.fill(atmosphereRayleighCoefficients,new Vector3f(0,0,0));
        atmosphereRayleighScaleHeight = new float[amount];
        Arrays.fill(atmosphereRayleighScaleHeight,0);
        atmosphereMieCoefficients = new float[amount];
        Arrays.fill(atmosphereMieCoefficients,0);
        atmosphereMieScaleHeight = new float[amount];
        Arrays.fill(atmosphereMieScaleHeight,0);
        atmosphereBrightnesses = new float[amount];
        Arrays.fill(atmosphereBrightnesses,0);


    }

}
