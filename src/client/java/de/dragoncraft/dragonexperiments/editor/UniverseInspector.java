package de.dragoncraft.dragonexperiments.editor;


import de.dragoncraft.dragonexperiments.DragonExperiments;
import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.text.Text;

public class UniverseInspector extends SingleWindowInspector {

    public static final Text TITLE = Text.translatable("editor.dragon_experiments.universe_inspector.title");

    private ImBoolean gravity = new ImBoolean(true);


    @Override
    protected void renderComponents() {

        if (DragonExperiments.universe == null) {
            ImGui.text("Missing Universe data");
            return;
        }

        if (ImGui.beginTabBar("Examples")) {
            for (Tab value : Tab.values()) {
                if (ImGui.beginTabItem(value.name)) {
                    if (value == Tab.PLANETS) {
                        ImGui.checkbox("Use Gravity", gravity);
                        ImGui.value("Use Entity Tessellation", 10);
                        ImGui.sameLine();

                    }
                }
                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    private enum Tab {
        PLANETS("Planets");

        private final String name;

        Tab(String name) {
            this.name = name;
        }
    }
}