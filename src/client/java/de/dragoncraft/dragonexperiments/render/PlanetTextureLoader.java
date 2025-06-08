package de.dragoncraft.dragonexperiments.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dragoncraft.dragonexperiments.DragonExperiments;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanetTextureLoader {
    @Getter
    private static int textureArrayId = -1;

    public static boolean texturesLoaded = false;

    private static final Map<Identifier, Integer> textureIndexMap = new TreeMap<>();

    public static NativeImage resizeNativeImage(NativeImage original, int targetWidth, int targetHeight) {
        NativeImage resized = new NativeImage(NativeImage.Format.RGBA, targetWidth, targetHeight, false);

        int srcWidth = original.getWidth();
        int srcHeight = original.getHeight();

        for (int y = 0; y < targetHeight; y++) {
            int srcY = y * srcHeight / targetHeight;
            for (int x = 0; x < targetWidth; x++) {
                int srcX = x * srcWidth / targetWidth;
                int color = original.getColor(srcX, srcY);
                resized.setColor(x, y, color);
            }
        }

        return resized;
    }

    public static void loadTextures(List<Identifier> imagePaths,int width,int height,boolean forceReload) {
        if (textureIndexMap.keySet().containsAll(imagePaths) && !forceReload) {
            DragonExperiments.LOGGER.info("Texture Paths are up to date, nothing to reload");
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            List<NativeImage> images = new ArrayList<>();
            List<Identifier> loadedPaths = new ArrayList<>();
            for (Identifier id : imagePaths) {
                try {
                    NativeImage image = NativeImage.read(MinecraftClient.getInstance().getResourceManager().open(id));
                    if (image.getWidth() != width || image.getHeight() != height) {
                        DragonExperiments.LOGGER.info("Image size mismatch for " + id + ": expected " + width + "x" + height +
                                ", got " + image.getWidth() + "x" + image.getHeight());
                        DragonExperiments.LOGGER.info("Trying to resize");

                        try (NativeImage resized = resizeNativeImage(image, width, height)) {
                            DragonExperiments.LOGGER.error("Resizing not yet implemented");
                            continue;
                        } catch (Exception e) {
                            DragonExperiments.LOGGER.error("Failed to resize image: " + id);
                            DragonExperiments.LOGGER.error(e.getMessage());
                            continue;
                        }
                    }
                    images.add(image);
                    loadedPaths.add(id);
                    DragonExperiments.LOGGER.info("Loaded " + id.toString());
                } catch (IOException e) {
                    DragonExperiments.LOGGER.error("Failed to load image: " + id);
                    DragonExperiments.LOGGER.error(e.getMessage());
                }
            }
            texturesLoaded = false;
            RenderSystem.recordRenderCall(() -> uploadTextureArray(images,loadedPaths,width,height));
            texturesLoaded = true;
        });
        executor.shutdown();
    }

    public static void uploadTextureArray(List<NativeImage> images,List<Identifier> loadedPaths,int width,int height) {
        textureIndexMap.clear();
        int layerCount = images.size();
        int textureId = GL45.glGenTextures();

        GL45.glBindTexture(GL45.GL_TEXTURE_2D_ARRAY, textureId);

        GL45.glTexStorage3D(
                GL45.GL_TEXTURE_2D_ARRAY,
                1,
                GL45.GL_RGBA8,
                width,
                height,
                layerCount
        );

        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_LINEAR);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_LINEAR);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_WRAP_S, GL45.GL_REPEAT);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_WRAP_T, GL45.GL_REPEAT);

        for (int i = 0;i < images.size();i++) {

            NativeImage image = images.get(i);
            Identifier id = loadedPaths.get(i);

            ByteBuffer pixelBuffer = MemoryUtil.memAlloc(width * height * 4);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = image.getColor(x, y);
                    byte g = (byte) ((color >> 8) & 0xFF);
                    byte b = (byte) ((color >> 16) & 0xFF);
                    byte r = (byte) (color & 0xFF);
                    byte a = (byte) ((color >> 24) & 0xFF);
                    pixelBuffer.put(r).put(g).put(b).put(a);
                }
            }
            pixelBuffer.flip();
            GL45.glTexSubImage3D(
                    GL45.GL_TEXTURE_2D_ARRAY,
                    0,
                    0, 0, i,
                    width, height, 1,
                    GL45.GL_RGBA,
                    GL45.GL_UNSIGNED_BYTE,
                    pixelBuffer
            );
            textureIndexMap.put(id,i);
            DragonExperiments.LOGGER.info(i + " -> " + id.toString());
            MemoryUtil.memFree(pixelBuffer);
        }

        GL45.glBindTexture(GL45.GL_TEXTURE_2D_ARRAY, 0);
        textureArrayId = textureId;
    }

    public static int getLayerIndex(Identifier id) {
        if (id == null) {
            return 0;
        }
        return textureIndexMap.getOrDefault(id, 0);
    }
}
