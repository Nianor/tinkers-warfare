package com.nianor.tinkersarsenal.network;

import com.mojang.util.UUIDTypeAdapter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.nio.charset.Charset;
import java.util.UUID;

import static jdk.nashorn.internal.objects.ArrayBufferView.length;

public class KeyPressPacket implements IMessage {

    public boolean selectorSwitch;
    public boolean fire;
    public boolean chamber;
    public boolean load;

    public KeyPressPacket(){}

    public KeyPressPacket(boolean selectorSwitch, boolean fire, boolean chamber, boolean load){

        this.selectorSwitch=selectorSwitch;
        this.fire=fire;
        this.chamber=chamber;
        this.load=load;

    }

    @Override
    public void fromBytes(ByteBuf buf) {

        this.selectorSwitch = buf.readBoolean();
        this.fire = buf.readBoolean();
        this.chamber = buf.readBoolean();
        this.load = buf.readBoolean();


    }

    @Override
    public void toBytes(ByteBuf buf) {

        buf.writeBoolean(this.selectorSwitch);
        buf.writeBoolean(this.fire);
        buf.writeBoolean(this.chamber);
        buf.writeBoolean(this.load);

    }
}
