package com.nianor.tinkersarsenal.network;

import com.mojang.util.UUIDTypeAdapter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.CharSequenceUtils;
import scala.util.parsing.input.CharSequenceReader;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

import java.nio.charset.Charset;
import java.util.UUID;

public class VelocityPacket implements IMessage {
    public UUID entityID;
    public double seed;
    public double posX;
    public double posY;
    public double posZ;
    public double velX;
    public double velY;
    public double velZ;

    public VelocityPacket(){}

    public VelocityPacket(UUID id, double seed, double posX, double posY, double posZ, double velX, double velY, double velZ){
        this.entityID=id;
        this.seed=seed;
        this.posX=posX;
        this.posY=posY;
        this.posZ=posZ;
        this.velX=velX;
        this.velY=velY;
        this.velZ=velZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        this.entityID= UUIDTypeAdapter.fromString((String)buf.readCharSequence(32, Charset.defaultCharset()));
        this.seed = buf.readDouble();
        this.posX= buf.readDouble();
        this.posY= buf.readDouble();
        this.posZ= buf.readDouble();
        this.velX= buf.readDouble();
        this.velY= buf.readDouble();
        this.velZ= buf.readDouble();




    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeCharSequence(UUIDTypeAdapter.fromUUID(this.entityID), Charset.defaultCharset());
        buf.writeDouble(this.seed);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeDouble(velX);
        buf.writeDouble(velY);
        buf.writeDouble(velZ);

    }
}
