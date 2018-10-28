package com.nianor.tinkersarsenal.tinkers.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import scala.tools.nsc.doc.model.Trait;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.swing.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.utils.Tags.DURABILITY;

public class MechanicalNBT extends ToolNBT {
    public static final String MANUAL = "Manual";
    public static final String MODEL = "Model";
    public static final String CALIBER = "Caliber";
    public static final String CASE_DIAMETER = "Case Diameter";
    public static final String CASE_LENGTH = "Case Length";
    public static final String CLEANLINESS = "Cleanliness";
    public static final String SPEED = "Speed";
    public static final String FORCE = "Force";
    public static final String DIRTINESS = "Dirtiness";
    public static final String WEIGHT = "Weight";
    public static final String MECH_EFFECTIVENESS = "Mechanical Effectiveness";
    public static final String HARDNESS = "Hardness";
    public static final String DAMAGE = "Damage";
    public static final String COMPLEXITY = "Complexity";
    public static final String ROUND_LENGTH = "Round Length";
    public static final String HEAT = "Heat";

    public boolean manual;
    public String model;
    public float caliber;
    public float round_length;
    public float case_length;
    public float case_diameter;
    public float cleanliness;
    public float speed;
    public float force;
    public float dirtiness;
    public float weight;
    public float mech_effectiveness;
    public float hardness;
    public int damage;
    public float complexity;
    public int heat;

    public MechanicalNBT () { }

    public MechanicalNBT (NBTTagCompound tag)
    {
        read(tag);
    }


    public MechanicalNBT core(List<Integer> materialIngots, CoreMaterialStats... cores)
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

    public MechanicalNBT face(List<Integer> materialIngots, FaceMaterialStats... faces)
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

    public MechanicalNBT mech(ArrayList<Integer> materialIngots, MechMaterialStats... mechs)
    {
        int i = 0;
        for (MechMaterialStats stat : mechs)
        {
            this.weight += stat.weight*materialIngots.get(i);
            this.mech_effectiveness+= stat.mech_effectiveness;
            this.durability += stat.durability;
        }

        return this;
    }

    public MechanicalNBT general(/*float setCaliber, float setCaseDiameter, float setCaseLength, float setRoundLength, */float setSpeed, float setForce, float setCleanliness, float setComplexity) {
        //caliber = setCaliber;
        //case_diameter = setCaseDiameter;
        //case_length=setCaseLength;
        //round_length=setRoundLength;
        speed=setSpeed;
        force=setForce;
        cleanliness=setCleanliness;
        complexity=setComplexity;
        dirtiness=0;
        heat = 0;
        return this;
    }

    public MechanicalNBT action(boolean setManual) {
        manual=setManual;
        return this;
    }

    //public MechanicalNBT feed() {
//
    //}

    //public MechanicalNBT receiver () {
//
    //}

    @Override
    public void read(NBTTagCompound tag)
    {
        super.read(tag);
        this.weight = tag.getFloat(WEIGHT);
        this.mech_effectiveness = tag.getFloat(MECH_EFFECTIVENESS);
        this.durability= tag.getInteger(DURABILITY);
        this.damage=tag.getInteger(DAMAGE);
        this.caliber = tag.getFloat(CALIBER);
        this.case_diameter  = tag.getFloat(CASE_DIAMETER);
        this.case_length = tag.getFloat(CASE_LENGTH);
        this.round_length = tag.getFloat(ROUND_LENGTH);
        this.speed = tag.getFloat(SPEED);
        this.force = tag.getFloat(FORCE);
        this.cleanliness = tag.getFloat(CLEANLINESS);
        this.complexity = tag.getFloat(COMPLEXITY);
        this.dirtiness = tag.getFloat(DIRTINESS);
        this.heat = tag.getInteger(HEAT);
    }

    @Override
    public void write(NBTTagCompound tag)
    {
        super.write(tag);
        tag.setFloat(WEIGHT, this.weight);
        tag.setFloat(MECH_EFFECTIVENESS, this.mech_effectiveness);
        tag.setInteger(DURABILITY, this.durability);
        tag.setFloat(CALIBER, this.caliber);
        tag.setFloat(CASE_DIAMETER, this.case_diameter );
        tag.setFloat(CASE_LENGTH, this.case_length);
        tag.setFloat(ROUND_LENGTH, this.round_length);
        tag.setFloat(SPEED, this.speed);
        tag.setFloat(FORCE, this.force);
        tag.setFloat(CLEANLINESS, this.cleanliness);
        tag.setFloat(COMPLEXITY, this.complexity);
        tag.setFloat(DIRTINESS, this.dirtiness);
        tag.setInteger(HEAT, this.heat);
    }

    public static MechanicalNBT from(ItemStack itemStack)
    {
        return new MechanicalNBT(TagUtil.getToolTag(itemStack));
    }
}
