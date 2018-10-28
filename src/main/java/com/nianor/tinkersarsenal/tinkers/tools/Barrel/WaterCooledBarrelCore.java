package com.nianor.tinkersarsenal.tinkers.tools.Barrel;

import com.nianor.tinkersarsenal.CommonProxy;
import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.tinkers.material.*;
import com.nianor.tinkersarsenal.tinkers.tools.Action.ActionCore;
import com.nianor.tinkersarsenal.tinkers.tools.Barrel.BarrelCore;
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

public class WaterCooledBarrelCore extends BarrelCore {

    public WaterCooledBarrelCore () {
        super(ArsenalPartMaterialTypes.core(CommonProxy.LARGE_TUBING), ArsenalPartMaterialTypes.core(CommonProxy.LARGE_TUBING), ArsenalPartMaterialTypes.face(CommonProxy.STURDY_TUBING));
    }
    @Override
    protected BarrelNBT buildTagData(List<Material> materials) {
        ArrayList<Integer> coreMaterials = new ArrayList<Integer>();
        ArrayList<Integer> faceMaterials = new ArrayList<Integer>();

        coreMaterials.add(CommonProxy.LARGE_TUBING.getCost());
        coreMaterials.add(CommonProxy.LARGE_TUBING.getCost());
        faceMaterials.add(CommonProxy.STURDY_TUBING.getCost());

        CoreMaterialStats large_tubing = materials.get(0).getStatsOrUnknown(ArsenalMaterialTypes.CORE);
        CoreMaterialStats large_tubing2 = materials.get(1).getStatsOrUnknown(ArsenalMaterialTypes.CORE);
        FaceMaterialStats sturdy_tubing = materials.get(2).getStatsOrUnknown(ArsenalMaterialTypes.FACE);
        BarrelNBT data = new BarrelNBT();
        data.core(coreMaterials, large_tubing);
        data.core(coreMaterials, large_tubing2);
        data.face(faceMaterials, sturdy_tubing);
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

        BarrelNBT nbt = BarrelNBT.from(stack);
        info.addDurability(!detailed);
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



    public float getWeight(ItemStack watercooledbarrel)
    {
        return BarrelNBT.from(watercooledbarrel).weight;
    }

    public float getRifling(ItemStack watercooledbarrel)
    {
        return BarrelNBT.from(watercooledbarrel).rifling;
    }

    public int getDurability(ItemStack watercooledbarrel)
    {
        return BarrelNBT.from(watercooledbarrel).durability;
    }

    public int getDamage(ItemStack watercooledbarrel)
    {
        return BarrelNBT.from(watercooledbarrel).damage;
    }

    public float getHardness(ItemStack watercooledbarrel)
    {
        return BarrelNBT.from(watercooledbarrel).hardness;
    }

    public void damageItem(ItemStack watercooledbarrel, EntityLivingBase player, int damage)
    {
        ToolHelper.damageTool(watercooledbarrel, damage, player);
    }

    @SideOnly(Side.CLIENT)
    public int getCoreColor(ItemStack watercooledbarrel)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(watercooledbarrel));
        return materials.get(0).materialTextColor;
    }

    @SideOnly(Side.CLIENT)
    public int getFaceColor(ItemStack watercooledbarrel)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(watercooledbarrel));
        return materials.get(1).materialTextColor;
    }

}
