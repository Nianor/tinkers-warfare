package com.nianor.tinkersarsenal.tinkers.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.swing.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.nianor.tinkersarsenal.TinkersArsenal.logger;
import static slimeknights.tconstruct.library.utils.Tags.DURABILITY;

public class BarrelNBT extends ToolNBT {
    public static final String MANUAL = "Manual";
    public static final String MODEL = "Model";
    public static final String CALIBER = "Caliber";
    public static final String RIFLING = "Rifling";
    public static final String CLEANLINESS = "Cleanliness";
    public static final String DIRTINESS = "Dirtiness";
    public static final String WEIGHT = "Weight";
    public static final String HARDNESS = "Hardness";
    public static final String DAMAGE = "Damage";

    public boolean manual;
    public String model;
    public float caliber;
    public float cleanliness;
    public float dirtiness;
    public float weight;
    public float rifling;
    public float hardness;
    public int damage;

    public BarrelNBT() { }

    public BarrelNBT(NBTTagCompound tag)
    {
        read(tag);
    }


    public BarrelNBT core(List<Integer> materialIngots, CoreMaterialStats... cores)
    {
        int i = 0;
        for (CoreMaterialStats core : cores)
        {
            this.attack += core.attack;
            this.durability += core.durability;
            this.weight += core.weight*materialIngots.get(i);
        }

        return this;
    }

    public BarrelNBT face(List<Integer> materialIngots, FaceMaterialStats... faces)
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


    @Override
    public void read(NBTTagCompound tag)
    {
        super.read(tag);
        this.weight = tag.getFloat(WEIGHT);
        this.durability= tag.getInteger(DURABILITY);
        this.hardness=tag.getFloat(HARDNESS);
    }

    @Override
    public void write(NBTTagCompound tag)
    {
        super.write(tag);
        tag.setFloat(WEIGHT, this.weight);
        tag.setInteger(DURABILITY, this.durability);
        tag.setFloat(HARDNESS, this.hardness);
    }

    public static BarrelNBT from(ItemStack itemStack)
    {
        return new BarrelNBT(TagUtil.getToolTag(itemStack));
    }
}
