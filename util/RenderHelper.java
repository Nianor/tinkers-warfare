package com.nianor.tinkersarsenal.util;

import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.client.render.EntityArsenalProjectileRender;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

public class RenderHelper {
    public static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, new IRenderFactory<EntityArsenalProjectile>() {
            @Override
            public Render<? super EntityArsenalProjectile> createRenderFor(RenderManager manager) {
                if (isTest) {TinkersArsenal.logger.info("Render Helper Registering Entity");}
                return new EntityArsenalProjectileRender(manager);
            }
        });
    }
}
