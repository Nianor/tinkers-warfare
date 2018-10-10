package com.nianor.tinkersarsenal;

import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.nianor.tinkersarsenal.client.render.EntityArsenalProjectileRender;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
//MOST OF THIS CODE COMES FROM DIREWOLF20
public class ModEntities {
    public static int id = 1;

    public static void registerEntities() {
        int i = id;
        registerEntity("entity_arsenal_projectile", EntityArsenalProjectile.class, i, 512, 90, true);
    }

    private static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int range, int freq, boolean velUpdate) {
        EntityRegistry.registerModEntity(new ResourceLocation (TinkersArsenal.MODID + ":"+name), entityClass, "name", id, TinkersArsenal.INSTANCE, range, freq, velUpdate);
        TinkersArsenal.logger.info("Entity Registered: {}", EntityRegistry.getEntry(entityClass));
    }

    public static void initModels() {
        //RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, new EntityArsenalProjectileRender(Minecraft.getMinecraft().getRenderManager()));
    }
}
