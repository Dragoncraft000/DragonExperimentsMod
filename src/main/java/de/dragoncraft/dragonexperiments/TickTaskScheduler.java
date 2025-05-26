// TickTaskScheduler.java
package de.dragoncraft.dragonexperiments;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class TickTaskScheduler {
    private static final Map<Long, List<Runnable>> tickTasks = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long tick = server.getOverworld().getTime();
            List<Runnable> tasks = tickTasks.remove(tick);
            if (tasks != null) {
                for (Runnable task : tasks) {
                    task.run();
                }
            }
        });
    }

    public static void schedule(long delayTicks, Runnable task, MinecraftServer server) {
        long tick = server.getOverworld().getTime() + delayTicks;
        tickTasks.computeIfAbsent(tick, t -> new ArrayList<>()).add(task);
    }
}
