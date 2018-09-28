package com.nianor.tinkersarsenal.tinkers.tools.Projectiles;

import com.nianor.tinkersarsenal.CommonProxy;
import com.nianor.tinkersarsenal.tinkers.material.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

import java.util.ArrayList;
import java.util.List;

public class BulletCore extends ProjectileCore{

    public BulletCore () {
        super(ArsenalPartMaterialTypes.core(CommonProxy.SHAFT), ArsenalPartMaterialTypes.face(CommonProxy.SHAFT), ArsenalPartMaterialTypes.face(CommonProxy.STURDY_TUBING));
    }
    @Override
    protected ProjectileNBT buildTagData(List<Material> materials) {
        ArrayList<Integer> coreMaterials = new ArrayList<Integer>();
        ArrayList<Integer> faceMaterials = new ArrayList<Integer>();
        ArrayList<Integer> jacketMaterials = new ArrayList<Integer>();

        coreMaterials.add(CommonProxy.SHAFT.getCost());
        faceMaterials.add(CommonProxy.SHAFT.getCost());
        jacketMaterials.add(CommonProxy.STURDY_TUBING.getCost());

        CoreMaterialStats shaft = materials.get(0).getStatsOrUnknown(ArsenalMaterialTypes.CORE);
        FaceMaterialStats shaft2 = materials.get(1).getStatsOrUnknown(ArsenalMaterialTypes.FACE);
        FaceMaterialStats sturdy_tubing = materials.get(2).getStatsOrUnknown(ArsenalMaterialTypes.MECH);
        ProjectileNBT data = new ProjectileNBT();
        data.core(coreMaterials, shaft);
        data.face(faceMaterials, shaft2);
        data.jacket(jacketMaterials, sturdy_tubing);
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

        ProjectileNBT nbt = ProjectileNBT.from(stack);
        info.addDurability(!detailed);
        info.add(FaceMaterialStats.formatWeight(nbt.caliber));
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



    public float getWeight(ItemStack bullet)
    {
        return ProjectileNBT.from(bullet).weight;
    }

    public float getCaliber(ItemStack bullet)
    {
        return ProjectileNBT.from(bullet).caliber;
    }

    public int getDurability(ItemStack bullet)
    {
        return ProjectileNBT.from(bullet).durability;
    }

    public int getDamage(ItemStack bullet)
    {
        return ProjectileNBT.from(bullet).damage;
    }

    public float getHardness(ItemStack bullet)
    {
        return ProjectileNBT.from(bullet).hardness;
    }

    public void damageItem(ItemStack bullet, EntityLivingBase player, int damage)
    {
        ToolHelper.damageTool(bullet, damage, player);
    }

    @SideOnly(Side.CLIENT)
    public int getCoreColor(ItemStack bullet)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(bullet));
        return materials.get(0).materialTextColor;
    }

    @SideOnly(Side.CLIENT)
    public int getFaceColor(ItemStack bullet)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(bullet));
        return materials.get(1).materialTextColor;
    }

    @SideOnly(Side.CLIENT)
    public int getJacketColor(ItemStack bullet)
    {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(bullet));
        return materials.get(2).materialTextColor;
    }





}