package com.nianor.tinkersarsenal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class ParticlePacket implements IMessage {
    public float x;
    public float y;
    public float z;
    public float xSpeed;
    public float ySpeed;
    public float zSpeed;

    public ParticlePacket(){}

    public ParticlePacket(float x, float y, float z, float xSpeed, float ySpeed, float zSpeed){
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.xSpeed = buf.readFloat();
        this.ySpeed = buf.readFloat();
        this.zSpeed = buf.readFloat();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.xSpeed);
        buf.writeFloat(this.ySpeed);
        buf.writeFloat(this.zSpeed);

    }
}
