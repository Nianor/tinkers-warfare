package com.nianor.tinkersarsenal.common.tileentities;

import com.nianor.tinkersarsenal.TinkersArsenal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DamageCounterEntity extends TileEntity implements ITickable{

    private NBTTagCompound customTileData;

    //This *might* not work. Idk.



    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return !newState.getBlock().isAir(newState, world, pos);
    }

    public void damage(int damage) {
        customTileData.setInteger("damage", customTileData.getInteger("damage")+damage);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        System.out.println("Getting Update Packets");
        NBTTagCompound nbtTag = customTileData;

        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        System.out.println("Receiving Update Packets");
        NBTTagCompound tag = pkt.getNbtCompound();
        customTileData=tag;
    }

    @Override
    public void update() {
        System.out.println("Damage counter updating.");
        if(!customTileData.hasKey("isBroken")) {
            customTileData.setBoolean("isBroken", false);
        }
        if(customTileData.getBoolean("isBroken")) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            boolean dropBlock=Math.random()<(1/Math.sqrt((customTileData.getInteger("damage")-customTileData.getInteger("health"))));
            world.destroyBlock(pos, dropBlock);
            System.out.println("Block Broken");
            this.invalidate();
        }
        if(customTileData.hasKey("cooldown")) {
            customTileData.setInteger("cooldown", customTileData.getInteger("cooldown")-1);
            if(customTileData.getInteger("cooldown") <= 0) {
                customTileData.removeTag("cooldown");
            }
        }
        else if (customTileData.hasKey("damage")) {
            customTileData.setInteger("cooldown", customTileData.getInteger("damage")*5);
        }
        if(customTileData.hasKey("damage")&&!customTileData.hasKey("cooldown")) {
            customTileData.setInteger("damage", customTileData.getInteger("damage")-1);
            if(customTileData.getInteger("health")-customTileData.getInteger("damage") <0 ) {
                customTileData.setBoolean("isBroken", true);
            }
        }
        markDirty();
    }

}
