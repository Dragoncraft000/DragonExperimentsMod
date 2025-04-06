package de.dragoncraft.dragonexperiments.render.entity;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.entity.SeatEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class ShipSeatEntityRender extends EntityRenderer<SeatEntity> {
    public ShipSeatEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SeatEntity entity) {
        return Identifier.of(DragonExperiments.MOD_ID, "textures/entity/seat.png"); // Can be a transparent 1x1 PNG
    }

    @Override
    public boolean shouldRender(SeatEntity entity, Frustum frustum, double x, double y, double z) {
        return false;
    }
}
