package com.nianor.tinkersarsenal.tinkers.material;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class FaceMaterialStats extends AbstractMaterialStats {

    public static final String LOC_Weight = "stat.body.weight.name";
    public static final String LOC_WeightDesc = "stat.body.weight.desc";

    public static final String COLOR_Weight = CustomFontColor.encodeColor(0, 255, 217);

    public final float hardness;
    public final float weight;
    public final int durability;

    public FaceMaterialStats(float hardness, float weight, int durability)
    {
        super(ArsenalMaterialTypes.FACE);
        this.hardness = hardness;
        this.weight = weight;
        this.durability = durability;
    }

    public static String formatWeight(float weight)
    {
        return formatNumber(LOC_Weight, COLOR_Weight, weight);
    }

    @Override
    public List<String> getLocalizedInfo() {
        return ImmutableList.of(HeadMaterialStats.formatAttack(this.hardness), formatWeight(this.weight), HeadMaterialStats.formatDurability(this.durability));
    }


    @Override
    public List<String> getLocalizedDesc()
    {
        return ImmutableList.of(Util.translate(HeadMaterialStats.LOC_AttackDesc), Util.translate(LOC_WeightDesc), Util.translate(HeadMaterialStats.LOC_DurabilityDesc));
    }


    @Nullable
    public static FaceMaterialStats deserialize(JsonObject material) throws JsonParseException
    {
        if (!JsonUtils.hasField(material, "body"))
            return null;

        JsonObject body = JsonUtils.getJsonObject(material, "body");

        float hardness = JsonUtils.getFloat(body, "hardness");
        float weight = JsonUtils.getFloat(body, "weight");
        int durability = JsonUtils.getInt(body, "durability");

        return new FaceMaterialStats(hardness, weight, durability);
    }


}
