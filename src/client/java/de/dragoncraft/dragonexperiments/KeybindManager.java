package de.dragoncraft.dragonexperiments;

import de.dragoncraft.dragonexperiments.entity.ModEntities;
import de.dragoncraft.dragonexperiments.entity.SeatEntity;
import de.dragoncraft.dragonexperiments.networking.ship.SeatInputPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {

    private static final KeyBinding rollKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.dragon_experiments.ship_roll",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.dragon_experiments.space"
    ));

    private static final KeyBinding upKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.dragon_experiments.ship_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_CAPS_LOCK,
            "category.dragon_experiments.space"
    ));

    // Store previous mouse position
    private static double lastMouseX = 0;
    private static double lastMouseY = 0;

    private static boolean shipInputDisabled = true;

    public static void initialize() {
        registerShipInputListener();
    }

    public static void sendSeatInputToServer(PlayerEntity player, String action, Vector3f mouseMove,Vector3f steering) {
        SeatInputPayload payload = new SeatInputPayload(player.getUuid(), action,mouseMove,steering);
        ClientPlayNetworking.send(payload);
    }
    public static void registerShipInputListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (
                    client.isPaused()
                    || !client.isWindowFocused()
                    || client.inGameHud.getChatHud().isChatFocused()
                    || !client.mouse.isCursorLocked()
            ) {
                shipInputDisabled = true;
                return;
            } else {
                if (shipInputDisabled) {
                    lastMouseX = client.mouse.getX();
                    lastMouseY = client.mouse.getY();
                    shipInputDisabled = false;
                }
            }

            if (client.player != null && client.player.hasVehicle() && client.player.getVehicle() instanceof SeatEntity) {
                Vector3f mouse = new Vector3f(0,0,0);
                Vector3f steering = getSteering(client);
                if (client.mouse.getX() != lastMouseX || client.mouse.getY() != lastMouseY) {
                    if (rollKey.isPressed()) {
                        mouse = new Vector3f((float) -(client.mouse.getX() - lastMouseX),0, (float) (client.mouse.getY() - lastMouseY));
                    } else {
                        mouse = new Vector3f(0,(float) -(client.mouse.getX() - lastMouseX), (float) (client.mouse.getY() - lastMouseY));
                    }
                }
                sendSeatInputToServer(client.player, "move",mouse,steering);
                lastMouseX = client.mouse.getX();
                lastMouseY = client.mouse.getY();
            }
        });
    }

    @NotNull
    private static Vector3f getSteering(MinecraftClient client) {
        Vector3f steering = new Vector3f(0,0,0);
        if (client.options.forwardKey.isPressed()) steering.add(-1,0,0);
        else if (client.options.backKey.isPressed()) steering.add(1,0,0);

        if (client.options.leftKey.isPressed()) steering.add(0,0,1);
        else if (client.options.rightKey.isPressed()) steering.add(0,0,-1);

        if (client.options.sneakKey.isPressed()) steering.add(0,-1,0);
        else if (upKey.isPressed() && client.player != null ) {
            steering.add(0,1,0);
        }

        else if (client.options.jumpKey.isPressed()) steering.add(0,-100,0);
        return steering;
    }
}

