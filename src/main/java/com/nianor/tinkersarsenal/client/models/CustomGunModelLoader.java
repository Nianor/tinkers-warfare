package com.nianor.tinkersarsenal.client.models;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import static com.nianor.tinkersarsenal.TinkersArsenal.MODID;

public class CustomGunModelLoader extends RenderEntityItem {


    private RenderGun itemRenderer;

    public CustomGunModelLoader(RenderManager renderManager, RenderItem renderItem)
    {
        super(renderManager, renderItem);
        this.itemRenderer = new RenderGun();
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        ItemStack stack = entity.getItem();

        this.bindTexture(new ResourceLocation(MODID, "temp"));

        if(stack.getItem() instanceof BaseGunItem /*&& ((BaseGunItem)stack.getItem()).GetType().model != null*/)
        {
            System.out.println("Model Loader Loaded");

            Tessellator tessellator = ClientUtils.tes();
            BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + 0.25D, z);
            float xMinBody = -1.5F;//-3/5;
            float xMaxBody = 1.5F;//3/5;
            float yMinBody = -1.5F;//-3/5;
            float yMaxBody = 1.5F;//3/5;
            float zMinBody = -1.5F;//-3/5;
            float zMaxBody = 1.5F;//3/5;

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            //tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            //tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();

            GlStateManager.rotate(entity.ticksExisted + partialTicks, 0F, 1F, 0F);

            itemRenderer.renderGun(stack, 1, 1, partialTicks);
            GlStateManager.popMatrix();
        }
        else
        {
            super.doRender(entity, x, y, z, partialTicks, partialTicks);
        }
    }

    public static class Factory implements IRenderFactory
    {
        @Override
        public Render createRenderFor(RenderManager manager)
        {
            return new CustomGunModelLoader(manager, Minecraft.getMinecraft().getRenderItem());
        }
    }
}