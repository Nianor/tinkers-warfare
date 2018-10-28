package com.nianor.tinkersarsenal.client.particles;

import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TracerParticle extends ParticleFlame {

    private float scale;

    public TracerParticle(World world, double xPos, double yPos, double zPos) {
        super(world, xPos, yPos, zPos, 0, 0, 0);
        this.scale = 3.5F;
    }

    public TracerParticle(World world, double xPos, double yPos, double zPos, float scale, float red, float green, float blue, float xVel, float yVel, float zVel) {
        super(world, xPos, yPos, zPos, 0, 0, 0);

        this.scale = scale;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        motionX=xVel;
        motionY=yVel;
        motionZ=zVel;
        this.particleMaxAge = (int)(2.0D / (Math.random() * 0.8D + 0.2D));

    }


    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float x, float y, float z, float p_180434_7_, float p_180434_8_) {
        float xz = (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        this.particleScale = this.scale * (1 - xz * xz * 0.5F);

        float f = this.particleTextureIndexX / 16F;
        float f1 = f + 0.0624375F;
        float f2 = this.particleTextureIndexY / 16F;
        float f3 = f2 + 0.0624375F;
        float scale = 0.1F * this.particleScale;

        if (this.particleTexture != null) {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        int brightness = this.getBrightnessForRender(partialTicks);
        //Commented out for tweaking, so I can keep an original copy, since I have no idea what I'm doing with this.
        //int j = brightness >> 16 & 65535;
        //int k = brightness & 65535;

        int j = brightness >> 16 & 4000;
        int k = brightness & 4000;

        buffer.pos((double)(xPos - x * scale - p_180434_7_ * scale), (double)(yPos - y * scale), (double)(zPos - z * scale - p_180434_8_ * scale)).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)(xPos - x * scale + p_180434_7_ * scale), (double)(yPos + y * scale), (double)(zPos - z * scale + p_180434_8_ * scale)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)(xPos + x * scale + p_180434_7_ * scale), (double)(yPos + y * scale), (double)(zPos + z * scale + p_180434_8_ * scale)).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)(xPos + x * scale - p_180434_7_ * scale), (double)(yPos - y * scale), (double)(zPos + z * scale - p_180434_8_ * scale)).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

}
