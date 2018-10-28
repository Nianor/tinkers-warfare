package com.nianor.tinkersarsenal.tinkers;

import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class TestItem extends ItemArrow {
    public TestItem()
    {
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public ActionResult<net.minecraft.item.ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!playerIn.capabilities.isCreativeMode)
        {
            itemstack.shrink(1);
        }

        //worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundCategory.NEUTRAL, 1F, 5F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            EntityArsenalProjectile projectile = new EntityArsenalProjectile(worldIn, playerIn, playerIn.cameraYaw, 1F, .5F, 1);
            projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1F, .5F);
            worldIn.spawnEntity(projectile);
        }

        //playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);

        //Entity projectile = new EntityArsenalProjectile(worldIn, shooter) {};


        //return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    //{
    //    EntityArsenalProjectile projectile = new EntityArsenalProjectile(worldIn, shooter) {};
    //    return projectile;
    //}

}
