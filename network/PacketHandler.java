package com.nianor.tinkersarsenal.network;

import com.mojang.util.UUIDTypeAdapter;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

import static com.nianor.tinkersarsenal.TinkersArsenal.checkProjectiles;
import static com.nianor.tinkersarsenal.TinkersArsenal.logger;
import static com.nianor.tinkersarsenal.TinkersArsenal.trackGunFiring;

public class PacketHandler {

    public static class VelocityPacketHandler implements IMessageHandler<VelocityPacket, IMessage> {
        @Override
        public IMessage onMessage(VelocityPacket message, MessageContext ctx) {
            if (checkProjectiles) {
                logger.info("packet initialized on side {}", ctx.side);
            }


            if (ctx.side == Side.SERVER) {

                //ctx.getServerHandler().player.getEntityWorld().getLoadedEntityList().

                System.out.println(ctx.getServerHandler().player.getServer().getEntityFromUuid(message.entityID));
                ctx.getServerHandler().sendPacket(new SPacketEntityTeleport(ctx.getServerHandler().player.getServer().getEntityFromUuid(message.entityID)));
                //Entity entity = ctx.getServerHandler().player.getServerWorld().getEntityByID(message.entityID);
            }
            else {

            }
            return null;
        }
    }

    public static class ParticlePacketHandler implements IMessageHandler<ParticlePacket, IMessage> {

        @Override
        public IMessage onMessage(ParticlePacket message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ctx.getClientHandler().handleParticles(new SPacketParticles(EnumParticleTypes.EXPLOSION_HUGE,true, message.x, message.y, message.z, message.xSpeed, message.ySpeed,message.zSpeed, (float)Math.sqrt(message.xSpeed*message.xSpeed+message.ySpeed*message.ySpeed+message.zSpeed*message.zSpeed), 1) );
            }
            if(ctx.side==Side.SERVER) {
                ctx.getServerHandler().sendPacket(new SPacketParticles(EnumParticleTypes.EXPLOSION_HUGE,true, message.x, message.y, message.z, message.xSpeed, message.ySpeed,message.zSpeed, (float)Math.sqrt(message.xSpeed*message.xSpeed+message.ySpeed*message.ySpeed+message.zSpeed*message.zSpeed), 1));
            }
            return null;
        }
    }

    public static class KeyPressPacketHandler implements IMessageHandler<KeyPressPacket, IMessage> {

        @Override
        public IMessage onMessage(KeyPressPacket message, MessageContext ctx){
            if (trackGunFiring) {
                logger.info("packet initialized on side {}", ctx.side);
            }
            if(ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if(trackGunFiring) {logger.info("Server Packet KeyPress");}
                if (player.getHeldItemMainhand().getItem() instanceof BaseGunItem){
                    BaseGunItem gunItem = (BaseGunItem) player.getHeldItemMainhand().getItem();
                    World world = ctx.getServerHandler().player.world;
                    if (message.chamber) {
                        gunItem.GunChamber(world, player);
                    }
                    if (message.load) {
                        gunItem.GunLoad(world, player);
                    }
                    if (message.selectorSwitch) {
                        if(trackGunFiring){logger.info("Selector switch flipped");}
                        gunItem.GunSelector(world, player);
                    }
                }
            }
        return null;
        }
    }
}