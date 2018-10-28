package com.nianor.tinkersarsenal.tinkers.tools.Action;

import com.nianor.tinkersarsenal.CommonProxy;
import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.tinkers.material.*;
import com.nianor.tinkersarsenal.tinkers.tools.Action.ActionCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

import java.util.ArrayList;
import java.util.List;

public class BlowbackCore extends ActionCore {

    public static boolean manual = false;

    public BlowbackCore() {
        super(ArsenalPartMaterialTypes.core(CommonProxy.SHAFT), ArsenalPartMaterialTypes.face(CommonProxy.STURDY_TUBING), ArsenalPartMaterialTypes.mech(CommonProxy.HEAVY_SPRING));
    }
    @Override
    protected MechanicalNBT buildTagData(List<Material> materials) {
        ArrayList<Integer> coreMaterials = new ArrayList<Integer>();
        ArrayList<Integer> faceMaterials = new ArrayList<Integer>();
        ArrayList<Integer> mechMaterials = new ArrayList<Integer>();

        coreMaterials.add(CommonProxy.SHAFT.getCost());
        faceMaterials.add(CommonProxy.STURDY_TUBING.getCost());
        mechMaterials.add(CommonProxy.HEAVY_SPRING.getCost());

        CoreMaterialStats shaft = materials.get(0).getStatsOrUnknown(ArsenalMaterialTypes.CORE);
        FaceMaterialStats sturdy_tubing = materials.get(1).getStatsOrUnknown(ArsenalMaterialTypes.FACE);
        MechMaterialStats heavy_spring = materials.get(2).getStatsOrUnknown(ArsenalMaterialTypes.MECH);
        MechanicalNBT data = new MechanicalNBT();
        data.core(coreMaterials, shaft);
        data.face(faceMaterials, sturdy_tubing);
        data.mech(mechMaterials, heavy_spring);
        data.general(5F, 5F, 5F, -0.3F);
        data.action(false);
        return data;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }
    @Override
    public List<String> getInformation(ItemStack stack, boolean detailed)
    {
        TooltipBuilder info = new TooltipBuilder(stack);

        MechanicalNBT nbt = MechanicalNBT.from(stack);
        info.addDurability(!detailed);
        info.add(MechMaterialStats.formatWeight(nbt.mech_effectiveness));
/*
        if (nbt.duration < 0)
            info.add(String.format("%s: %s%s", Util.translate(YoyoNBT.LOC_Duration), AxleMaterialStats.COLOR_Friction, Util.translate(YoyoNBT.LOC_Infinite)) + TextFormatting.RESET);
        else
            info.add(String.format("%s: %s%s %s", Util.translate(YoyoNBT.LOC_Duration), AxleMaterialStats.COLOR_Friction, Util.df.format(nbt.duration / 20F), Util.translate(YoyoNBT.LOC_Suffix)) + TextFormatting.RESET);
*/
        info.add(CoreMaterialStats.formatWeight(nbt.weight));

        if (ToolHelper.getFreeModifiers(stack) > 0)
            info.addFreeModifiers();

        if (detailed)
            info.addModifierInfo();

        return info.getTooltip();
    }


    @Override
    @SideOnly(Side.CLIENT)
    public Material getMaterialForPartForGuiRendering(int index)
    {
        switch(index)
        {
            case 0: return ClientProxy.RenderMaterialString;
            case 1: return ClientProxy.RenderMaterials[2];
            case 2: return ClientProxy.RenderMaterials[1];
            case 3: return ClientProxy.RenderMaterials[0];
            default: return super.getMaterialForPartForGuiRendering(index);
        }
    }



    public float getWeight(ItemStack blowback)
    {
        return MechanicalNBT.from(blowback).weight;
    }

    public float getMech(ItemStack blowback)
    {
        return MechanicalNBT.from(blowback).mech_effectiveness;
    }

    public int getDurability(ItemStack blowback)
    {
        return MechanicalNBT.from(blowback).durability;
    }

    public int getDamage(ItemStack blowback)
    {
        return MechanicalNBT.from(blowback).damage;
    }

    public float getHardness(ItemStack blowback)
    {
        return MechanicalNBT.from(blowback).hardness;
    }

    public void damageItem(ItemStack blowback, EntityLivingBase player, int damage)
    {
        ToolHelper.damageTool(blowback, damage, player);
    }

    @SideOnly(Side.CLIENT)
    public int getCoreColor(ItemStack blowback)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(blowback));
        return materials.get(0).materialTextColor;
    }

    @SideOnly(Side.CLIENT)
    public int getFaceColor(ItemStack blowback)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(blowback));
        return materials.get(1).materialTextColor;
    }

    @SideOnly(Side.CLIENT)
    public int getMechColor(ItemStack blowback)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(blowback));
        return materials.get(2).materialTextColor;
    }





}
