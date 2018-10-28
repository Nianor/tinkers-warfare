package com.nianor.tinkersarsenal.tinkers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.MinecraftForge;

public class TASoundEvent {
    Minecraft mc =Minecraft.getMinecraft();
    TASoundEvent(Entity entity, double x, double y, double z, double xVel, double yVel, double zVel, net.minecraft.util.SoundEvent sound, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
        double d0 = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
        System.out.println("Starting sound event");

        PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(sound, category, volume, pitch, (float)x, (float)y, (float)z);
        System.out.println("Sound record initialized");
        System.out.println("Distance Delay Initialized");
        double d1 = Math.sqrt(d0) / 20.0D;
        System.out.println("About to play delayed sound");
        //this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int)(d1));
        entity.playSound(sound, volume, pitch);
        System.out.println("Sound Played");
    }
}
