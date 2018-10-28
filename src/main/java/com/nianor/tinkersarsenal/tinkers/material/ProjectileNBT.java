package com.nianor.tinkersarsenal.tinkers.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

import java.util.List;

import static slimeknights.tconstruct.library.utils.Tags.DURABILITY;

public class ProjectileNBT extends ToolNBT {

    public static final String CALIBER = "Caliber";
    public static final String SPINFORCE = "Spin Force";
    public static final String ENERGYTRANSFER = "Energy Transfer";
    public static final String LENGTH = "Length";
    public static final String SLOPE = "Slope";
    public static final String POWDEREFFICIENCY = "Powder Efficiency";
    public static final String POWDERVOLUME = "Powder Volume";
    public static final String POWDERBURNRATE = "Powder Burn Rate";
    public static final String WEIGHT = "Weight";
    public static final String HARDNESS = "Hardness";
    public static final String JACKETHARDNESS = "Jacket Hardness";
    public static final String DAMAGE = "Damage";

    public float caliber;
    public float spinForce;
    public float energyTransfer;
    public float length;
    public float slope;
    public float powderEfficiency;
    public float powderVolume;
    public float powderBurnRate;
    public float weight;
    public float hardness;
    public float jacketHardness;
    public int damage;

    public ProjectileNBT() { }

    public ProjectileNBT(NBTTagCompound tag)
    {
        read(tag);
    }


    public ProjectileNBT core(List<Integer> materialIngots, CoreMaterialStats... cores)
    {
        int i = 0;
        for (CoreMaterialStats core : cores)
        {
            this.durability += core.durability;
            this.weight += core.weight*materialIngots.get(i);
        }

        return this;
    }

    public ProjectileNBT face(List<Integer> materialIngots, FaceMaterialStats... faces)
    {
        int i = 0;
        for (FaceMaterialStats stat : faces)
        {
            this.hardness += stat.hardness;
            this.durability += stat.durability;
            this.weight += stat.weight*materialIngots.get(i);
        }

        return this;
    }

    public ProjectileNBT jacket(List<Integer> materialIngots, FaceMaterialStats... faces) {
        int i = 0;
        for (FaceMaterialStats stat : faces)
        {
            this.hardness += stat.hardness;
            this.durability += stat.durability;
            this.weight += stat.weight*materialIngots.get(i);
        }

        return this;
    }

    public ProjectileNBT powder(float volume, float burnRate, float efficiency) {
        this.powderVolume=volume;
        this.powderBurnRate=burnRate;
        this.powderEfficiency=efficiency;
        return this;
    }


    @Override
    public void read(NBTTagCompound tag)
    {
        super.read(tag);
        this.weight = tag.getFloat(WEIGHT);
        this.durability= tag.getInteger(DURABILITY);
        this.hardness=tag.getFloat(HARDNESS);
        this.caliber=tag.getFloat(CALIBER);
        this.spinForce=tag.getFloat(SPINFORCE);
        this.energyTransfer=tag.getFloat(ENERGYTRANSFER);
        this.length=tag.getFloat(LENGTH);
        this.slope=tag.getFloat(SLOPE);
        this.powderEfficiency=tag.getFloat(POWDEREFFICIENCY);
        this.powderVolume=tag.getFloat(POWDERVOLUME);
        this.powderBurnRate=tag.getFloat(POWDERBURNRATE);
        this.jacketHardness=tag.getFloat(JACKETHARDNESS);
        this.damage=tag.getInteger(DAMAGE);
    }

    @Override
    public void write(NBTTagCompound tag)
    {
        super.write(tag);
        tag.setFloat(WEIGHT, this.weight);
        tag.setInteger(DURABILITY, this.durability);
        tag.setFloat(HARDNESS, this.hardness);
        tag.setFloat(CALIBER, this.caliber);
        tag.setFloat(SPINFORCE, this.spinForce);
        tag.setFloat(ENERGYTRANSFER, this.energyTransfer);
        tag.setFloat(LENGTH, this.length);
        tag.setFloat(SLOPE, this.slope);
        tag.setFloat(POWDEREFFICIENCY, this.powderEfficiency);
        tag.setFloat(POWDERVOLUME, this.powderVolume);
        tag.setFloat(POWDERBURNRATE, this.powderBurnRate);
        tag.setFloat(JACKETHARDNESS, this.jacketHardness);
        tag.setInteger(DAMAGE, this.damage);
    }

    public static ProjectileNBT from(ItemStack itemStack)
    {
        return new ProjectileNBT(TagUtil.getToolTag(itemStack));
    }
}
