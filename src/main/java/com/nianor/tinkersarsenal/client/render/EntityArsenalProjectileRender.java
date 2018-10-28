package com.nianor.tinkersarsenal.client.render;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.client.models.ModelProjectile;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.sun.istack.internal.NotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

//RENDER CODE FROM IMMERSIVE ENGINEERING
import static com.nianor.tinkersarsenal.TinkersArsenal.*;

/*
@SideOnly(Side.CLIENT)
public class EntityArsenalProjectileRender extends Render<EntityArsenalProjectile        > {

    public EntityArsenalProjectileRender(RenderManager rendermanagerIn) {
        super(rendermanagerIn);
    }

    @Override
    public void doRender(EntityArsenalProjectile entityThrownSand, double x, double y, double z, float yaw, float tick) {
        System.out.println("RENDERING PROJECTILE");
        renderThrownSand(entityThrownSand, x, y, z, yaw, tick);
    }

    public void renderThrownSand(EntityArsenalProjectile entityThrownSand, double x, double y, double z, float yaw, float tick) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5D, z);
        GlStateManager.scale(1.0D, 1.0D, 1.0D);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.SAND.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityArsenalProjectile entityThrownSand) {
        return null;
    }

}
*/
public class EntityArsenalProjectileRender extends Render<EntityArsenalProjectile> {

    private RenderManager renderManager;

    public static final ResourceLocation TEXTURES = new ResourceLocation(TinkersArsenal.MODID + ":textures/models/entity_arsenal_projectile");

    public EntityArsenalProjectileRender(RenderManager renderManager) {
        super(renderManager);
        this.renderManager = renderManager;

        if(checkRender){TinkersArsenal.logger.info("Render Manager Loaded for EntityArsenalProjectile with renderManager "+renderManager);}
    }

    @Override
    public void doRender(EntityArsenalProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {

        System.out.println("RENDERING PROJECTILE");
        //if(entity.getSeed()==0) {
        //    return;
        //}

        //GlStateManager.pushMatrix();
        Tessellator tessellator = ClientUtils.tes();
        BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();
        this.bindEntityTexture(entity);
        GL11.glPushMatrix();
        System.out.println("\nEntity: "+entity+"\nMotion Vector: "+entity.motionVector+"\nRender Manager: "+renderManager);
        GL11.glTranslated(entity.posX+entity.motionX*partialTicks/entity.timeMultiplier-renderManager.viewerPosX, entity.posY+entity.motionY*partialTicks/entity.timeMultiplier-renderManager.viewerPosY, entity.posZ+entity.motionZ*partialTicks/entity.timeMultiplier-renderManager.viewerPosZ);

        if(checkRender) {
            logger.info("X Motion: {} Y Motion: {} Z Motion: {}", entity.motionX, entity.motionY, entity.motionZ);
            logger.info("X Position: {} Y Position: {} Z Position: {}",entity.posX,entity.posY,entity.posZ);
        }
        //GL11.glTranslated((entity.posX-entity.xInit)+(entity.posX-entity.prevPosX)*partialTicks, (entity.posY-entity.yInit)+(entity.posY-entity.prevPosY)*partialTicks, (entity.posZ-entity.zInit)+(entity.posZ-entity.prevPosZ)*partialTicks);
        //GL11.glTranslatef(entity.yaw, entity.pitch, entity.roll);
        GL11.glRotatef(entity.prevRotationYaw+(entity.rotationYaw-entity.prevRotationYaw)*partialTicks-90, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch+(entity.rotationPitch-entity.prevRotationPitch)*partialTicks, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(entity.previousRoll+(entity.rollSpeed)*partialTicks, 1.0F, 0.0F, 0.0F);



        float xMaxNeck = entity.neckLength/6;
        float xMaxTip = entity.tipLength/6;
        float xMaxBody = entity.bodyLength/6;
        float xMinBody = 0;
        float yMaxBody = (entity.caliber/2)/6;
        float yMinBody = -(entity.caliber/2)/6;
        float zMaxBody = (entity.caliber/2)/6;
        float zMinBody = -(entity.caliber/2)/6;

        //DRAW BODY:
        {
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            tessellator.draw();//GOOD

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            tessellator.draw();//GOOD

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();
        }
        //DRAW NECK:
        {
            //yMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMinBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //yMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMinBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //xFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();//GOOD
        }
        //DRAW TIP
        {
            //yMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMaxBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMinBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //yMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMinBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //xFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();//GOOD
        }





        //model.render(entity, entity.caliber, entity.neckLength, entity.tipLength, 1);
        //GlStateManager.translate(x, y, z);
        //GlStateManager.translate(entity.motionX*partialTicks, entity.motionY*partialTicks, entity.motionZ*partialTicks);
        //GlStateManager.translate((entity.posX-entity.xInit)+entity.motionX*partialTicks, (entity.posY-entity.yInit)+entity.motionY*partialTicks, (entity.posZ-entity.zInit)+entity.motionZ*partialTicks);
        //GlStateManager.enableRescaleNormal();


        //if(checkRender){
        //    logger.info("Renderer Default Coords:\nX: {} Y: {} Z: {}", x, y, z);
        //    logger.info("Renderer Replacement Coords:\nX: {} Y: {} Z: {}", entity.posX-entity.xInit, entity.posY-entity.yInit, entity.posZ-entity.zInit);
        //    logger.info("Renderer posInit:\nX: {} Y: {} Z: {}", entity.xInit, entity.yInit, entity.zInit);
        //    logger.info("Renderer posCurrent:\nX: {} Y: {} Z: {}", entity.posX, entity.posY, entity.posZ);
        //}




        //GlStateManager.disableCull();
        //GlStateManager.translate(entity.motionX, entity.motionY, entity.motionZ);
        //GlStateManager.rotate(entity.prevRotationYaw+(entity.rotationYaw-entity.prevRotationYaw)*partialTicks-90.0F, 0.0F, 1.0F, 0.0F);
        //GlStateManager.rotate(entity.prevRotationPitch+(entity.rotationPitch-entity.prevRotationPitch)*partialTicks, 0.0F, 0.0F, 1.0F);
        //GlStateManager.rotate(entity.previousRoll+(entity.rollSpeed/*entity.roll-entity.previousRoll*//*)*partialTicks, 1.0F, 0.0F, 0.0F);



        //GlStateManager.scale(.1f, .1f, .1f);


        //GlStateManager.popMatrix();

        GL11.glPopMatrix();
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityArsenalProjectile entity) {
        return TEXTURES;
    }

    public static class Factory implements IRenderFactory<EntityArsenalProjectile> {

        @Override
        public Render<? super EntityArsenalProjectile> createRenderFor(RenderManager manager) {
            return new EntityArsenalProjectileRender(manager);
        }
    }
}
/*GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(true);
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);

        //DRAW BODY:
        {
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(5 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(0 / 32d, 10 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(5 / 32d, 5 / 32d).endVertex();
            tessellator.draw();

            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, 0, 0).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, 0, 0).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMaxBody, 0).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, yMaxBody, 0).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();
//
            //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //worldrenderer.pos(xMaxBody, yMinBody, 0).tex(8 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, yMinBody, 0).tex(0 / 32d, 5 / 32d).endVertex();
            //worldrenderer.pos(xMinBody, 0, 0).tex(0 / 32d, 0 / 32d).endVertex();
            //worldrenderer.pos(xMaxBody, 0, 0).tex(8 / 32d, 0 / 32d).endVertex();
            //tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMaxBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMaxBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMaxBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();

            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxBody, yMinBody, zMinBody).tex(8 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMinBody).tex(0 / 32d, 5 / 32d).endVertex();
            worldrenderer.pos(xMinBody, yMinBody, zMaxBody).tex(0 / 32d, 0 / 32d).endVertex();
            worldrenderer.pos(xMaxBody, yMinBody, zMaxBody).tex(8 / 32d, 0 / 32d).endVertex();
            tessellator.draw();
        }
        //DRAW NECK:
        {
            //yMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //yMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMinBody*2)/3, (zMinBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxBody, (yMaxBody*2)/3, (zMinBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //xFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMinBody*2)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody*2)/3, (zMaxBody*2)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMaxBody*2)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody*2)/3, (zMinBody*2)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();
        }
        //DRAW TIP
        {
            //yMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //yMaxFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //zMinFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMinBody)/3, (zMinBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxNeck, (yMaxBody)/3, (zMinBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();

            //xFace
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMinBody)/3).tex(0/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMinBody)/3, (zMaxBody)/3).tex(8/32d, 5/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMaxBody)/3).tex(0/32d, 0/32d).endVertex();
            worldrenderer.pos(xMaxTip, (yMaxBody)/3, (zMinBody)/3).tex(8/32d, 0/32d).endVertex();
            tessellator.draw();
        }

        GlStateManager.enableLighting();


        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();

*/

//public class EntityArsenalProjectileRender<T extends EntityArsenalProjectile> extends RenderArrow<T> {
//
//    public static final ResourceLocation TEXTURES = new ResourceLocation(TinkersArsenal.MODID + ":textures/models/entity_arsenal_projectile");
//    protected ModelProjectile model;
//
//    public EntityArsenalProjectileRender(RenderManager renderManager) {
//        super(renderManager);
//        this.shadowSize= 0F;
//    }
//
//    /**
//     * Renders the desired {@code T} type Entity.
//     */
//    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks, float caliber) {
//
//        super.doRender(entity, x, y, z, entityYaw, partialTicks);
//        if (checkRender) {TinkersArsenal.logger.info("Renderer commencing rendering");}
//
//
//        RenderHelper.disableStandardItemLighting();
//
//        GlStateManager.pushMatrix();
//        {
//            GlStateManager.translate(x, y, z);
//            GlStateManager.rotate(-entityYaw, 0, 1, 0);
//            //GlStateManager.scale(caliber/12.3, caliber/12.3, caliber/12.3);
//
//            GlStateManager.popMatrix();
//
//            super.doRender(entity, x, y, z, entityYaw, partialTicks);
//        }
//        model.setDimensions(caliber);
//        //model.setRotateAngle(new ModelRenderer(), );
//        model.render(entity, (float)x, (float)y, (float)z, 1);
//
//        if (!entity.collided) {
//            entity.roll = (int)((float)entity.roll + (float)entity.rollSpeed * partialTicks);
//        }
//
//        float r = (float)entity.roll;
//    }
//
//    @Nullable
//    @Override
//    protected ResourceLocation getEntityTexture(T entity) {
//        //if (checkRender) {TinkersArsenal.logger.info("Renderer returning texture");}
//        return TEXTURES;
//    }
//}


/*
//MOST OF THIS CODE COMES FROM DIREWOLF20
public class EntityArsenalProjectileRender extends Render<EntityArsenalProjectile> {

    private static final ResourceLocation texture = new ResourceLocation (MODID + ".entity_arsenal_projectile");
    private Model model = new ResourceLocation("tinkersarsenal:models/supersonic_arrow");

    public EntityArsenalProjectileRender(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize= 0F;
    }

    private ResourceLocation getCustomTexture(EntityArsenalProjectile entity) {
// now you have access to your custom entity fields and methods, if any,
// and can base the texture to return upon those
        return texture;
    }
    @Override
    public void doRender(EntityArsenalProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();

// bind your texture:
        bindTexture(texture);

// do whatever transformations you need, then render

// typically you will at least want to translate for x/y/z position:
        GL11.glTranslated(x, y, z);

// if you are using a model, you can do so like this:
        model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

// note all the values are 0 except the final argument, which is scale
// vanilla Minecraft almost excusively uses 0.0625F, but you can change it to whatever works

        GL11.glPopMatrix();
    }
    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityArsenalProjectile entity) {
        return new ResourceLocation("tinkersarsenal:textures/models/entity_arsenal_projectile.png");
    }

    protected ResourceLocation getEntityModel(EntityArsenalProjectile entity) {
        return new ResourceLocation("tinkersarsenal:models/supersonic_arrow");
    }



}
*/
