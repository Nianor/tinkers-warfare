package com.nianor.tinkersarsenal.client.models;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.nianor.tinkersarsenal.ClientProxy;
import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import scala.xml.dtd.impl.Base;

import java.io.*;

import static com.nianor.tinkersarsenal.TinkersArsenal.MODID;
import static com.nianor.tinkersarsenal.TinkersArsenal.checkRender;
import static com.nianor.tinkersarsenal.client.models.CustomItemRenderType.EQUIPPED;

public class RenderGun {
    private static TextureManager renderEngine;

    public static final ResourceLocation TEXTURES = new ResourceLocation(MODID + ":textures/models/entity_arsenal_projectile");


    public static float smoothing;
    public static boolean bindTextures = true;


    public void RenderGun(CustomItemRenderType type, EnumHand hand, ItemStack item, float partialTicks/*, Object... data*/)
    {
        //Avoid any broken cases by returning
        /*if(!(item.getItem() instanceof BaseGunItem))
            return;

        GunType gunType = ((BaseGunItem)item.getItem()).GetType();
        if(gunType == null)
            return;

        ModelGun model = gunType.model;
        if(model == null)
            return;

        Render main hand gun
        GunAnimations animations =
                (type == CustomItemRenderType.ENTITY || type == CustomItemRenderType.INVENTORY)
                        ? new GunAnimations()
                        : FlansModClient.getGunAnimations((EntityLivingBase)data[1], hand);*/

        renderGun(type, item, hand, partialTicks/*, data*/);
    }

    //Render off-hand gun in 3rd person
    public void renderOffHandGun(EntityPlayer player, ItemStack offHandItemStack, float partialTicks)
    {
        System.out.println("Rendering third person");
        /*GunAnimations animations = FlansModClient.gunAnimationsLeft.get(player);
        if(animations == null)
        {
            animations = new GunAnimations();
            FlansModClient.gunAnimationsLeft.put(player, animations);
        }*/
        renderGun(CustomItemRenderType.INVENTORY, offHandItemStack, EnumHand.OFF_HAND, partialTicks, player);
    }

    private void renderGun(CustomItemRenderType type, ItemStack item, /*GunType gunType, GunAnimations animations,*/ EnumHand hand, float partialTicks, Object... data)
    {
        //The model scale
        float f = 1F / 16F;
        //ModelGun model = gunType.model;

        int flip = hand == EnumHand.OFF_HAND ? -1 : 1;

        GL11.glPushMatrix();
        {
            //Get the reload animation rotation
            float reloadRotate = 0F;

            //Setup transforms based on gun position
            switch(type)
            {
                case ENTITY :
                {
                    //EntityItem entity = (EntityItem)data[1];
                    //GL11.glRotatef(entity.getAge() + (entity.getAge() == 0 ? 0 : smoothing), 0F, 1F, 0F);
                    GL11.glTranslatef(-0.45F/* + model.itemFrameOffset.x*/, -0.05F/* + model.itemFrameOffset.y*/, 0/*+model.itemFrameOffset.z*/);
                    break;
                }
                case INVENTORY :
                {
                    GL11.glTranslatef(0, 0, 0/*model.itemFrameOffset.x, model.itemFrameOffset.y, model.itemFrameOffset.z*/);
                    break;
                }
                case EQUIPPED:
                {
                    if(hand == EnumHand.OFF_HAND)
                    {
                        GL11.glRotatef(-70F, 1F, 0F, 0F);
                        GL11.glRotatef(48F, 0F, 0F, 1F);
                        GL11.glRotatef(105F, 0F, 1F, 0F);
                        //GL11.glTranslatef(-0.1F, -0.22F, -0.15F);
                        GL11.glTranslatef(-0.1F, -0.22F, 1);
                    }
                    else
                    {
                        GL11.glRotatef(90F, 0F, 0F, 1F);
                        GL11.glRotatef(-90F, 1F, 0F, 0F);
                        GL11.glTranslatef(0.2F, 0.05F, 0F);
                        GL11.glScalef(1F, 1F, -1F);
                    }
                    GL11.glTranslatef(0, 0, 0/*model.thirdPersonOffset.x, model.thirdPersonOffset.y, model.thirdPersonOffset.z*/);

					/*
					if(animations.meleeAnimationProgress > 0 && animations.meleeAnimationProgress < gunType.meleePath.size())
					{
						Vector3f meleePos = gunType.meleePath.get(animations.meleeAnimationProgress);
						Vector3f nextMeleePos = animations.meleeAnimationProgress + 1 < gunType.meleePath.size() ? gunType.meleePath.get(animations.meleeAnimationProgress + 1) : new Vector3f();
						GL11.glTranslatef(meleePos.x + (nextMeleePos.x - meleePos.x) * smoothing, meleePos.y + (nextMeleePos.y - meleePos.y) * smoothing, meleePos.z + (nextMeleePos.z - meleePos.z) * smoothing);
					}
					*/
                    break;
                }
                case EQUIPPED_FIRST_PERSON:
                {
                    //IScope scope = gunType.getCurrentScope(item);
                    //if(FlansModClient.zoomProgress > 0.9F && scope.hasZoomOverlay())
                    //{
                    //    GL11.glPopMatrix();
                    //    return;
                    //}
                    float adsSwitch = 0;//FlansModClient.lastZoomProgress + (FlansModClient.zoomProgress - FlansModClient.lastZoomProgress) * smoothing;//0F;//((float)Math.sin((FlansMod.ticker) / 10F) + 1F) / 2F;

                    if(hand == EnumHand.OFF_HAND)
                    {
                        //GL11.glRotatef(45F, 0F, 1F, 0F);
                        GL11.glTranslatef(-1F, -0.675F, -1.8F);
                    }
                    else
                    {

                        //GL11.glRotatef(45F, 0F, 1F, 0F);
                        //GL11.glRotatef(0F, 1F, 0F, 0F);
                        GL11.glRotatef(0F - 5F * adsSwitch, 0F, 0F, 1F);

                        //GL11.glTranslatef(2.5F, -1.5F, -2F);

                        GL11.glTranslatef(1F, -0.675F, -1.8F);

                        //GL11.glTranslatef(-1F, 0.675F + 0.180F * adsSwitch, -1F - 0.395F * adsSwitch);
                        //Tessellator tessellator = ClientUtils.tes();
                        //BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();

                        //float xMin = -1;
                        //float yMin = -1;
                        //float zMin = -1;
                        //float xMax = 1;
                        //float yMax = 1;
                        //float zMax = 1;

                        //GL11.glPushMatrix();
                        //{
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMin, yMin, zMin).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMin, yMin, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMin, yMax, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMin, yMax, zMin).color(1, 1, 1, 1).endVertex();
                        //    tessellator.draw();
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMax, yMax, zMin).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMax, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMin).color(1, 1, 1, 1).endVertex();
                        //    tessellator.draw();
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMin, yMax, zMin).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMax, zMin).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMin).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMin, yMin, zMin).color(1, 1, 1, 1).endVertex();
                        //    tessellator.draw();
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMin, yMin, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMax, yMax, zMax).color(1, 1, 1, 1).endVertex();
                        //    worldrenderer.pos(xMin, yMax, zMax).color(1, 1, 1, 1).endVertex();
                        //    tessellator.draw();
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMin, yMax, zMin).tex(5 / 32d, 10 / 32d).endVertex();
                        //    worldrenderer.pos(xMin, yMax, zMax).tex(0 / 32d, 10 / 32d).endVertex();
                        //    worldrenderer.pos(xMax, yMax, zMax).tex(0 / 32d, 5 / 32d).endVertex();
                        //    worldrenderer.pos(xMax, yMax, zMin).tex(5 / 32d, 5 / 32d).endVertex();
                        //    tessellator.draw();
                        //    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        //    worldrenderer.pos(xMin, yMin, zMax).tex(5 / 32d, 10 / 32d).endVertex();
                        //    worldrenderer.pos(xMin, yMin, zMin).tex(0 / 32d, 10 / 32d).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMin).tex(0 / 32d, 5 / 32d).endVertex();
                        //    worldrenderer.pos(xMax, yMin, zMax).tex(5 / 32d, 5 / 32d).endVertex();
                        //    tessellator.draw();
                        //}
                        //GL11.glPopMatrix();

                        //if(gunType.hasScopeOverlay)
                        //    GL11.glTranslatef(-0.7F * adsSwitch, -0.12F * adsSwitch, -0.05F * adsSwitch);
                        //GL11.glRotatef(4.5F * adsSwitch, 0F, 0F, 1F);
                        //GL11.glTranslatef(0F, -0.03F * adsSwitch, 0F);

                    }


                    //if(animations.meleeAnimationProgress > 0 && animations.meleeAnimationProgress < gunType.meleePath.size())
                    //{
                    //    Vector3f meleePos = gunType.meleePath.get(animations.meleeAnimationProgress);
                    //    Vector3f nextMeleePos = animations.meleeAnimationProgress + 1 < gunType.meleePath.size() ? gunType.meleePath.get(animations.meleeAnimationProgress + 1) : new Vector3f();
                    //    GL11.glTranslatef(meleePos.x + (nextMeleePos.x - meleePos.x) * smoothing, meleePos.y + (nextMeleePos.y - meleePos.y) * smoothing, meleePos.z + (nextMeleePos.z - meleePos.z) * smoothing);
                    //    Vector3f meleeAngles = gunType.meleePathAngles.get(animations.meleeAnimationProgress);
                    //    Vector3f nextMeleeAngles = animations.meleeAnimationProgress + 1 < gunType.meleePathAngles.size() ? gunType.meleePathAngles.get(animations.meleeAnimationProgress + 1) : new Vector3f();
                    //    GL11.glRotatef(meleeAngles.y + (nextMeleeAngles.y - meleeAngles.y) * smoothing, 0F, 1F, 0F);
                    //    GL11.glRotatef(meleeAngles.z + (nextMeleeAngles.z - meleeAngles.z) * smoothing, 0F, 0F, 1F);
                    //    GL11.glRotatef(meleeAngles.x + (nextMeleeAngles.x - meleeAngles.x) * smoothing, 1F, 0F, 0F);
                    //}

                    // Look at gun stuff
                    //float interp = animations.lookAtTimer + smoothing;
                    //interp /= animations.lookAtTimes[animations.lookAt.ordinal()];

                    final Vector3f idlePos = new Vector3f(0.0f, 0.0f, 0.0f);
                    final Vector3f look1Pos = new Vector3f(0.25f, 0.25f, 0.0f);
                    final Vector3f look2Pos = new Vector3f(0.25f, 0.25f, -0.5f);
                    final Vector3f idleAngles = new Vector3f(0.0f, 0.0f, 0.0f);
                    final Vector3f look1Angles = new Vector3f(0.0f, 70.0f, 0.0f);
                    final Vector3f look2Angles = new Vector3f(0.0f, -60.0f, 60.0f);
                    Vector3f startPos, endPos, startAngles, endAngles;

                    //switch(animations.lookAt)
                    //{
                    //    default:
                    //    case NONE:
                    //        startPos = endPos = idlePos;
                    //        startAngles = endAngles = idleAngles;
                    //        break;
                    //    case LOOK1:
                    //        startPos = endPos = look1Pos;
                    //        startAngles = endAngles = look1Angles;
                    //        break;
                    //    case LOOK2:
                    //        startPos = endPos = look2Pos;
                    //        startAngles = endAngles = look2Angles;
                    //        break;
                    //    case TILT1:
                    //        startPos = idlePos;
                    //        startAngles = idleAngles;
                    //        endPos = look1Pos;
                    //        endAngles = look1Angles;
                    //        break;
                    //    case TILT2:
                    //        startPos = look1Pos;
                    //        startAngles = look1Angles;
                    //        endPos = look2Pos;
                    //        endAngles = look2Angles;
                    //        break;
                    //    case UNTILT:
                    //        startPos = look2Pos;
                    //        startAngles = look2Angles;
                    //        endPos = idlePos;
                    //        endAngles = idleAngles;
                    //        break;
                    //}

                    //GL11.glRotatef(startAngles.y + (endAngles.y - startAngles.y) * interp, 0f, 1f, 0f);
                    //GL11.glRotatef(startAngles.z + (endAngles.z - startAngles.z) * interp, 0f, 0f, 1f);
                    //GL11.glTranslatef(startPos.x + (endPos.x - startPos.x) * interp,
                    //        startPos.y + (endPos.y - startPos.y) * interp,
                    //        startPos.z + (endPos.z - startPos.z) * interp);


                    //GL11.glRotatef(70f, 0f, 1f, 0f);
                    //GL11.glTranslatef(0.25f, 0.25f, 0f);

                    //GL11.glRotatef(-60f, 0f, 1f, 0f);
                    //GL11.glRotatef(60f, 0f, 0f, 1f);
                    //GL11.glTranslatef(0.25f, 0.25f, -0.5f);

                    //GL11.glRotatef(-animations.recoilAngle * (float)Math.sqrt(gunType.recoil) * 1.5f, 0F, 0F, 1F);
                    //GL11.glTranslatef(animations.recoilOffset.x, animations.recoilOffset.y, animations.recoilOffset.z);

                    //if(model.spinningCocking)
                    //{
                    //    GL11.glTranslatef(model.spinPoint.x, model.spinPoint.y, model.spinPoint.z);
                    //    float pumped = (animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing);
                    //    GL11.glRotatef(pumped * 180F + 180F, 0F, 0F, 1F);
                    //    GL11.glTranslatef(-model.spinPoint.x, -model.spinPoint.y, -model.spinPoint.z);
                    //}

                    //if(animations.reloading)
                    //{
                    //    //Calculate the amount of tilt required for the reloading animation
                    //    float effectiveReloadAnimationProgress = animations.lastReloadAnimationProgress + (animations.reloadAnimationProgress - animations.lastReloadAnimationProgress) * smoothing;
                    //    reloadRotate = 1F;
                    //    if(effectiveReloadAnimationProgress < model.tiltGunTime)
                    //        reloadRotate = effectiveReloadAnimationProgress / model.tiltGunTime;
                    //    if(effectiveReloadAnimationProgress > model.tiltGunTime + model.unloadClipTime + model.loadClipTime)
                    //        reloadRotate = 1F - (effectiveReloadAnimationProgress - (model.tiltGunTime + model.unloadClipTime + model.loadClipTime)) / model.untiltGunTime;                        Rotate the gun dependent on the animation type
                    //    switch(model.animationType)
                    //    {
                    //        case BOTTOM_CLIP : case PISTOL_CLIP : case SHOTGUN : case END_LOADED :
                    //    {
                    //        GL11.glRotatef(60F * reloadRotate, 0F, 0F, 1F);
                    //        GL11.glRotatef(30F * reloadRotate * flip, 1F, 0F, 0F);
                    //        GL11.glTranslatef(0.25F * reloadRotate, 0F, 0F);
                    //        break;
                    //    }
                    //        case BACK_LOADED :
                    //        {
                    //            GL11.glRotatef(-75F * reloadRotate, 0F, 0F, 1F);
                    //            GL11.glRotatef(-30F * reloadRotate * flip, 1F, 0F, 0F);
                    //            GL11.glTranslatef(0.5F * reloadRotate, 0F, 0F);
                    //            break;
                    //        }
                    //        case BULLPUP :
                    //        {
                    //            GL11.glRotatef(70F * reloadRotate, 0F, 0F, 1F);
                    //            GL11.glRotatef(10F * reloadRotate * flip, 1F, 0F, 0F);
                    //            GL11.glTranslatef(0.5F * reloadRotate, -0.2F * reloadRotate, 0F);
                    //            break;
                    //        }
                    //        case RIFLE :
                    //        {
                    //            GL11.glRotatef(30F * reloadRotate, 0F, 0F, 1F);
                    //            GL11.glRotatef(-30F * reloadRotate * flip, 1F, 0F, 0F);
                    //            GL11.glTranslatef(0.5F * reloadRotate, 0F, -0.5F * reloadRotate);
                    //            break;
                    //        }
                    //        case RIFLE_TOP : case REVOLVER :
                    //    {
                    //        GL11.glRotatef(30F * reloadRotate, 0F, 0F, 1F);
                    //        GL11.glRotatef(10F * reloadRotate, 0F, 1F, 0F);
                    //        GL11.glRotatef(-10F * reloadRotate * flip, 1F, 0F, 0F);
                    //        GL11.glTranslatef(0.1F * reloadRotate, -0.2F * reloadRotate, -0.1F * reloadRotate);
                    //        break;
                    //    }
                    //        case ALT_PISTOL_CLIP :
                    //        {
                    //            GL11.glRotatef(60F * reloadRotate * flip, 0F, 1F, 0F);
                    //            GL11.glTranslatef(0.15F * reloadRotate, 0.25F * reloadRotate, 0F);
                    //            break;
                    //        }
                    //        case STRIKER :
                    //        {
                    //            GL11.glRotatef(-35F * reloadRotate * flip, 1F, 0F, 0F);
                    //            GL11.glTranslatef(0.2F * reloadRotate, 0F, -0.1F * reloadRotate);
                    //            break;
                    //        }
                    //        case GENERIC :
                    //        {
                    //            //Gun reloads partly or completely off-screen.
                    //            GL11.glRotatef(45F * reloadRotate, 0F, 0F, 1F);
                    //            GL11.glTranslatef(-0.2F * reloadRotate, -0.5F * reloadRotate, 0F);
                    //            break;
                    //        }
                    //        case CUSTOM :
                    //        {
                    //            GL11.glRotatef(model.rotateGunVertical * reloadRotate, 0F, 0F, 1F);
                    //            GL11.glRotatef(model.rotateGunHorizontal * reloadRotate, 0F, 1F, 0F);
                    //            GL11.glRotatef(model.tiltGun * reloadRotate, 1F, 0F, 0F);
                    //            GL11.glTranslatef(model.translateGun.x * reloadRotate,  model.translateGun.y * reloadRotate, model.translateGun.z * reloadRotate);
                    //            break;
                    //        }
                    //        default : break;
                    //    }
                    //}
                    break;
                }
                default : break;
            }


            renderGun(item, f, reloadRotate, partialTicks);
        }
        GL11.glPopMatrix();
    }

    /** Gun render method, seperated from transforms so that mecha renderer may also call this */
    public void renderGun(ItemStack item, float f, float reloadRotate, float partialTicks)
    {
        //Make sure we actually have the renderEngine
        if(renderEngine == null)
            renderEngine = Minecraft.getMinecraft().renderEngine;
        renderEngine.bindTexture(TEXTURES);

        Tessellator tessellator = ClientUtils.tes();
        BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();
        //If we have no animation variables, use defaults
        //if(animations == null)
        //    animations = GunAnimations.defaults;

        //Get all the attachments that we may need to render
        //AttachmentType scopeAttachment = type.getScope(item);
        //AttachmentType barrelAttachment = type.getBarrel(item);
        //AttachmentType stockAttachment = type.getStock(item);
        //AttachmentType gripAttachment = type.getGrip(item);

        //ItemStack scopeItemStack = type.getScopeItemStack(item);
        //ItemStack barrelItemStack = type.getBarrelItemStack(item);
        //ItemStack stockItemStack = type.getStockItemStack(item);
        //ItemStack gripItemStack = type.getGripItemStack(item);

        //ItemStack[] bulletStacks = new ItemStack[type.numAmmoItemsInGun];
        boolean empty = true;

        float xMin = -1;
        float yMin = -1;
        float zMin = -1;
        float xMax = 1;
        float yMax = 1;
        float zMax = 1;
        float xScale=1;
        float yScale=1;
        float zScale = 1;
        if(item.getItem() instanceof BaseGunItem){
            xMax=((BaseGunItem) item.getItem()).getCaliber()/1.5F;
            xMin=-((BaseGunItem) item.getItem()).getCaliber()/1.5F;
            yMax=((BaseGunItem) item.getItem()).getCaliber()/1.5F;
            yMin=-((BaseGunItem) item.getItem()).getCaliber()/1.5F;
            xScale=((BaseGunItem) item.getItem()).getCaliber()*1.3F;
            yScale=((BaseGunItem) item.getItem()).getCaliber()*1.3F;//STILL NEEDS TESTING AND TWEAKING

        }

        GL11.glScaled(f, f, f);
        //for(int i = 0; i < type.numAmmoItemsInGun; i++)
        //{
        //    bulletStacks[i] = ((BaseGunItem)item.getItem()).getBulletItemStack(item, i);
        //    if(bulletStacks[i] != null && bulletStacks[i].getItem() instanceof ItemBullet && bulletStacks[i].getItemDamage() < bulletStacks[i].getMaxDamage())
        //        empty = false;
        //}

        //Load texture
        ////renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(type.getPaintjob(item.getTagCompound().getString("Paint"))));
        //Paintjob paintjob = type.getPaintjob(item.getItemDamage());
        //if(bindTextures)
        //{
        //    if(PaintableType.HasCustomPaintjob(item))
        //    {
        //        renderEngine.bindTexture(PaintableType.GetCustomPaintjobSkinResource(item));
        //    }
        //    else
        //    {
        //        renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(paintjob));
        //    }
        //}

        //if(scopeAttachment != null)
        //    GL11.glTranslatef(0F, -scopeAttachment.model.renderOffset / 16F, 0F);

        //Render the gun and default attachment models


        GL11.glPushMatrix();
        {


            renderWavefront(ClientProxy.models.simpleBarrel, xScale, yScale, zScale);


            //GL11.glScalef(type.modelScale, type.modelScale, type.modelScale);

            if(item.getItem() instanceof BaseGunItem) {
                if(((BaseGunItem) item.getItem()).isChambering()) {
                    if(Mouse.isButtonDown((100+ClientProxy.keyBindings[2].getKeyCode()))) {
                        GL11.glTranslatef(0, 0, (zMax-zMin));
                    }
                    else {
                        GL11.glTranslatef(0, 0, (zMax - zMin) * partialTicks);
                    }
                }
            }

            renderWavefront(ClientProxy.models.simpleBolt, xScale, yScale, zScale);

            if(item.getItem() instanceof BaseGunItem) {
                if(((BaseGunItem) item.getItem()).isChambering()/*&&something to check if it has a pivoting bolt handle*/) {
                    if(Mouse.isButtonDown((100+ClientProxy.keyBindings[2].getKeyCode()))) {
                        GL11.glRotatef(45, 0, 0, 1);
                    }
                    else {
                        GL11.glRotatef(45*partialTicks, 0, 0, 1);
                    }
                }
                else if(!((BaseGunItem) item.getItem()).isChambering()/*&&something to check if it has a pivoting bolt handle*/) {
                    GL11.glRotatef(-25, 0, 0, 1);
                }

                renderWavefront(ClientProxy.models.simpleBoltHandle, xScale, xScale, xScale);

            }



            //model.renderGun(f);
            //model.renderCustom(f, animations);
            //if(scopeAttachment == null && !model.scopeIsOnSlide && !model.scopeIsOnBreakAction)
            //    model.renderDefaultScope(f);
            //if(barrelAttachment == null)
            //    model.renderDefaultBarrel(f);
            //if(stockAttachment == null)
            //    model.renderDefaultStock(f);
            //if(gripAttachment == null && !model.gripIsOnPump)
            //    model.renderDefaultGrip(f);
            //
            ////Render various shoot / reload animated parts
            ////Render the slide
            //GL11.glPushMatrix();
            //{
            //    GL11.glTranslatef(-(animations.lastGunSlide + (animations.gunSlide - animations.lastGunSlide) * smoothing) * model.gunSlideDistance, 0F, 0F);
            //    model.renderSlide(f);
            //    if(scopeAttachment == null && model.scopeIsOnSlide)
            //        model.renderDefaultScope(f);
            //}
            //GL11.glPopMatrix();

            ////Render the break action
            //GL11.glPushMatrix();
            //{
            //    GL11.glTranslatef(model.barrelBreakPoint.x, model.barrelBreakPoint.y, model.barrelBreakPoint.z);
            //    GL11.glRotatef(reloadRotate * -model.breakAngle, 0F, 0F, 1F);
            //    GL11.glTranslatef(-model.barrelBreakPoint.x, -model.barrelBreakPoint.y, -model.barrelBreakPoint.z);
            //    model.renderBreakAction(f);
            //    if(scopeAttachment == null && model.scopeIsOnBreakAction)
            //        model.renderDefaultScope(f);
            //}
            //GL11.glPopMatrix();

            ////Render the pump-action handle
            //GL11.glPushMatrix();
            //{
            //    GL11.glTranslatef(-(1 - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance, 0F, 0F);
            //    model.renderPump(f);
            //    if(gripAttachment == null && model.gripIsOnPump)
            //        model.renderDefaultGrip(f);
            //}
            //GL11.glPopMatrix();

            ////Render the minigun barrels
            //if(type.mode == EnumFireMode.MINIGUN)
            //{
            //    GL11.glPushMatrix();
            //    GL11.glTranslatef(model.minigunBarrelOrigin.x, model.minigunBarrelOrigin.y, model.minigunBarrelOrigin.z);
            //    GL11.glRotatef(animations.minigunBarrelRotation, 1F, 0F, 0F);
            //    GL11.glTranslatef(-model.minigunBarrelOrigin.x, -model.minigunBarrelOrigin.y, -model.minigunBarrelOrigin.z);
            //    model.renderMinigunBarrel(f);
            //    GL11.glPopMatrix();
            //}

            ////Render the cocking handle

            ////Render the revolver barrel
            //GL11.glPushMatrix();
            //{
            //    GL11.glTranslatef(model.revolverFlipPoint.x, model.revolverFlipPoint.y, model.revolverFlipPoint.z);
            //    GL11.glRotatef(reloadRotate * model.revolverFlipAngle, 1F, 0F, 0F);
            //    GL11.glTranslatef(-model.revolverFlipPoint.x, -model.revolverFlipPoint.y, -model.revolverFlipPoint.z);
            //    model.renderRevolverBarrel(f);
            //}
            //GL11.glPopMatrix();

            ////Render the clip
            //GL11.glPushMatrix();
            //{
            //    boolean shouldRender = true;
            //    //Check to see if the ammo should be rendered first
            //    switch(model.animationType)
            //    {
            //        case END_LOADED : case BACK_LOADED :
            //    {
            //        if(empty)
            //            shouldRender = false;
            //        break;
            //    }
            //        default: break;
            //    }
            //    //If it should be rendered, do the transformations required
            //    if(shouldRender && animations.reloading && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            //    {
            //        //Calculate the amount of tilt required for the reloading animation
            //        float effectiveReloadAnimationProgress = animations.lastReloadAnimationProgress + (animations.reloadAnimationProgress - animations.lastReloadAnimationProgress) * smoothing;
            //        float clipPosition = 0F;
            //        if(effectiveReloadAnimationProgress > model.tiltGunTime && effectiveReloadAnimationProgress < model.tiltGunTime + model.unloadClipTime)
            //            clipPosition = (effectiveReloadAnimationProgress - model.tiltGunTime) / model.unloadClipTime;
            //        if(effectiveReloadAnimationProgress >= model.tiltGunTime + model.unloadClipTime && effectiveReloadAnimationProgress < model.tiltGunTime + model.unloadClipTime + model.loadClipTime)
            //            clipPosition = 1F - (effectiveReloadAnimationProgress - (model.tiltGunTime + model.unloadClipTime)) / model.loadClipTime;

            //        float loadOnlyClipPosition = Math.max(0F, Math.min(1F, 1F - ((effectiveReloadAnimationProgress - model.tiltGunTime) / (model.unloadClipTime + model.loadClipTime))));

            //        //Rotate the gun dependent on the animation type
            //        switch(model.animationType)
            //        {
            //            case BREAK_ACTION :
            //            {
            //                GL11.glTranslatef(model.barrelBreakPoint.x, model.barrelBreakPoint.y, model.barrelBreakPoint.z);
            //                GL11.glRotatef(reloadRotate * -model.breakAngle, 0F, 0F, 1F);
            //                GL11.glTranslatef(-model.barrelBreakPoint.x, -model.barrelBreakPoint.y, -model.barrelBreakPoint.z);
            //                GL11.glTranslatef(-1F * clipPosition, 0F, 0F);
            //                break;
            //            }
            //            case REVOLVER :
            //            {
            //                GL11.glTranslatef(model.revolverFlipPoint.x, model.revolverFlipPoint.y, model.revolverFlipPoint.z);
            //                GL11.glRotatef(reloadRotate * model.revolverFlipAngle, 1F, 0F, 0F);
            //                GL11.glTranslatef(-model.revolverFlipPoint.x, -model.revolverFlipPoint.y, -model.revolverFlipPoint.z);
            //                GL11.glTranslatef(-1F * clipPosition, 0F, 0F);
            //                break;
            //            }
            //            case BOTTOM_CLIP :
            //            {
            //                GL11.glRotatef(-180F * clipPosition, 0F, 0F, 1F);
            //                GL11.glRotatef(60F * clipPosition, 1F, 0F, 0F);
            //                GL11.glTranslatef(0.5F * clipPosition, 0F, 0F);
            //                break;
            //            }
            //            case PISTOL_CLIP :
            //            {
            //                GL11.glRotatef(-90F * clipPosition * clipPosition, 0F, 0F, 1F);
            //                GL11.glTranslatef(0F, -1F * clipPosition, 0F);
            //                break;
            //            }
            //            case ALT_PISTOL_CLIP :
            //            {
            //                GL11.glRotatef(5F * clipPosition, 0F, 0F, 1F);
            //                GL11.glTranslatef(0F, -3F * clipPosition, 0F);
            //                break;
            //            }
            //            case SIDE_CLIP :
            //            {
            //                GL11.glRotatef(180F * clipPosition, 0F, 1F, 0F);
            //                GL11.glRotatef(60F * clipPosition, 0F, 1F, 0F);
            //                GL11.glTranslatef(0.5F * clipPosition, 0F, 0F);
            //                break;
            //            }
            //            case BULLPUP :
            //            {
            //                GL11.glRotatef(-150F * clipPosition, 0F, 0F, 1F);
            //                GL11.glRotatef(60F * clipPosition, 1F, 0F, 0F);
            //                GL11.glTranslatef(1F * clipPosition, -0.5F * clipPosition, 0F);
            //                break;
            //            }
            //            case P90 :
            //            {
            //                GL11.glRotatef(-15F * reloadRotate * reloadRotate, 0F, 0F, 1F);
            //                GL11.glTranslatef(0F, 0.075F * reloadRotate, 0F);
            //                GL11.glTranslatef(-2F * clipPosition, -0.3F * clipPosition, 0.5F * clipPosition);
            //                break;
            //            }
            //            case RIFLE :
            //            {
            //                float thing = clipPosition * model.numBulletsInReloadAnimation;
            //                int bulletNum = MathHelper.floor(thing);
            //                float bulletProgress = thing - bulletNum;

            //                GL11.glRotatef(bulletProgress * 15F, 0F, 1F, 0F);
            //                GL11.glRotatef(bulletProgress * 15F, 0F, 0F, 1F);
            //                GL11.glTranslatef(bulletProgress * -1F, 0F, bulletProgress * 0.5F);

            //                break;
            //            }
            //            case RIFLE_TOP :
            //            {
            //                float thing = clipPosition * model.numBulletsInReloadAnimation;
            //                int bulletNum = MathHelper.floor(thing);
            //                float bulletProgress = thing - bulletNum;

            //                GL11.glRotatef(bulletProgress * 55F, 0F, 1F, 0F);
            //                GL11.glRotatef(bulletProgress * 95F, 0F, 0F, 1F);
            //                GL11.glTranslatef(bulletProgress * -0.1F, bulletProgress * 1F, bulletProgress * 0.5F);

            //                break;
            //            }
            //            case SHOTGUN : case STRIKER :
            //        {
            //            float thing = clipPosition * model.numBulletsInReloadAnimation;
            //            int bulletNum = MathHelper.floor(thing);
            //            float bulletProgress = thing - bulletNum;

            //            GL11.glRotatef(bulletProgress * -30F, 0F, 0F, 1F);
            //            GL11.glTranslatef(bulletProgress * -0.5F, bulletProgress * -1F, 0F);

            //            break;
            //        }
            //            case CUSTOM :
            //            {
            //                GL11.glRotatef(model.rotateClipVertical * clipPosition, 0F, 0F, 1F);
            //                GL11.glRotatef(model.rotateClipHorizontal * clipPosition, 0F, 1F, 0F);
            //                GL11.glRotatef(model.tiltClip * clipPosition, 1F, 0F, 0F);
            //                GL11.glTranslatef(model.translateClip.x * clipPosition,  model.translateClip.y * clipPosition, model.translateClip.z * clipPosition);
            //                break;
            //            }
            //            case END_LOADED :
            //            {
            //                //float bulletProgress = 1F;
            //                //if(effectiveReloadAnimationProgress > model.tiltGunTime)
            //                //	bulletProgress = 1F - Math.min((effectiveReloadAnimationProgress - model.tiltGunTime) / (model.unloadClipTime + model.loadClipTime), 1);



            //                float dYaw = (loadOnlyClipPosition > 0.5F ? loadOnlyClipPosition * 2F - 1F : 0F);


            //                GL11.glRotatef(-45F * dYaw, 0F, 0F, 1F);
            //                GL11.glTranslatef(-model.endLoadedAmmoDistance * dYaw, -0.5F * dYaw, 0F);

            //                float xDisplacement = (loadOnlyClipPosition < 0.5F ? loadOnlyClipPosition * 2F : 1F);

            //                GL11.glTranslatef(model.endLoadedAmmoDistance * xDisplacement, 0F, 0F);

			//				/*
			//				GL11.glTranslatef(1F * bulletProgress, -3F * bulletProgress, 0F);
			//				if(bulletProgress > 0.5F)
			//					GL11.glRotatef(-90F * (bulletProgress * 2F), 0F, 0F, 1F);

			//				if(bulletProgress < 0.5F)
			//				{
			//					GL11.glTranslatef(-3F * (bulletProgress - 0.5F), 0F, 0F);

			//				}
			//				*/


            //                break;
            //            }
            //            case BACK_LOADED :
            //            {
            //                float dYaw = (loadOnlyClipPosition > 0.5F ? loadOnlyClipPosition * 2F - 1F : 0F);


            //                //GL11.glRotatef(-45F * dYaw, 0F, 0F, 1F);
            //                GL11.glTranslatef(model.endLoadedAmmoDistance * dYaw, -0.5F * dYaw, 0F);

            //                float xDisplacement = (loadOnlyClipPosition < 0.5F ? loadOnlyClipPosition * 2F : 1F);

            //                GL11.glTranslatef(-model.endLoadedAmmoDistance * xDisplacement, 0F, 0F);
            //            }

            //            default : break;
            //        }
            //    }

            //    if(shouldRender)
            //        model.renderAmmo(f);
            //}
            //GL11.glPopMatrix();
        }
        GL11.glPopMatrix();

        //Render static attachments
        //Scope
        //if(scopeAttachment != null)
        //{
        //    GL11.glPushMatrix();
        //    {
        //        Paintjob scopepaintjob = scopeAttachment.getPaintjob(scopeItemStack.getItemDamage());
        //        renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(scopepaintjob));
        //        if(model.scopeIsOnBreakAction)
        //        {
        //            GL11.glTranslatef(model.barrelBreakPoint.x, model.barrelBreakPoint.y, model.barrelBreakPoint.z);
        //            GL11.glRotatef(reloadRotate * -model.breakAngle, 0F, 0F, 1F);
        //            GL11.glTranslatef(-model.barrelBreakPoint.x, -model.barrelBreakPoint.y, -model.barrelBreakPoint.z);
        //        }
        //        GL11.glTranslatef(model.scopeAttachPoint.x * type.modelScale, model.scopeAttachPoint.y * type.modelScale, model.scopeAttachPoint.z * type.modelScale);
//
        //        if(model.scopeIsOnSlide)
        //            GL11.glTranslatef(-(animations.lastGunSlide + (animations.gunSlide - animations.lastGunSlide) * smoothing) * model.gunSlideDistance, 0F, 0F);
        //        GL11.glScalef(scopeAttachment.modelScale, scopeAttachment.modelScale, scopeAttachment.modelScale);
        //        ModelAttachment scopeModel = scopeAttachment.model;
        //        if(scopeModel != null)
        //            scopeModel.renderAttachment(f);
        //        renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
        //    }
        //    GL11.glPopMatrix();
        //}
//
        ////Grip
        //if(gripAttachment != null)
        //{
        //    GL11.glPushMatrix();
        //    {
        //        Paintjob grippaintjob = gripAttachment.getPaintjob(gripItemStack.getItemDamage());
        //        renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(grippaintjob));
        //        GL11.glTranslatef(model.gripAttachPoint.x * type.modelScale, model.gripAttachPoint.y * type.modelScale, model.gripAttachPoint.z * type.modelScale);
        //        if(model.gripIsOnPump)
        //            GL11.glTranslatef(-(1 - Math.abs(animations.lastPumped + (animations.pumped - animations.lastPumped) * smoothing)) * model.pumpHandleDistance, 0F, 0F);
        //        GL11.glScalef(gripAttachment.modelScale, gripAttachment.modelScale, gripAttachment.modelScale);
        //        ModelAttachment gripModel = gripAttachment.model;
        //        if(gripModel != null)
        //            gripModel.renderAttachment(f);
        //        renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
        //    }
        //    GL11.glPopMatrix();
        //}
//
        ////Barrel
        //if(barrelAttachment != null)
        //{
        //    GL11.glPushMatrix();
        //    {
        //        Paintjob barrelpaintjob = barrelAttachment.getPaintjob(barrelItemStack.getItemDamage());
        //        renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(barrelpaintjob));
        //        GL11.glTranslatef(model.barrelAttachPoint.x * type.modelScale, model.barrelAttachPoint.y * type.modelScale, model.barrelAttachPoint.z * type.modelScale);
        //        GL11.glScalef(barrelAttachment.modelScale, barrelAttachment.modelScale, barrelAttachment.modelScale);
        //        ModelAttachment barrelModel = barrelAttachment.model;
        //        if(barrelModel != null)
        //            barrelModel.renderAttachment(f);
        //        renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
        //    }
        //    GL11.glPopMatrix();
        //}
//
        ////Stock
        //if(stockAttachment != null)
        //{
        //    GL11.glPushMatrix();
        //    {
        //        Paintjob stockpaintjob = stockAttachment.getPaintjob(stockItemStack.getItemDamage());
        //        renderEngine.bindTexture(FlansModResourceHandler.getPaintjobTexture(stockpaintjob));
        //        GL11.glTranslatef(model.stockAttachPoint.x * type.modelScale, model.stockAttachPoint.y * type.modelScale, model.stockAttachPoint.z * type.modelScale);
        //        GL11.glScalef(stockAttachment.modelScale, stockAttachment.modelScale, stockAttachment.modelScale);
        //        ModelAttachment stockModel = stockAttachment.model;
        //        if(stockModel != null)
        //            stockModel.renderAttachment(f);
        //        renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
        //    }
        //    GL11.glPopMatrix();
        //}
    }

    private void renderWavefront(PartModel model, float xScale, float yScale, float zScale) {

        //BufferedReader reader = new BufferedReader(new FileReader(model.getResourcePath()));
        //String line=reader.readLine();
        int i = 0;
        Tessellator tessellator = ClientUtils.tes();
        BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();
        while (i<model.faceList.length) {
                worldrenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
                worldrenderer.pos(-model.xCoords[model.faceList[i]]*xScale,  model.yCoords[model.faceList[i]]*yScale,  model.zCoords[model.faceList[i]]*zScale) .tex(0 / 32d, 10 / 32d).endVertex();
                i+=1;
                worldrenderer.pos(-model.xCoords[model.faceList[i]]*xScale,  model.yCoords[model.faceList[i]]*yScale,  model.zCoords[model.faceList[i]]*zScale) .tex(0 / 32d, 10 / 32d).endVertex();
                i+=1;
                worldrenderer.pos(-model.xCoords[model.faceList[i]]*xScale,  model.yCoords[model.faceList[i]]*yScale,  model.zCoords[model.faceList[i]]*zScale) .tex(5 / 32d, 10 / 32d).endVertex();
                i+=1;
                tessellator.draw();
        }
        //while (line != null) {
        //    System.out.println(line);
        //    line=reader.readLine();
        //}
    }
}

    /*private RenderManager renderManager;

    //FIX DIS LATER
    public static final ResourceLocation TEXTURES = new ResourceLocation(TinkersArsenal.MODID + ":textures/models/entity_arsenal_projectile");

    public RenderGun(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
        super(renderManagerIn, p_i46167_2_);
        this.renderManager=renderManagerIn;

        if(checkRender){TinkersArsenal.logger.info("Render Manager Loaded for RenderGun");}
    }

}*/
